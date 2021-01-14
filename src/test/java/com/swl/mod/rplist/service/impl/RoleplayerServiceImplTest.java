package com.swl.mod.rplist.service.impl;

import com.swl.mod.rplist.dao.RoleplayerDao;
import com.swl.mod.rplist.enumerated.Playfield;
import com.swl.mod.rplist.model.Roleplayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.util.List;

import static com.swl.mod.rplist.enumerated.Playfield.BLUE_MOUNTAIN;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoleplayerServiceImplTest {

    @Mock
    private RoleplayerDao roleplayerDaoMock;

    @InjectMocks
    private RoleplayerServiceImpl roleplayerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMarkInSameInstance() {
        Roleplayer r11 = mockRoleplayer(11L, BLUE_MOUNTAIN, 1, false);
        Roleplayer r12 = mockRoleplayer(12L, BLUE_MOUNTAIN, 1, false);
        Roleplayer r13 = mockRoleplayer(13L, BLUE_MOUNTAIN, 1, false);
        Roleplayer r21 = mockRoleplayer(21L, BLUE_MOUNTAIN, 2, false);
        Roleplayer r22 = mockRoleplayer(22L, BLUE_MOUNTAIN, 2, false);
        Roleplayer r31 = mockRoleplayer(31L, BLUE_MOUNTAIN, 3, false);

        mockDaoRoleplayersInInstance(BLUE_MOUNTAIN, 1, asList(r11, r12, r13));
        mockDaoRoleplayersInInstance(BLUE_MOUNTAIN, 2, asList(r21, r22));
        mockDaoRoleplayersInInstance(BLUE_MOUNTAIN, 3, singletonList(r31));

        roleplayerService.markInSameInstance(BLUE_MOUNTAIN.getPlayfieldId(), asList(r11, r21, r31), mock(StopWatch.class));

        assertEquals(1, r11.getInstanceId().intValue());
        assertEquals(1, r12.getInstanceId().intValue());
        assertEquals(1, r13.getInstanceId().intValue());
        assertEquals(1, r21.getInstanceId().intValue());
        assertEquals(1, r22.getInstanceId().intValue());
        assertEquals(1, r31.getInstanceId().intValue());
    }

    private void mockDaoRoleplayersInInstance(Playfield playfield, Integer instanceId, List<Roleplayer> roleplayers) {
        when(roleplayerDaoMock.findAllByPlayfieldIdAndInstanceId(playfield.getPlayfieldId(), instanceId))
                .thenReturn(roleplayers);
    }

    private Roleplayer mockRoleplayer(Long id, Playfield playfield, Integer instanceId, boolean changedInstance) {
        Roleplayer roleplayer = new Roleplayer();
        roleplayer.setPlayerId(id);
        roleplayer.setNick(id.toString());
        roleplayer.setInstanceId(instanceId);
        roleplayer.setPlayfieldId(playfield.getPlayfieldId());
        if (changedInstance) {
            roleplayer.setEnteredInstanceAt(Instant.now());
        } else {
            roleplayer.setEnteredInstanceAt(Instant.now().minus(10, MINUTES));
        }
        return roleplayer;
    }
}
