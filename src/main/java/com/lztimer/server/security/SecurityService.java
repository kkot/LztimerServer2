package com.lztimer.server.security;

import com.lztimer.server.entity.User;
import com.lztimer.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Currently it is wrapper for {@link SecurityUtils} provided by JHipster.
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@RequiredArgsConstructor
@Service
public class SecurityService {

    private final UserService userService;

    public String getLoggedUserId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return authentication.getName();
    }

    public String getLoggedUserEmail() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return SecurityUtils.getEmail((OAuth2AuthenticationToken) authentication);
    }

    @Transactional(readOnly = true)
    public Optional<User> getLoggedUserOpt() {
        // TODO: handle situation that uuid or email is not in DB, deleted account or some error
        Optional<UUID> userUuid = SecurityUtils.getLoggedUserUuid();
        if (userUuid.isPresent()) {
            return userService.getUserByUuid(userUuid.get());
        }
        Optional<String> userEmail = SecurityUtils.getLoggedUserEmail();
        if (userEmail.isPresent()) {
            return userService.getUserByEmail(userEmail.get());
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public User getLoggedUser() {
        return getLoggedUserOpt().orElseThrow(() -> new IllegalStateException());
    }


}
