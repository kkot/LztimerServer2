package com.lztimer.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Period.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "lz_period")
public class Period implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "begin_time", nullable = false)
    private Instant beginTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @JsonIgnore
    @ManyToOne(optional = false)
    private User owner;

    public Period(Instant beginTime, Instant endTime, Boolean active) {
        this(beginTime, endTime, active, null);
    }

    public Period(Instant beginTime, Instant endTime, Boolean active, User owner) {
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.active = active;
        this.owner = owner;
    }

    public Period(Period previous, Period next) {
        if (previous.isActive() != next.isActive()) {
            throw new IllegalStateException("Merging active with inactive");
        }
        this.beginTime = previous.beginTime;
        this.endTime = next.endTime;
        this.active = previous.active;
    }

    public Period beginTime(Instant beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public Period endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public Boolean isActive() {
        return active;
    }

    public Period active(Boolean active) {
        this.active = active;
        return this;
    }

    public Period owner(User user) {
        this.owner = user;
        return this;
    }

}
