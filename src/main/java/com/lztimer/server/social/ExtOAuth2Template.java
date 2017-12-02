package com.lztimer.server.social;

import org.springframework.social.google.api.oauth2.UserInfo;
import org.springframework.social.google.api.oauth2.impl.OAuth2Template;
import org.springframework.web.client.RestTemplate;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
public class ExtOAuth2Template extends OAuth2Template {
    private final String userInfoUrl;

    public ExtOAuth2Template(String userInfoUrl, RestTemplate restTemplate, boolean authorized) {
        super(restTemplate, authorized);
        this.userInfoUrl = userInfoUrl;
    }

    @Override
    public UserInfo getUserinfo() {
        return getEntity(userInfoUrl, UserInfo.class);
    }
}
