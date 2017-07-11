package com.lztimer.server.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lztimer.server.config.Constants
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.validator.constraints.Email

import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import java.io.Serializable
import java.util.HashSet

/**
 * A user.
 */
@Entity
@Table(name = "jhi_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
class User : AbstractAuditingEntity(), Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    var id: Long? = null

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 100)
    @Column(length = 100, unique = true, nullable = false)
    var login: String? = null

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    var firstName: String? = null

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    var lastName: String? = null

    @Email
    @Size(min = 5, max = 100)
    @Column(length = 100, unique = true)
    var email: String? = null

    @Size(min = 2, max = 5)
    @Column(name = "lang_key", length = 5)
    var langKey: String? = null

    @Size(max = 256)
    @Column(name = "image_url", length = 256)
    var imageUrl: String? = null

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "jhi_user_authority", joinColumns = arrayOf(JoinColumn(name = "user_id", referencedColumnName = "id")), inverseJoinColumns = arrayOf(JoinColumn(name = "authority_name", referencedColumnName = "name")))
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @BatchSize(size = 20)
    var authorities: Set<Authority>? = HashSet()



    protected fun canEqual(other: Any): Boolean {
        return other is User
    }

    override fun toString(): String {
        return "com.lztimer.server.entity.User(id=" + this.id + ", login=" + this.login + ", firstName=" + this.firstName + ", lastName=" + this.lastName + ", email=" + this.email + ", langKey=" + this.langKey + ", imageUrl=" + this.imageUrl + ", authorities=" + this.authorities + ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as User

        if (login != other.login) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (email != other.email) return false
        if (langKey != other.langKey) return false
        if (imageUrl != other.imageUrl) return false
        if (authorities != other.authorities) return false

        return true
    }

    override fun hashCode(): Int {
        var result = login?.hashCode() ?: 0
        result = 31 * result + (firstName?.hashCode() ?: 0)
        result = 31 * result + (lastName?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (langKey?.hashCode() ?: 0)
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + (authorities?.hashCode() ?: 0)
        return result
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
