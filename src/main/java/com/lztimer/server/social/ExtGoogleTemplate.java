package com.lztimer.server.social;

import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.oauth2.OAuth2Operations;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
public class ExtGoogleTemplate extends GoogleTemplate {

    private String userInfoUrl;

    public ExtGoogleTemplate(String accessToken, String userInfoUrl) {
        super(accessToken);
        this.userInfoUrl = userInfoUrl;
    }

    @Override
    public OAuth2Operations oauth2Operations() {
        return new ExtOAuth2Template(userInfoUrl, getRestTemplate(), isAuthorized());
    }
}
