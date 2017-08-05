package com.lztimer.server.service

import com.lztimer.server.entity.Authority
import com.lztimer.server.entity.User
import com.lztimer.server.repository.AuthorityRepository
import com.lztimer.server.repository.UserRepository
import com.lztimer.server.security.AuthoritiesConstants
import com.lztimer.server.security.SecurityService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.HashSet
import java.util.Optional
import java.util.stream.Collectors

/**
 * Service class for managing users.
 */
@Service
@Transactional
class UserService(
        private val userRepository: UserRepository,
        private val authorityRepository: AuthorityRepository,
        private val securityService: SecurityService) {

    private val log = LoggerFactory.getLogger(UserService::class.java)

    fun createUser(login: String, password: String, firstName: String, lastName: String, email: String,
                   imageUrl: String, langKey: String): User {
        val newUser = User()
        newUser.login = login
        newUser.firstName = firstName
        newUser.lastName = lastName
        newUser.email = email
        newUser.imageUrl = imageUrl
        newUser.langKey = langKey
        newUser.authorities = setOf(authorityRepository.getOne(AuthoritiesConstants.USER.roleName))
        userRepository.save(newUser)
        log.debug("Created Information for User: {}", newUser)
        return newUser
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.

     * @param firstName first name of user
     * @param lastName last name of user
     * @param email email id of user
     */
    fun updateUser(firstName: String, lastName: String, email: String, langKey: String, imageUrl: String) {
        userRepository.findOneByLogin(securityService.currentUserLogin).ifPresent { user ->
            user.firstName = firstName
            user.lastName = lastName
            user.email = email
            user.langKey = langKey
            user.imageUrl = imageUrl
            log.debug("Changed Information for User: {}", user)
        }
    }

    @Transactional(readOnly = true)
    fun getUserWithAuthoritiesByLogin(login: String): Optional<User> {
        return userRepository.findOneWithAuthoritiesByLogin(login)
    }

    @Transactional(readOnly = true)
    fun getUserWithAuthorities(id: Long?): User {
        return userRepository.findOneWithAuthoritiesById(id)
    }

    val userWithAuthorities: User
        @Transactional(readOnly = true)
        get() = userRepository.findOneWithAuthoritiesByLogin(securityService.currentUserLogin).orElse(null)


    /**
     * @return a list of all the authorities
     */
    val authorities: List<String>
        get() {
            return authorityRepository.findAll().map { it.name }
        }
}
