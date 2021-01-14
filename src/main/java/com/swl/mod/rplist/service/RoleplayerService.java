package com.swl.mod.rplist.service;

import com.swl.mod.rplist.dto.PlayfieldDto;
import com.swl.mod.rplist.dto.UpdateRoleplayerDto;

import java.util.SortedSet;

/**
 * Service for roleplayer list related logic (for SWLRP mod). Keeps track of who is in which zone and instance.
 */
public interface RoleplayerService {

    SortedSet<PlayfieldDto> getAll(boolean includeUnknownZones);

    void update(UpdateRoleplayerDto updateRoleplayerDto);

    void remove(Long playerId);

}
