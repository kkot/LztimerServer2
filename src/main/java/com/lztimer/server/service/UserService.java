package com.lztimer.server.service;

import com.lztimer.server.entity.Authority;
import com.lztimer.server.entity.User;
import com.lztimer.server.repository.AuthorityRepository;
import com.lztimer.server.repository.UserRepository;
import com.lztimer.server.security.Authorities;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
@AllArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    public User createUser(String email) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setAuthorities(new HashSet<>(Arrays.asList(getUserAuthority())));
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    Authority getUserAuthority() {
        return authorityRepository.findById(Authorities.USER.getName())
                .orElse(authorityRepository.save(new Authority(Authorities.USER.getName())));
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findOneWithAuthoritiesByEmail(email);
    }

    /**
     * @return a list of all the authorities
     */
    public List<String> authorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    public Optional<User> getUserByUuid(UUID uuid) {
        return userRepository.findOneWithAuthoritiesByUuid(uuid);
    }
}
