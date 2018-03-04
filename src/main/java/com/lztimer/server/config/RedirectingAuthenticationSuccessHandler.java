package com.lztimer.server.config;

import com.lztimer.server.security.SecurityService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RedirectingAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SecurityService securityService;

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        if (request.getRequestURI().contains("/login/oauth2/code/desktop_")) {
            return "/desktop/logged_in";
        }
        if (!securityService.getLoggedUserOpt().isPresent()) {
            return "/web/log_in";
        }
        return super.determineTargetUrl(request, response);
    }
}
