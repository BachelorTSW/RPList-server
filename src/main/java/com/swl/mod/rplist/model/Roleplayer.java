package com.swl.mod.rplist.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;

@RedisHash("roleplayers")
public class Roleplayer {

    @Id
    @Indexed
    private Long playerId;

    private String nick;

    private String firstName;

    private String lastName;

    @Indexed
    private Integer playfieldId;

    @Indexed
    private Integer instanceId;

    private Instant enteredInstanceAt;

    private Boolean autoMeetup;

    @Indexed
    private Instant idleOutAt;

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

    public Integer getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Integer instanceId) {
        this.instanceId = instanceId;
    }

    public Instant getEnteredInstanceAt() {
        return enteredInstanceAt;
    }

    public void setEnteredInstanceAt(Instant enteredInstanceAt) {
        this.enteredInstanceAt = enteredInstanceAt;
    }

    public Boolean getAutoMeetup() {
        return autoMeetup;
    }

    public void setAutoMeetup(Boolean autoMeetup) {
        this.autoMeetup = autoMeetup;
    }

    public Instant getIdleOutAt() {
        return idleOutAt;
    }

    public void setIdleOutAt(Instant idleOutAt) {
        this.idleOutAt = idleOutAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Roleplayer that = (Roleplayer) o;

        return playerId != null ? playerId.equals(that.playerId) : that.playerId == null;
    }

    @Override
    public int hashCode() {
        return playerId != null ? playerId.hashCode() : 0;
    }
}
