package com.lztimer.server.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A UserSettings.
 */
@Entity
@Table(name = "user_settings")
public class UserSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "min_idle_time")
    private Integer minIdleTime;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMinIdleTime() {
        return minIdleTime;
    }

    public UserSettings minIdleTime(Integer minIdleTime) {
        this.minIdleTime = minIdleTime;
        return this;
    }

    public void setMinIdleTime(Integer minIdleTime) {
        this.minIdleTime = minIdleTime;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public UserSettings updatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public UserSettings user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserSettings userSettings = (UserSettings) o;
        if (userSettings.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userSettings.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserSettings{" +
            "id=" + getId() +
            ", minIdleTime='" + getMinIdleTime() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
