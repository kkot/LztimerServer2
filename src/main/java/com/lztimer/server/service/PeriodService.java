package com.lztimer.server.service;

import com.lztimer.server.entity.Period;
import com.lztimer.server.entity.User;
import com.lztimer.server.repository.PeriodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing Period.
 */
@Slf4j
@Service
@Transactional
public class PeriodService {

    private final PeriodRepository periodRepository;

    private final UserService userService;

    @Autowired
    public PeriodService(PeriodRepository periodRepository, UserService userService) {
        this.periodRepository = periodRepository;
        this.userService = userService;
    }

    /**
     * Save a period and set owner to logged user.
     *
     * @param period the entity to save
     * @return the persisted entity
     */
    public Period save(Period period) {
        log.debug("Request to save Period : {}", period);
        period.setOwner(userService.getUserWithAuthorities());
        return periodRepository.save(period);
    }

    /**
     * Adds {@code period} and deletes old period that are inside new period.
     */
    public Period addAndReplace(Period period) {
        User user = userService.getUserWithAuthorities();
        int deleted = periodRepository.deleteFromInterval(user.getLogin(), period.getBeginTime(), period.getEndTime());
        List<Period> intersectingInterval = periodRepository.findPeriodsIntersectingInterval(user.getLogin(),
                period.getBeginTime(), period.getEndTime());
        if (!intersectingInterval.isEmpty()) {
            reportIncorrectPeriodUpdater(intersectingInterval);
        }
        log.debug("deleted periods {}", deleted);
        return save(period);
    }

    void reportIncorrectPeriodUpdater(List<Period> intersectingInterval) {
        log.error("updating previous periods is incorrect {}", intersectingInterval);
    }
}
