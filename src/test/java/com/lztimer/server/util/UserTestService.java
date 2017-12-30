package com.lztimer.server.util;

import com.lztimer.server.entity.User;
import com.lztimer.server.repository.UserRepository;
import com.lztimer.server.service.AuthorityService;
import com.lztimer.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Creates users for tests.
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Service
public class UserTestService {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityService authorityService;

    /**
     * Creates and saves new user with login given as argument.
     *
     * @param login user login
     * @return new user
     */
    public User createUser(String login) {
        authorityService.addStandard();
        return userService.createUser(login, "John", "Doe",
                login + "@localhost", "http://placehold.it/50x50", "en-US");
    }
}
