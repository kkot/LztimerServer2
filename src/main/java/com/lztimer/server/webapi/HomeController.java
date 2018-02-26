package com.lztimer.server.webapi;

import com.lztimer.server.entity.User;
import com.lztimer.server.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Controller
@AllArgsConstructor
public class HomeController {

    private final UserService userService;

    @RequestMapping("/")
    public String welcome(Model model) {
        User user = userService.getLoggedUserWeb().get();
        model.addAttribute("user", user);
        return "index";
    }
}
