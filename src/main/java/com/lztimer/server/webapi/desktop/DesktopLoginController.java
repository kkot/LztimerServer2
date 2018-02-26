package com.lztimer.server.webapi.desktop;

import com.lztimer.server.entity.User;
import com.lztimer.server.security.SecurityUtils;
import com.lztimer.server.security.TokenProvider;
import com.lztimer.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/desktop")
public class DesktopLoginController {

    private final TokenProvider tokenProvider;

    private final UserService userService;

    @GetMapping("/log_in")
    public String logIn(@RequestParam("port") String port, HttpSession session) {
        session.setAttribute("port", Integer.parseInt(port));
        return "redirect:/oauth2/authorization/desktop_google";
    }

    @GetMapping("/logged_in")
    public ModelAndView loggedIn(Authentication authentication, ServletWebRequest servletWebRequest, HttpSession session) {
        if (authentication instanceof OAuth2LoginAuthenticationToken) {
            throw new IllegalStateException("Not authenticated");
        }
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String email = SecurityUtils.getEmail(token);
        Optional<User> userOpt = userService.getUserByEmail(email);
        User user;
        if (!userOpt.isPresent()) {
            log.info("Creating user from authentication {}", authentication);
            user = userService.createUser(email);
            log.info("Created user {}", user);
        } else {
            user = userOpt.get();
            log.info("User existed {}", user);
        }
        String jwt = tokenProvider.createToken(user.getUuid(), authentication, true);
        servletWebRequest.getResponse().addCookie(createSocialAuthenticationCookie(jwt));
        servletWebRequest.getResponse().addCookie(createCookie("port", String.valueOf(getPortFromSession(session))));
        return new ModelAndView("/signin_completed.html");
    }

    private Cookie createSocialAuthenticationCookie(String token) {
        return createCookie("social-authentication", token);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(10);
        return cookie;
    }

    private int getPortFromSession(HttpSession session) {
        Object port = session.getAttribute( "port");
        if (!(port instanceof Integer)) {
            throw new IllegalStateException();
        }
        return (int) port;
    }
}
