package com.lztimer.server.config;

import com.lztimer.server.security.Http401UnauthorizedEntryPoint;
import com.lztimer.server.security.JWTConfigurer;
import com.lztimer.server.security.TokenProvider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Order(1)
    @Configuration
    @RequiredArgsConstructor
    class RestSecurity extends WebSecurityConfigurerAdapter {

        private final TokenProvider tokenProvider;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .headers()
                    .frameOptions()
                    .disable()
                    .and()
                    .csrf()
                    .disable()
                    .antMatcher("/api/*")
                    .authorizeRequests().anyRequest().authenticated()
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .apply(securityConfigurerAdapter());
        }

        private JWTConfigurer securityConfigurerAdapter() {
            return new JWTConfigurer(tokenProvider);
        }
    }

    @AllArgsConstructor
    @Order(2)
    @Configuration
    public static class WebConfiguration extends WebSecurityConfigurerAdapter {

        private final RedirectingAuthenticationSuccessHandler authenticationSuccessHandler;

        @Override
        public void configure(WebSecurity web) {
            web.ignoring()
                    .antMatchers(HttpMethod.OPTIONS, "/**")
                    .antMatchers("/app/**/*.{js,html}")
                    .antMatchers("/i18n/**")
                    .antMatchers("/content/**")
                    .antMatchers("/swagger-ui/index.html")
                    .antMatchers("/test/**")
                    .antMatchers("/h2/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
//            .exceptionHandling()
//            .authenticationEntryPoint(http401UnauthorizedEntryPoint())
                    .csrf()
                    .disable()
//            .headers()
//            .frameOptions()
//            .disable()
                    .authorizeRequests()
                    .antMatchers("/desktop/log_in").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .oauth2Login()
                    .successHandler(authenticationSuccessHandler);

        }
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Bean
    public Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint() {
        return new Http401UnauthorizedEntryPoint();
    }
}
