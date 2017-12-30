package com.lztimer.server.config;

import com.lztimer.server.repository.CustomSocialUsersConnectionRepository;
import com.lztimer.server.repository.SocialUserConnectionRepository;
import com.lztimer.server.security.StateProvider;
import com.lztimer.server.service.SocialService;
import com.lztimer.server.social.ExtGoogleConnectionFactory;
import com.lztimer.server.webapi.DesktopSignInController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;

/**
 * Basic Spring Social configuration.
 * <p>
 * <p>
 * Creates the beans necessary to manage Connections to social services and
 * link accounts from those services to internal Users.
 */
@Configuration
@EnableSocial
public class SocialConfiguration implements SocialConfigurer {

    private final Logger log = LoggerFactory.getLogger(SocialConfiguration.class);

    private final SocialUserConnectionRepository socialUserConnectionRepository;

    private final Environment environment;

    private final StateProvider stateProvider;

    private final SocialProviders socialProviders;

    public SocialConfiguration(SocialUserConnectionRepository socialUserConnectionRepository,
                               Environment environment, StateProvider stateProvider, SocialProviders socialProviders) {

        this.socialUserConnectionRepository = socialUserConnectionRepository;
        this.environment = environment;
        this.stateProvider = stateProvider;
        this.socialProviders = socialProviders;
    }

    @Bean
    public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator,
                                               ConnectionRepository connectionRepository) {
        ConnectController controller = new ConnectController(connectionFactoryLocator, connectionRepository);
        controller.setApplicationUrl(environment.getProperty("spring.application.url"));
        return controller;
    }

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        // Google configuration
        String googleClientId = environment.getProperty("spring.social.google.app-id");
        String googleClientSecret = environment.getProperty("spring.social.google.app-secret");
        String authorizeUrl = environment.getProperty("spring.social.google.authorize-url");
        String accessTokenUrl = environment.getProperty("spring.social.google.access-token-url");
        String userInfoUrl = environment.getProperty("spring.social.google.user-info-url");

        if (googleClientId != null && googleClientSecret != null) {
            log.debug("Configuring GoogleConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                    new ExtGoogleConnectionFactory(
                            authorizeUrl,
                            accessTokenUrl,
                            userInfoUrl,
                            googleClientId,
                            googleClientSecret) {
                        @Override
                        public String generateState() {
                            return stateProvider.generateState();
                        }
                    }
            );
        } else {
            log.error("Cannot configure GoogleConnectionFactory id or secret null");
        }

        // Facebook configuration
        String facebookClientId = environment.getProperty("spring.social.facebook.client-id");
        String facebookClientSecret = environment.getProperty("spring.social.facebook.client-secret");
        if (facebookClientId != null && facebookClientSecret != null) {
            log.debug("Configuring FacebookConnectionFactory");
            connectionFactoryConfigurer.addConnectionFactory(
                    new FacebookConnectionFactory(
                            facebookClientId,
                            facebookClientSecret
                    )
            );
        } else {
            log.error("Cannot configure FacebookConnectionFactory id or secret null");
        }
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return new CustomSocialUsersConnectionRepository(socialUserConnectionRepository, connectionFactoryLocator);
    }

    @Bean
    public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator,
                                                             UsersConnectionRepository usersConnectionRepository,
                                                             SignInAdapter signInAdapter, SocialService socialService,
                                                             ProviderSignInUtils providerSignInUtils) {
        ProviderSignInController providerSignInController = new DesktopSignInController(connectionFactoryLocator,
                usersConnectionRepository, signInAdapter, socialService, providerSignInUtils, socialProviders);
        providerSignInController.setSignUpUrl("/signin/desktop/new_user");
        providerSignInController.setApplicationUrl(environment.getProperty("spring.application.url"));
        return providerSignInController;
    }

    @Bean
    public ProviderSignInUtils getProviderSignInUtils(ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository) {
        return new ProviderSignInUtils(connectionFactoryLocator, usersConnectionRepository);
    }
}
