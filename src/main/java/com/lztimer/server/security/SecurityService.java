package com.lztimer.server.security;

import org.springframework.stereotype.Service;

/**
 * Currently it is wrapper for {@link SecurityUtils} provided by JHipster.
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Service
public class SecurityService {

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user
     */
    public String getCurrentUserLogin() {
        return SecurityUtils.getCurrentUserLogin();
    }
}
