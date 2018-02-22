package com.swl.mod.rplist.service.impl;

import com.swl.mod.rplist.dao.RoleplayerDao;
import com.swl.mod.rplist.model.Roleplayer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.StopWatch;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class RoleplayerServiceImplTest {

    private static final String PLAYER_A_INSTANCE = "current-player-instance";
    private static final String PLAYER_B_INSTANCE = "yet-another-instance";
    private static final String SHARED_INSTANCE = "other-instance";
    private static final String ELSEWHERE_INSTANCE = "elsewhere-instance";

    private static final Integer PLAYFIELD_ID = 1000;
    private static final Integer OTHER_PLAYFIELD_ID = 2000;

    @InjectMocks
    private RoleplayerServiceImpl roleplayerService;

    @Mock
    private RoleplayerDao roleplayerDao;

    private Roleplayer playerA;
    private Roleplayer playerInSameInstance;
    private Roleplayer playerInSameInstanceFresh;
    private Roleplayer playerB;
    private Roleplayer playerElsewhere;

    @Before
    public void beforeMethod() {
        MockitoAnnotations.initMocks(this);
        LocalDateTime oldDateTime = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        playerA = mockRolePlayer(1L, PLAYFIELD_ID, PLAYER_A_INSTANCE, oldDateTime);
        playerB = mockRolePlayer(4L, PLAYFIELD_ID, PLAYER_B_INSTANCE, oldDateTime);
        playerInSameInstance = mockRolePlayer(2L, PLAYFIELD_ID, SHARED_INSTANCE, oldDateTime);
        playerInSameInstanceFresh = mockRolePlayer(3L, PLAYFIELD_ID, SHARED_INSTANCE, LocalDateTime.now());
        playerElsewhere = mockRolePlayer(5L, OTHER_PLAYFIELD_ID, ELSEWHERE_INSTANCE, oldDateTime);
    }

    @Test
    public void testMarkInSameInstance() {
        List<Roleplayer> players = asList(playerInSameInstance, playerInSameInstanceFresh, playerB, playerElsewhere);
        Set<Long> playerIds = players.stream()
                .map(Roleplayer::getId)
                .collect(Collectors.toSet());
        when(roleplayerDao.findAll(eq(playerIds))).thenReturn(players);
        when(roleplayerDao.findAll(eq(singleton(playerB.getId())))).thenReturn(singletonList(playerB));
        when(roleplayerDao.findByInstanceId(PLAYER_A_INSTANCE)).thenReturn(singletonList(playerA));
        when(roleplayerDao.findByInstanceId(PLAYER_B_INSTANCE)).thenReturn(singletonList(playerB));
        when(roleplayerDao.findByInstanceId(SHARED_INSTANCE)).thenReturn(asList(playerInSameInstance, playerInSameInstanceFresh));
        when(roleplayerDao.findByInstanceId(ELSEWHERE_INSTANCE)).thenReturn(singletonList(playerElsewhere));

        roleplayerService.markInSameInstance(playerA, playerIds, new StopWatch());

        assertEquals(SHARED_INSTANCE, playerA.getInstanceId());
        assertEquals(SHARED_INSTANCE, playerInSameInstance.getInstanceId());
        assertEquals(SHARED_INSTANCE, playerInSameInstanceFresh.getInstanceId());
        assertEquals(PLAYER_B_INSTANCE, playerB.getInstanceId());
        assertEquals(ELSEWHERE_INSTANCE, playerElsewhere.getInstanceId());
        assertEquals(OTHER_PLAYFIELD_ID, playerElsewhere.getPlayfieldId());

        roleplayerService.markInSameInstance(playerA, singleton(playerB.getId()), new StopWatch());

        assertEquals(SHARED_INSTANCE, playerA.getInstanceId());
        assertEquals(SHARED_INSTANCE, playerInSameInstance.getInstanceId());
        assertEquals(SHARED_INSTANCE, playerInSameInstanceFresh.getInstanceId());
        assertEquals(PLAYER_B_INSTANCE, playerB.getInstanceId());
        assertEquals(ELSEWHERE_INSTANCE, playerElsewhere.getInstanceId());
        assertEquals(OTHER_PLAYFIELD_ID, playerElsewhere.getPlayfieldId());
    }

    private Roleplayer mockRolePlayer(Long id, Integer playfieldId, String instanceId, LocalDateTime enteredInstanceAt) {
        Roleplayer roleplayer = new Roleplayer();
        roleplayer.setId(id);
        roleplayer.setPlayfieldId(playfieldId);
        roleplayer.setInstanceId(instanceId);
        roleplayer.setEnteredInstanceAt(enteredInstanceAt);
        return roleplayer;
    }

}
