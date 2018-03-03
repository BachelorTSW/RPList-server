package com.swl.mod.rplist.dto;

import com.swl.mod.rplist.model.Roleplayer;

public class RoleplayerDto {

    private Long playerId;

    private String nick;

    private String firstName;

    private String lastName;

    private Integer playfieldId;

    private Boolean autoMeetup;

    public RoleplayerDto() {
    }

    public RoleplayerDto(Roleplayer roleplayer) {
        this.playerId = roleplayer.getPlayerId();
        this.nick = roleplayer.getNick();
        this.firstName = roleplayer.getFirstName();
        this.lastName = roleplayer.getLastName();
        this.playfieldId = roleplayer.getPlayfieldId();
        this.autoMeetup = roleplayer.getAutoMeetup();
    }

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
}
