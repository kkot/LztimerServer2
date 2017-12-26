package com.lztimer.server.webapi;

import com.lztimer.server.entity.Period;
import com.lztimer.server.repository.PeriodRepository;
import com.lztimer.server.service.UserService;
import com.lztimer.server.webapi.util.HeaderUtil;
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

import static org.hibernate.id.IdentifierGenerator.ENTITY_NAME;

@Slf4j
@RestController
@RequestMapping("/api")
public class PeriodController {

    private static final String ENTITY_NAME = "period";

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private UserService userService;

    /**
     * POST  /periods : Create a new period.
     *
     * @param period the period to create
     * @return the ResponseEntity with status 201 (Created) and with body the new period, or with status 400 (Bad Request) if the period has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/periods")
    public ResponseEntity<Period> createPeriod(@Valid @RequestBody Period period) throws URISyntaxException {
        log.debug("REST request to save Period : {}", period);
        if (period.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new period cannot already have an ID")).body(null);
        }
        period.setOwner(userService.getUserWithAuthorities());
        Period result = periodRepository.save(period);
        return ResponseEntity.created(new URI("/api/periods/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }
}
