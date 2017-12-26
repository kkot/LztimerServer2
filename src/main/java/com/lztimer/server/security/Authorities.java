package com.lztimer.server.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    public GrantedAuthority toGrantedAuthority() {
        return new SimpleGrantedAuthority(name);
    }
}

