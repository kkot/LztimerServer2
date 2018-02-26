package com.lztimer.server.config;

import com.lztimer.server.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Component
@AllArgsConstructor
public class RedirectingAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private UserService userService;

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        if (request.getRequestURI().contains("/login/oauth2/code/desktop_")) {
            return "/desktop/logged_in";
        }
        if (!userService.getLoggedUserWeb().isPresent()) {
            return "/web/log_in";
        }
        return super.determineTargetUrl(request, response);
    }
}
