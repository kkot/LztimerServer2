package com.lztimer.server.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Authorities.
 */
@AllArgsConstructor
public enum Authorities {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    ANONYMOUS("ROLE_ANONYMOUS");

    @Getter
    private String name;
}

