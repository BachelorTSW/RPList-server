package com.swl.mod.rplist.dto;

import com.swl.mod.rplist.enumerated.Playfield;
import com.swl.mod.rplist.model.Roleplayer;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * DTO for SWLRP playfield instance - contains a list of all roleplayers who have the SWLRP mod.
 * Implements {@link Comparable} with regards to the amount of roleplayers in the instance.
 */
public class PlayfieldInstanceDto implements Comparable<PlayfieldInstanceDto> {

    private Playfield playfield;

    private int instanceNumber;

    private String instanceId;

    private List<Roleplayer> roleplayers;

    public PlayfieldInstanceDto(Playfield playfield, int instanceNumber, String instanceId, List<Roleplayer> roleplayers) {
        this.playfield = playfield;
        this.instanceNumber = instanceNumber;
        this.instanceId = instanceId;
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

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public List<Roleplayer> getRoleplayers() {
        return roleplayers;
    }

    public void setRoleplayers(List<Roleplayer> roleplayers) {
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

        return instanceId != null ? instanceId.equals(that.instanceId) : that.instanceId == null;
    }

    @Override
    public int hashCode() {
        return instanceId != null ? instanceId.hashCode() : 0;
    }

}
