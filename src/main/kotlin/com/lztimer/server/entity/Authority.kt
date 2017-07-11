package com.lztimer.server.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import java.io.Serializable

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@Table(name = "jhi_authority")
class Authority() : Serializable {
    constructor(name : String) : this() {
        this.name = name
    }

    @NotNull
    @Size(min = 0, max = 50)
    @Id
    @Column(length = 50)
    lateinit var name: String

    companion object {
        private const val serialVersionUID = 1L
    }
}
