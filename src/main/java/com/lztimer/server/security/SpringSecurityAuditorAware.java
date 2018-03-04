package com.lztimer.server.security;

import com.lztimer.server.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements AuditorAware<User> {
    private final SecurityService securityService;

    @Override
    public Optional<User> getCurrentAuditor() {
        return securityService.getLoggedUserOpt();
    }
}
