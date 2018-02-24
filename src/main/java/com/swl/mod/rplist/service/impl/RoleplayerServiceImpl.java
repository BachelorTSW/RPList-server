package com.swl.mod.rplist.service.impl;

import com.swl.mod.rplist.dao.RoleplayerDao;
import com.swl.mod.rplist.dto.PlayfieldDto;
import com.swl.mod.rplist.dto.PlayfieldInstanceDto;
import com.swl.mod.rplist.dto.UpdateRoleplayerDto;
import com.swl.mod.rplist.enumerated.Playfield;
import com.swl.mod.rplist.model.Roleplayer;
import com.swl.mod.rplist.service.RoleplayerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Service
public class RoleplayerServiceImpl implements RoleplayerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${rplist.player.ttlSeconds}")
    private Long playerTtl;

    @Autowired
    private RoleplayerDao roleplayerDao;

    @Override
    public SortedSet<PlayfieldDto> getAll(boolean includeUnknownZones) {
        Map<Integer, Map<String, List<Roleplayer>>> aggregated = StreamSupport.stream(roleplayerDao.findAll().spliterator(), false)
                .filter(Objects::nonNull)
                .collect(groupingBy(Roleplayer::getPlayfieldId, groupingBy(Roleplayer::getInstanceId)));

        SortedSet<PlayfieldDto> playfields = new TreeSet<>();
        for (Map.Entry<Integer, Map<String, List<Roleplayer>>> playfieldEntry : aggregated.entrySet()) {
            Playfield playfieldId = Playfield.fromId(playfieldEntry.getKey());
            if (playfieldId == null) {
                if (includeUnknownZones) {
                    playfieldId = Playfield.UNKNOWN;
                } else {
                    continue;
                }
            }
            PlayfieldDto playfield = new PlayfieldDto(playfieldId);
            int i = 0;
            for (Map.Entry<String, List<Roleplayer>> playfieldInstanceEntry : playfieldEntry.getValue().entrySet()) {
                PlayfieldInstanceDto playfieldInstance = new PlayfieldInstanceDto(playfieldId, ++i, playfieldInstanceEntry.getKey(), playfieldInstanceEntry.getValue());
                playfieldInstance.getRoleplayers().sort(Comparator.comparing(Roleplayer::getNick));
                playfield.getPlayfieldInstances().add(playfieldInstance);
                playfield.setRoleplayerCount(playfield.getRoleplayerCount() + playfieldInstance.getRoleplayers().size());
            }
            playfields.add(playfield);
        }

        return playfields;
    }

    @Override
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
            currentPlayer.setInstanceId(generateInstanceId());
            currentPlayer.setEnteredInstanceAt(LocalDateTime.now());
        }

        stopWatch.stop();

        if (logger.isDebugEnabled()) {
            logger.debug("Updating player {} ({}), zone {}, instanceId {}, players: {}", updateRoleplayerDto.getNick(),
                    updateRoleplayerDto.getPlayerId(), updateRoleplayerDto.getPlayfieldId(), currentPlayer.getInstanceId(),
                    updateRoleplayerDto.getPlayers() == null ? "" : updateRoleplayerDto.getPlayers().stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(",")));
        }

        if (Playfield.AGARTHA.getPlayfieldId() == updateRoleplayerDto.getPlayfieldId()) {
            currentPlayer.setInstanceId("agartha");
        } else if (updateRoleplayerDto.getPlayers() != null && !updateRoleplayerDto.getPlayers().isEmpty()) {
            markInSameInstance(currentPlayer, updateRoleplayerDto.getPlayers(), stopWatch);
        }

        currentPlayer.setAutoMeetup(updateRoleplayerDto.getAutoMeetup());

        stopWatch.start("Saving refreshed player");
        saveRefreshed(currentPlayer);
        stopWatch.stop();

        if (stopWatch.getTotalTimeMillis() > 500 && logger.isWarnEnabled()) {
            logger.warn("Roleplayer list update took {}s: {}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
        }
    }

    @Override
    public void remove(Long playerId) {
        synchronized (this) {
            roleplayerDao.delete(playerId);
        }
    }

    protected void markInSameInstance(Roleplayer currentPlayer, Set<Long> playerIds, StopWatch stopWatch) {
        stopWatch.start("Getting instanceIds of " + playerIds.size() + " players");
        LocalDateTime enteredInstanceThreshold = LocalDateTime.now().minus(2, ChronoUnit.MINUTES);
        Set<String> instanceIds = StreamSupport.stream(roleplayerDao.findAll(playerIds).spliterator(), false)
                .filter(p -> currentPlayer.getPlayfieldId().equals(p.getPlayfieldId()))
                .filter(p -> enteredInstanceThreshold.isAfter(p.getEnteredInstanceAt()))
                .map(Roleplayer::getInstanceId)
                .collect(toSet());
        instanceIds.add(currentPlayer.getInstanceId());
        stopWatch.stop();

        stopWatch.start("Getting all players in " + instanceIds.size() + " instances");
        Optional<Pair<String, Long>> largestInstance = instanceIds.stream()
                .map(instanceId -> Pair.of(instanceId, countRoleplayersInInstance(instanceId)))
                .reduce(this::getLargerInstance);
        if (!largestInstance.isPresent()) {
            logger.error("No instances could be found for the players {}", playerIds);
        } else if (!StringUtils.equals(currentPlayer.getInstanceId(), largestInstance.get().getKey())) {
            logger.info("Updating player {} ({}) - changing instanceId from {} to {} ({} players)",
                    currentPlayer.getNick(), currentPlayer.getId(), currentPlayer.getInstanceId(),
                    largestInstance.get().getKey(), largestInstance.get().getValue());
            currentPlayer.setInstanceId(largestInstance.get().getKey());
        }
        stopWatch.stop();
    }

    protected Long countRoleplayersInInstance(String instanceId) {
        return roleplayerDao.findByInstanceId(instanceId).stream()
                .filter(Objects::nonNull)
                .count();
    }

    protected Roleplayer createIfNotExists(Long playerId, String nick, String firstName, String lastName) {
        Roleplayer player = roleplayerDao.findOne(playerId);
        if (player == null) {
            player = new Roleplayer();
            player.setId(playerId);
            player.setNick(nick);
            player.setFirstName(firstName);
            player.setLastName(lastName);
            player.setInstanceId(generateInstanceId());
            player.setEnteredInstanceAt(LocalDateTime.now());
            player.setTimeToLive(playerTtl);
        }
        return player;
    }

    protected void saveRefreshed(Roleplayer roleplayer) {
        roleplayer.setTimeToLive(playerTtl);
        roleplayerDao.save(roleplayer);
    }

    protected String generateInstanceId() {
        return randomUUID().toString();
    }

    private Pair<String, Long> getLargerInstance(Pair<String, Long> a, Pair<String, Long> b) {
        if (a.getValue().equals(b.getValue())) {
            if (a.getKey().compareTo(b.getKey()) < 0) {
                return a;
            }
            return b;
        }
        return a.getValue() > b.getValue() ? a : b;
    }
}
