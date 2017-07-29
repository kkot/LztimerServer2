package com.lztimer.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("lztimer")
public class ConfigProperties {

    public JWT getJwt() {
        return jwt;
    }

    public static class JWT {
        private String secret;

        private Integer tokenValidityInSecondsForRememberMe;

        private Integer tokenValidityInSeconds;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Integer getTokenValidityInSecondsForRememberMe() {
            return tokenValidityInSecondsForRememberMe;
        }

        public void setTokenValidityInSecondsForRememberMe(Integer tokenValidityInSecondsForRememberMe) {
            this.tokenValidityInSecondsForRememberMe = tokenValidityInSecondsForRememberMe;
        }

        public Integer getTokenValidityInSeconds() {
            return tokenValidityInSeconds;
        }

        public void setTokenValidityInSeconds(Integer tokenValidityInSeconds) {
            this.tokenValidityInSeconds = tokenValidityInSeconds;
        }
    }

    private final JWT jwt = new JWT();
}
