package com.swl.mod.rplist.service.impl;

import com.swl.mod.rplist.dao.RoleplayerDao;
import com.swl.mod.rplist.dto.PlayfieldDto;
import com.swl.mod.rplist.dto.PlayfieldInstanceDto;
import com.swl.mod.rplist.dto.RoleplayerDto;
import com.swl.mod.rplist.dto.UpdateRoleplayerDto;
import com.swl.mod.rplist.enumerated.Playfield;
import com.swl.mod.rplist.model.Roleplayer;
import com.swl.mod.rplist.service.RoleplayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.*;
import static java.util.stream.StreamSupport.stream;

@Service
public class RoleplayerServiceImpl implements RoleplayerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${rplist.player.ttlSeconds}")
    private Long playerTtl;

    @Autowired
    private RoleplayerDao roleplayerDao;

    private final SecureRandom random = new SecureRandom();

    @Override
    @Cacheable(value = "roleplayersList", sync = true)
    public SortedSet<PlayfieldDto> getAll(boolean includeUnknownZones) {
        Map<Playfield, Map<Integer, List<Roleplayer>>> roleplayersInPlayfields =
                stream(roleplayerDao.findAll().spliterator(), false)
                        .filter(Objects::nonNull)
                        .filter(roleplayer -> getPlayfield(roleplayer) != null || includeUnknownZones)
                        .collect(groupingBy(this::getPlayfield,
                                groupingBy(this::getInstanceForGrouping)));

        return roleplayersInPlayfields.entrySet().stream()
                .map(playfield -> toPlayfieldDto(playfield.getKey(), playfield.getValue()))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private Playfield getPlayfield(Roleplayer roleplayer) {
        Playfield playfield = Playfield.fromId(roleplayer.getPlayfieldId());
        return playfield == null ? Playfield.UNKNOWN : playfield;
    }

    private Integer getInstanceForGrouping(Roleplayer roleplayer) {
        if (Playfield.AGARTHA.equals(Playfield.fromId(roleplayer.getPlayfieldId()))) {
            return 1;
        }
        return roleplayer.getInstanceId();
    }

    private PlayfieldDto toPlayfieldDto(Playfield playfield, Map<Integer, List<Roleplayer>> instances) {
        PlayfieldDto playfieldDto = new PlayfieldDto(playfield);

        List<List<Roleplayer>> instancesOrderedBySize = instances.values().stream()
                .sorted(Comparator.comparing(List::size, reverseOrder()))
                .collect(toList());

        int i = 0;
        for (List<Roleplayer> instanceRoleplayers: instancesOrderedBySize) {
            PlayfieldInstanceDto playfieldInstance = new PlayfieldInstanceDto(playfield, ++i, instanceRoleplayers.stream()
                            .sorted(Comparator.comparing(Roleplayer::getNick))
                            .map(RoleplayerDto::new)
                            .collect(toList())
            );
            playfieldDto.getPlayfieldInstances().add(playfieldInstance);
            playfieldDto.setRoleplayerCount(playfieldDto.getRoleplayerCount() + playfieldInstance.getRoleplayers().size());
        }
        return playfieldDto;
    }

    @Override
//    @Transactional
    public void update(UpdateRoleplayerDto updateRoleplayerDto) {
        StopWatch stopWatch = new StopWatch("Updating roleplayer");

        boolean shouldClearInstance = updateRoleplayerDto.isClearInstance();

        stopWatch.start("Getting/creating player entity");
        Roleplayer currentPlayer = createIfNotExists(updateRoleplayerDto);
        stopWatch.stop();

        stopWatch.start("Updating player entity");
        // If the stored playfieldId is different than the current one, the player had to change zones since last update
        if (currentPlayer.getPlayfieldId() == null || !currentPlayer.getPlayfieldId().equals(updateRoleplayerDto.getPlayfieldId())) {
            shouldClearInstance = true;
        }
        currentPlayer.setPlayfieldId(updateRoleplayerDto.getPlayfieldId());
        if (shouldClearInstance) {
            currentPlayer.setInstanceId(random.nextInt());
            currentPlayer.setEnteredInstanceAt(Instant.now());
        }
        currentPlayer.setAutoMeetup(updateRoleplayerDto.getAutoMeetup());
        stopWatch.stop();

        stopWatch.start("Saving updated player entity");
        saveRefreshed(currentPlayer);
        stopWatch.stop();

        // Mark the player as in same instance as the supplied other players (but ignore in Agartha)
        if (!shouldClearInstance && Playfield.AGARTHA.getPlayfieldId() != updateRoleplayerDto.getPlayfieldId()
                && updateRoleplayerDto.getPlayers() != null && !updateRoleplayerDto.getPlayers().isEmpty()) {
            stopWatch.start("Compiling list of players to be marked as in same instance");
            Set<Roleplayer> visibleRoleplayers = new HashSet<>();
            visibleRoleplayers.add(currentPlayer);
            updateRoleplayerDto.getPlayers().stream()
                    .map(roleplayerDao::findByPlayerId)
                    .filter(Objects::nonNull)
                    .forEach(visibleRoleplayers::add);
            stopWatch.stop();
            markInSameInstance(currentPlayer.getPlayfieldId(), visibleRoleplayers, stopWatch);
        }

        if (stopWatch.getTotalTimeMillis() > 1000 && logger.isWarnEnabled()) {
            logger.warn("Roleplayer list update took {}s: {}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
        }
    }

    @Override
//    @Transactional
    public void remove(Long playerId) {
        roleplayerDao.deleteById(playerId);
    }

    @Override
//    @Transactional
    public int removeIdle() {
        Instant now = Instant.now();
        List<Roleplayer> idledOut = stream(roleplayerDao.findAll().spliterator(), false)
                .filter(Objects::nonNull)
                .filter(r -> r.getIdleOutAt().isBefore(now))
                .collect(toList());
        roleplayerDao.deleteAll(idledOut);
        return idledOut.size();
    }

    protected void markInSameInstance(Integer playfieldId, Collection<Roleplayer> roleplayers, StopWatch stopWatch) {
        stopWatch.start("Filtering players to be marked as in same instance");
        Instant enteredInstanceThreshold = Instant.now().minus(2, ChronoUnit.MINUTES);
        Set<Integer> instancesToMerge = roleplayers.stream()
                .filter(r -> playfieldId.equals(r.getPlayfieldId()))
                .filter(r -> enteredInstanceThreshold.isAfter(r.getEnteredInstanceAt()))
                .map(Roleplayer::getInstanceId)
                .collect(toSet());
        stopWatch.stop();

        if (instancesToMerge.size() < 2) {
            return;
        }

        stopWatch.start("Ascertaining largest instance");
        Integer largestInstance = findInstanceWithMostRoleplayers(playfieldId, instancesToMerge);
        instancesToMerge.remove(largestInstance);
        stopWatch.stop();

        stopWatch.start("Merging instances");
        mergeInstancesInto(playfieldId, largestInstance, instancesToMerge);
        stopWatch.stop();
    }

    private void mergeInstancesInto(Integer playfieldId, Integer targetInstance, Set<Integer> instancesToMerge) {
        List<Roleplayer> affectedRoleplayers = instancesToMerge.stream()
                .map(instance -> roleplayerDao.findAllByPlayfieldIdAndInstanceId(playfieldId, instance))
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(toList());
        affectedRoleplayers.forEach(r -> r.setInstanceId(targetInstance));
        roleplayerDao.saveAll(affectedRoleplayers);
    }

    private Integer findInstanceWithMostRoleplayers(Integer playfieldId, Set<Integer> instances) {
        Integer largestInstance = 0;
        long largestInstanceSize = 0;
        for (Integer instance: instances) {
            long instanceSize = roleplayerDao.findAllByPlayfieldIdAndInstanceId(playfieldId, instance).stream()
                    .filter(Objects::nonNull)
                    .count();
            if (instanceSize > largestInstanceSize) {
                largestInstance = instance;
                largestInstanceSize = instanceSize;
            }
        }
        return largestInstance;
    }

    private Roleplayer createIfNotExists(UpdateRoleplayerDto updateRoleplayerDto) {
        Roleplayer roleplayer = roleplayerDao.findByPlayerId(updateRoleplayerDto.getPlayerId());
        if (roleplayer == null) {
            roleplayer = new Roleplayer();
            roleplayer.setPlayerId(updateRoleplayerDto.getPlayerId());
            roleplayer.setEnteredInstanceAt(Instant.now());
        }

        roleplayer.setNick(updateRoleplayerDto.getNick());
        roleplayer.setFirstName(updateRoleplayerDto.getFirstName());
        roleplayer.setLastName(updateRoleplayerDto.getLastName());

        return roleplayer;
    }

    private void saveRefreshed(Roleplayer roleplayer) {
        Instant idleOutAt = Instant.now().plus(playerTtl, ChronoUnit.SECONDS);
        roleplayer.setIdleOutAt(idleOutAt);
        roleplayerDao.save(roleplayer);
    }

}
