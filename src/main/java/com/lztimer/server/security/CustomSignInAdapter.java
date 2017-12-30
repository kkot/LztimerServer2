package com.lztimer.server.security;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.Cookie;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomSignInAdapter implements SignInAdapter {

    private final AuthenticationService authenticationService;

    private final TokenProvider tokenProvider;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Override
    public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = authenticationService.authenticate(userId);
            String jwt = tokenProvider.createToken(authenticationToken, false);
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            servletWebRequest.getResponse().addCookie(getSocialAuthenticationCookie(jwt));
            servletWebRequest.getResponse().addCookie(createCookie("port", "" + getPortFromSession(request)));
        } catch (AuthenticationException ae) {
            log.error("Social authentication error");
            log.trace("Authentication exception trace: {}", ae);
        }
        return "signin_completed";
    }

    private Cookie getSocialAuthenticationCookie(String token) {
        return createCookie("social-authentication", token);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(10);
        return cookie;
    }

    private int getPortFromSession(NativeWebRequest request) {
        Object port = sessionStrategy.getAttribute(request, "port");
        if (!(port instanceof Integer)) {
            throw new IllegalStateException();
        }
        return (int) port;
    }
}
