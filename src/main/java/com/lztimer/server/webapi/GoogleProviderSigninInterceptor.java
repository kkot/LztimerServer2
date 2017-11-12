package com.lztimer.server.webapi;

import com.lztimer.server.config.SocialProviders;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ProviderSignInInterceptor;
import org.springframework.social.google.api.Google;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
public class GoogleProviderSigninInterceptor implements ProviderSignInInterceptor<Google> {

    private final SocialProviders socialProviders;

    public GoogleProviderSigninInterceptor(SocialProviders socialProviders) {
        this.socialProviders = socialProviders;
    }

    @Override
    public void preSignIn(ConnectionFactory<Google> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request) {
        parameters.add("scope", socialProviders.getScope("google"));
    }

    @Override
    public void postSignIn(Connection<Google> connection, WebRequest request) {

    }
}
