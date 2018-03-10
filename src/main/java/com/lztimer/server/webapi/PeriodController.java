package com.lztimer.server.webapi;

import com.lztimer.server.dto.PeriodList;
import com.lztimer.server.entity.Period;
import com.lztimer.server.entity.User;
import com.lztimer.server.security.SecurityService;
import com.lztimer.server.service.PeriodService;
import com.lztimer.server.service.UserService;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class PeriodController {

    private static final String ENTITY_NAME = "period";

    @Autowired
    private PeriodService periodService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    /**
     * POST  /periods : Create a new period.
     *
     * @param periods the period to create
     * @return the ResponseEntity with status 201 (Created) and with body the new period, or with status 400 (Bad Request) if the period has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/periods")
    public ResponseEntity<PeriodList> updatePeriods(@Valid @RequestBody PeriodList periods) throws URISyntaxException {
        log.debug("REST request to update Periods : {}", periods);

        var ids = new ArrayList<Long>();
        User user = securityService.getLoggedUser();
        periods.getPeriods().forEach(p -> {
            p.setOwner(user);
            Period saved = periodService.addAndReplace(p);
            ids.add(saved.getId());
        });

        var urls = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",","/api/periods/",""));
        return ResponseEntity.created(new URI(urls)).build();
    }

}
