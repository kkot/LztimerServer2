package com.lztimer.server.webapi.web;

import com.lztimer.server.entity.User;
import com.lztimer.server.security.SecurityService;
import com.lztimer.server.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Controller
@AllArgsConstructor
@RequestMapping("/web")
public class SignInController {

    private SecurityService securityService;

    private UserService userService;

    @GetMapping("/log_in")
    public String signIn() {
        String email = securityService.getLoggedUserEmail();
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (!userOpt.isPresent()) {
            userService.createUser(email);
        }
        return "redirect:/";
    }


}
