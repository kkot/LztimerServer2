package com.lztimer.server.service;

import com.lztimer.server.entity.Authority;
import com.lztimer.server.entity.User;
import com.lztimer.server.repository.AuthorityRepository;
import com.lztimer.server.repository.UserRepository;
import com.lztimer.server.security.Authorities;
import com.lztimer.server.security.SecurityService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

    private final SecurityService securityService;

    public User createUser(String login, String firstName, String lastName, String email,
                           String imageUrl, String langKey) {
        User newUser = new User();
        newUser.setLogin(login);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setImageUrl(imageUrl);
        newUser.setLangKey(langKey);
        newUser.setAuthorities(new HashSet<>(Arrays.asList(authorityRepository.getOne(Authorities.USER.getName()))));
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user
     * @param lastName  last name of user
     * @param email     email id of user
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        userRepository.findOneByLogin(securityService.getCurrentUserLogin()).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setLangKey(langKey);
            user.setImageUrl(imageUrl);
            log.debug("Changed Information for User: {}", user);
        });
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities(Long id) {
        return userRepository.findOneWithAuthoritiesById(id);
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        return userRepository.findOneWithAuthoritiesByLogin(securityService.getCurrentUserLogin()).orElse(null);
    }

    /**
     * @return a list of all the authorities
     */
    public List<String> authorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }
}
