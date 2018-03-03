package com.swl.mod.rplist.dto;

import com.swl.mod.rplist.enumerated.Playfield;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * DTO for SWLRP playfield instance - contains a list of all roleplayers who have the SWLRP mod.
 * Implements {@link Comparable} with regards to the amount of roleplayers in the instance.
 */
public class PlayfieldInstanceDto implements Comparable<PlayfieldInstanceDto> {

    private Playfield playfield;

    private int instanceNumber;

    private List<RoleplayerDto> roleplayers;

    public PlayfieldInstanceDto(Playfield playfield, int instanceNumber, List<RoleplayerDto> roleplayers) {
        this.playfield = playfield;
        this.instanceNumber = instanceNumber;
        this.roleplayers = roleplayers;
    }

    public Playfield getPlayfield() {
        return playfield;
    }

    public void setPlayfield(Playfield playfield) {
        this.playfield = playfield;
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(int instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public List<RoleplayerDto> getRoleplayers() {
        return roleplayers;
    }

    public void setRoleplayers(List<RoleplayerDto> roleplayers) {
        this.roleplayers = roleplayers;
    }

    @Override
    public int compareTo(@Nonnull PlayfieldInstanceDto o) {
        if (o.roleplayers.size() < roleplayers.size()) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlayfieldInstanceDto that = (PlayfieldInstanceDto) o;

        return instanceNumber == that.instanceNumber && playfield == that.playfield;
    }

    @Override
    public int hashCode() {
        int result = playfield != null ? playfield.hashCode() : 0;
        result = 31 * result + instanceNumber;
        return result;
    }
}
