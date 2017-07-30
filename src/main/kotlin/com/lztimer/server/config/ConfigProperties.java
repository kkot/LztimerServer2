package com.lztimer.server.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("lztimer")
public class ConfigProperties {

    @Data
    public static class Token {
        private String secret;

        private Integer tokenValidityInSecondsForRememberMe;

        private Integer tokenValidityInSeconds;
    }

    private final Token token = new Token();
}
