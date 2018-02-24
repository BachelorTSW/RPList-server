package com.swl.mod.rplist.dto;

import com.swl.mod.rplist.enumerated.Playfield;

import javax.annotation.Nonnull;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * DTO for SWLRP playfield - contains a set of {@link PlayfieldInstanceDto}s.
 * Implements {@link Comparable} with regards to how important the playfield is.
 */
public class PlayfieldDto implements Comparable<PlayfieldDto> {

    private Playfield playfield;

    private SortedSet<PlayfieldInstanceDto> playfieldInstances = new TreeSet<>();

    private int roleplayerCount;

    public PlayfieldDto(Playfield playfield) {
        this.playfield = playfield;
    }

    public Playfield getPlayfield() {
        return playfield;
    }

    public void setPlayfield(Playfield playfield) {
        this.playfield = playfield;
    }

    public SortedSet<PlayfieldInstanceDto> getPlayfieldInstances() {
        return playfieldInstances;
    }

    public void setPlayfieldInstances(SortedSet<PlayfieldInstanceDto> playfieldInstances) {
        this.playfieldInstances = playfieldInstances;
    }

    public int getRoleplayerCount() {
        return roleplayerCount;
    }

    public void setRoleplayerCount(int roleplayerCount) {
        this.roleplayerCount = roleplayerCount;
    }

    @Override
    public int compareTo(@Nonnull PlayfieldDto o) {
        return Integer.compare(playfield.getPriority(), o.getPlayfield().getPriority());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PlayfieldDto that = (PlayfieldDto) o;

        return playfield == that.playfield;
    }

    @Override
    public int hashCode() {
        return playfield != null ? playfield.hashCode() : 0;
    }
}
