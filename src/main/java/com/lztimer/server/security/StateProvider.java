package com.lztimer.server.security;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Component
public class StateProvider {

    public String generateState() {
        return UUID.randomUUID().toString();
    }
}
