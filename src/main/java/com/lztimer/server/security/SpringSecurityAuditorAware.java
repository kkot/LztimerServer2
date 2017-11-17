package com.lztimer.server.security;

import com.lztimer.server.config.Constants;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String userName = SecurityUtils.getCurrentUserLogin();
        if (userName != null) {
            return Optional.of(userName);
        }
        return Optional.of(Constants.SYSTEM_ACCOUNT);
    }
}
