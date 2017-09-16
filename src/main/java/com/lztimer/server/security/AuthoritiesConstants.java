package com.lztimer.server.security;

/**
 * Constants for Spring Security authorities.
 */
public enum AuthoritiesConstants {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    ANONYMOUS("ROLE_ANONYMOUS");

    private String name;
    AuthoritiesConstants(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

