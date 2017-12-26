package com.lztimer.server.service;

import com.lztimer.server.entity.Authority;
import com.lztimer.server.repository.AuthorityRepository;
import com.lztimer.server.security.Authorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Component
public class AuthorityService {

    @Autowired
    private AuthorityRepository repository;

    public void addStandard() {
        Arrays.stream(Authorities.values())
                .forEach(authorityEnum -> repository.save(new Authority(authorityEnum.getName())));
    }

}
