package com.lztimer.server.social;

import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleAdapter;
import org.springframework.social.oauth2.AccessGrant;

/**
 * Google ConnectionFactory implementation.
 *
 * @author Gabriel Axel
 */
public class ExtGoogleConnectionFactory extends OAuth2ConnectionFactory<Google> {

    public ExtGoogleConnectionFactory(
            final String authorizeUrl, final String accessTokenUrl,
            final String userInfoUrl,
            final String clientId, final String clientSecret) {
        super("google", new ExtGoogleServiceProvider(authorizeUrl, accessTokenUrl, userInfoUrl,
                        clientId, clientSecret),
                new GoogleAdapter());
    }

    @Override
    protected String extractProviderUserId(final AccessGrant accessGrant) {
        final Google api = ((ExtGoogleServiceProvider) getServiceProvider()).getApi(accessGrant.getAccessToken());
        final UserProfile userProfile = getApiAdapter().fetchUserProfile(api);
        return userProfile.getUsername();
    }
}