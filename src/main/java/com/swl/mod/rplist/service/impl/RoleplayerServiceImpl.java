package com.swl.mod.rplist.service.impl;

import com.swl.mod.rplist.dao.RoleplayerDao;
import com.swl.mod.rplist.dto.PlayfieldDto;
import com.swl.mod.rplist.dto.PlayfieldInstanceDto;
import com.swl.mod.rplist.dto.RoleplayerDto;
import com.swl.mod.rplist.dto.UpdateRoleplayerDto;
import com.swl.mod.rplist.enumerated.Playfield;
import com.swl.mod.rplist.model.Roleplayer;
import com.swl.mod.rplist.model.RoleplayersInSameInstance;
import com.swl.mod.rplist.service.RoleplayerService;
import org.neo4j.ogm.exception.OptimisticLockingException;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class RoleplayerServiceImpl implements RoleplayerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${rplist.player.ttlSeconds}")
    private Long playerTtl;

    @Autowired
    private RoleplayerDao roleplayerDao;

    @Override
    @Cacheable(value = "roleplayersList")
    @Transactional(readOnly = true)
    public SortedSet<PlayfieldDto> getAll(boolean includeUnknownZones) {
        Map<Integer, List<Set<Roleplayer>>> aggregated = new HashMap<>();
        List<RoleplayersInSameInstance> roleplayersInSameInstance = roleplayerDao.getRoleplayersInSameInstance();
        for (RoleplayersInSameInstance instance : roleplayersInSameInstance) {
            aggregated.putIfAbsent(instance.root.getPlayfieldId(), new ArrayList<>());
            List<Set<Roleplayer>> playfield = aggregated.get(instance.root.getPlayfieldId());
            boolean alreadyAssigned = playfield.stream()
                    .anyMatch(dimension -> dimension.contains(instance.root));
            if (!alreadyAssigned) {
                Set<Roleplayer> dimension = new HashSet<>(instance.inSameInstance);
                dimension.add(instance.root);
                playfield.add(dimension);
            }
        }
        for (Roleplayer roleplayer : roleplayerDao.getRoleplayersInEmptyInstances()) {
            aggregated.putIfAbsent(roleplayer.getPlayfieldId(), new ArrayList<>());
            List<Set<Roleplayer>> playfield = aggregated.get(roleplayer.getPlayfieldId());
            if (Playfield.AGARTHA.equals(Playfield.fromId(roleplayer.getPlayfieldId()))) {
                if (playfield.isEmpty()) {
                    playfield.add(new HashSet<>());
                }
                playfield.get(0).add(roleplayer);
            } else {
                playfield.add(Collections.singleton(roleplayer));
            }
        }

        SortedSet<PlayfieldDto> playfields = new TreeSet<>();
        for (Map.Entry<Integer, List<Set<Roleplayer>>> playfieldEntry : aggregated.entrySet()) {
            Playfield playfieldId = Playfield.fromId(playfieldEntry.getKey());
            if (playfieldId == null) {
                if (includeUnknownZones) {
                    playfieldId = Playfield.UNKNOWN;
                } else {
                    continue;
                }
            }
            PlayfieldDto playfield = new PlayfieldDto(playfieldId);
            playfieldEntry.getValue().sort(Comparator.comparing(Set::size));
            int i = 0;
            for (Set<Roleplayer> instanceRoleplayers : playfieldEntry.getValue()) {
                PlayfieldInstanceDto playfieldInstance = new PlayfieldInstanceDto(
                        playfieldId,
                        ++i,
                        instanceRoleplayers.stream()
                                .sorted(Comparator.comparing(Roleplayer::getNick))
                                .map(RoleplayerDto::new)
                                .collect(toList())
                );
                playfield.getPlayfieldInstances().add(playfieldInstance);
                playfield.setRoleplayerCount(playfield.getRoleplayerCount() + playfieldInstance.getRoleplayers().size());
            }
            playfields.add(playfield);
        }

        return playfields;
    }

    @Override
    @Transactional
    @Retryable(include = OptimisticLockingException.class, backoff = @Backoff(10))
    public void update(UpdateRoleplayerDto updateRoleplayerDto) {
        StopWatch stopWatch = new StopWatch("Updating roleplayer");
        stopWatch.start("Updating player");
        boolean shouldClearInstance = updateRoleplayerDto.isClearInstance();

        Roleplayer currentPlayer = createIfNotExists(updateRoleplayerDto.getPlayerId(), updateRoleplayerDto.getNick(),
                updateRoleplayerDto.getFirstName(), updateRoleplayerDto.getLastName());

        // If the stored playfield id is different than the current one, then the player had to change zones since last update
        if (currentPlayer.getPlayfieldId() == null || !currentPlayer.getPlayfieldId().equals(updateRoleplayerDto.getPlayfieldId())) {
            shouldClearInstance = true;
        }
        currentPlayer.setPlayfieldId(updateRoleplayerDto.getPlayfieldId());

        if (shouldClearInstance) {
            if (currentPlayer.getRoleplayersInSameInstance() != null) {
                currentPlayer.getRoleplayersInSameInstance().clear();
            }
            currentPlayer.setEnteredInstanceAt(Instant.now());
        }

        currentPlayer.setAutoMeetup(updateRoleplayerDto.getAutoMeetup());

        stopWatch.stop();

        Set<Roleplayer> visibleRoleplayers = new HashSet<>();
        visibleRoleplayers.add(currentPlayer);
        // Mark the player as in same instance as the supplied other players (but ignore in Agartha)
        if (!shouldClearInstance && Playfield.AGARTHA.getPlayfieldId() != updateRoleplayerDto.getPlayfieldId()
                && updateRoleplayerDto.getPlayers() != null && !updateRoleplayerDto.getPlayers().isEmpty()) {
            visibleRoleplayers.addAll(roleplayerDao.findAllByPlayerIdIn(updateRoleplayerDto.getPlayers()));
            markInSameInstance(currentPlayer.getPlayfieldId(), visibleRoleplayers, stopWatch);
        }

        stopWatch.start("Saving refreshed players");
        saveRefreshed(visibleRoleplayers);
        stopWatch.stop();

        if (stopWatch.getTotalTimeMillis() > 500 && logger.isWarnEnabled()) {
            logger.warn("Roleplayer list update took {}s: {}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
        }
    }

    @Override
    @Transactional
    @Retryable(include = OptimisticLockingException.class, backoff = @Backoff(10))
    public void remove(Long playerId) {
        roleplayerDao.deleteByPlayerId(playerId);
    }

    @Override
    @Transactional
    @Retryable(include = OptimisticLockingException.class, backoff = @Backoff(10))
    public int removeIdle() {
        List<Roleplayer> idledOut = roleplayerDao.findAllByIdleOutAtBefore(Instant.now());
        roleplayerDao.deleteAll(idledOut);
        return idledOut.size();
    }

    protected void markInSameInstance(Integer playfieldId, Set<Roleplayer> roleplayers, StopWatch stopWatch) {
        stopWatch.start("Marking players to be in same instance");
        Instant enteredInstanceThreshold = Instant.now().minus(2, ChronoUnit.MINUTES);
        List<Roleplayer> validRoleplayers = roleplayers.stream()
                .filter(x -> playfieldId.equals(x.getPlayfieldId()))
                .filter(x -> enteredInstanceThreshold.isAfter(x.getEnteredInstanceAt()))
                .collect(toList());
        for (Roleplayer roleplayer : validRoleplayers) {
            for (Roleplayer other : validRoleplayers) {
                if (roleplayer.equals(other)) {
                    continue;
                }
                if (roleplayer.getRoleplayersInSameInstance() == null) {
                    roleplayer.setRoleplayersInSameInstance(new HashSet<>());
                }
                roleplayer.getRoleplayersInSameInstance().add(other);
            }
        }
        stopWatch.stop();
    }

    private Roleplayer createIfNotExists(Long playerId, String nick, String firstName, String lastName) {
        Roleplayer roleplayer = roleplayerDao.findByPlayerId(playerId);
        if (roleplayer == null) {
            roleplayer = new Roleplayer();
            roleplayer.setPlayerId(playerId);
            roleplayer.setEnteredInstanceAt(Instant.now());
        }

        roleplayer.setNick(nick);
        roleplayer.setFirstName(firstName);
        roleplayer.setLastName(lastName);

        return roleplayerDao.save(roleplayer);
    }

    private void saveRefreshed(Set<Roleplayer> roleplayers) {
        Instant idleOutAt = Instant.now().plus(playerTtl, ChronoUnit.SECONDS);
        roleplayers.forEach(roleplayer -> roleplayer.setIdleOutAt(idleOutAt));
        roleplayerDao.saveAll(roleplayers);
    }

}
