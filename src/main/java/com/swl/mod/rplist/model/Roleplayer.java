package com.swl.mod.rplist.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@RedisHash("roleplayers")
public class Roleplayer {

    @Id
    private Long id;

    private String nick;

    private String firstName;

    private String lastName;

    @Indexed
    private Integer playfieldId;

    @Indexed
    private String instanceId;

    private LocalDateTime enteredInstanceAt;

    private Boolean autoMeetup;

    @TimeToLive
    private Long timeToLive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public LocalDateTime getEnteredInstanceAt() {
        return enteredInstanceAt;
    }

    public void setEnteredInstanceAt(LocalDateTime enteredInstanceAt) {
        this.enteredInstanceAt = enteredInstanceAt;
    }

    public Boolean getAutoMeetup() {
        return autoMeetup;
    }

    public void setAutoMeetup(Boolean autoMeetup) {
        this.autoMeetup = autoMeetup;
    }

    public Long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(Long timeToLive) {
        this.timeToLive = timeToLive;
    }

}
