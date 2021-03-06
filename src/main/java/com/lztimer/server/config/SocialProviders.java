package com.lztimer.server.config;

import org.springframework.stereotype.Component;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Component
public class SocialProviders {

    public String getScope(String providerId) {
        if (providerId.equals("google")) {
            return "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
        }
        throw new IllegalArgumentException("Unknown provider");
    }
}
