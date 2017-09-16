package com.lztimer.server;

import com.lztimer.server.config.ConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class LztimerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LztimerServerApplication.class, args);
    }
}
