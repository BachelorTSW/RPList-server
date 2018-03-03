package com.swl.mod.rplist.model;

import org.neo4j.ogm.annotation.*;
import org.springframework.data.annotation.Version;

import java.time.Instant;
import java.util.Set;

@NodeEntity
public class Roleplayer {

    @Id
    @GeneratedValue
    private Long id;

    @Index(unique = true)
    private Long playerId;

    @Version
    private Long version;

    private String nick;

    private String firstName;

    private String lastName;

    private Integer playfieldId;

    @Relationship(type = "IN_SAME_INSTANCE", direction = Relationship.UNDIRECTED)
    private Set<Roleplayer> roleplayersInSameInstance;

    private Instant enteredInstanceAt;

    private Boolean autoMeetup;

    @Index
    private Instant idleOutAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    public Set<Roleplayer> getRoleplayersInSameInstance() {
        return roleplayersInSameInstance;
    }

    public void setRoleplayersInSameInstance(Set<Roleplayer> roleplayersInSameInstance) {
        this.roleplayersInSameInstance = roleplayersInSameInstance;
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
