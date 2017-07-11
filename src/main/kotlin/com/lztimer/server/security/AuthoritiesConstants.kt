package com.lztimer.server.security

/**
 * Constants for Spring Security authorities.
 */
enum class AuthoritiesConstants(val roleName : String) {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    ANONYMOUS("ROLE_ANONYMOUS");
}

