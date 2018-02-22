package com.swl.mod.rplist.dto;

import java.util.Set;

/**
 * DTO for roleplayer list update requests. Specifies which zone the player is in, whether a fresh instance should
 * be created for him (due to zoning in), and which other players he has met.
 */
public class UpdateRoleplayerDto {

    private Long playerId;

    private String nick;

    private String firstName;

    private String lastName;

    private Integer playfieldId;

    private Boolean autoMeetup;

    private boolean clearInstance;

    private Set<Long> players;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getPlayfieldId() {
        return playfieldId;
    }

    public void setPlayfieldId(Integer playfieldId) {
        this.playfieldId = playfieldId;
    }

    public Boolean getAutoMeetup() {
        return autoMeetup;
    }

    public void setAutoMeetup(Boolean autoMeetup) {
        this.autoMeetup = autoMeetup;
    }

    public boolean isClearInstance() {
        return clearInstance;
    }

    public void setClearInstance(boolean clearInstance) {
        this.clearInstance = clearInstance;
    }

    public Set<Long> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Long> players) {
        this.players = players;
    }

}
