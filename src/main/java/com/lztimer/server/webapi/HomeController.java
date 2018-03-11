package com.lztimer.server.webapi;

import com.lztimer.server.entity.User;
import com.lztimer.server.repository.PeriodRepository;
import com.lztimer.server.security.SecurityService;
import lombok.AllArgsConstructor;
import lombok.experimental.var;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@Controller
@AllArgsConstructor
public class HomeController {

    private final SecurityService securityService;

    private final PeriodRepository periodRepository;

    @RequestMapping("/")
    public String welcome(Model model) {
        User user = securityService.getLoggedUser();
        model.addAttribute("user", user);

        var yesterday = LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        model.addAttribute("periods",
                periodRepository.findEndedAfter(user.getUuid(), yesterday));
        return "index";
    }
}
