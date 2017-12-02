package com.lztimer.server.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(ServletWebServerFactoryAutoConfiguration.class)
public class ForceTomcatAutoConfiguration {

    @Bean
    TomcatServletWebServerFactory tomcat() {
         return new TomcatServletWebServerFactory();
    }
}