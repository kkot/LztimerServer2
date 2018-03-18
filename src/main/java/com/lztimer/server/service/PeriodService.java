package com.lztimer.server.service;

import com.lztimer.server.dto.DailyUsage;
import com.lztimer.server.entity.Period;
import com.lztimer.server.entity.User;
import com.lztimer.server.repository.PeriodRepository;
import com.lztimer.server.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service Implementation for managing Period.
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PeriodService {

    private final PeriodRepository periodRepository;

    private final SecurityService securityService;

    private final JdbcTemplate jdbcTemplate;

    /**
     * Save a period and set owner to logged user.
     *
     * @param period the entity to save
     * @return the persisted entity
     */
    public Period save(Period period) {
        log.debug("Request to save Period : {}", period);
        period.setOwner(securityService.getLoggedUser());
        return periodRepository.save(period);
    }

    /**
     * Adds {@code period} and deletes old period that are inside new period.
     */
    public Period addAndReplace(Period period) {
        User user = securityService.getLoggedUser();
        int deleted = periodRepository.deleteFromInterval(user.getUuid(), period.getBeginTime(), period.getEndTime());
        List<Period> intersectingInterval = periodRepository.findPeriodsIntersectingInterval(user.getUuid(),
                period.getBeginTime(), period.getEndTime());
        if (!intersectingInterval.isEmpty()) {
            reportIncorrectPeriodUpdater(period, intersectingInterval);
        }
        log.debug("deleted periods {}", deleted);
        return save(period);
    }

    void reportIncorrectPeriodUpdater(Period period, List<Period> intersectingInterval) {
        log.error("current period {}, old intersecting intervals {}", period, intersectingInterval);
    }

    public List<DailyUsage> getDailyUsages(UUID userId) {
        var sql = "select begin_time::date as beg_date, task, sum(round(extract(epoch from (end_time - begin_time)))) as secs  " +
                "from lz_period where owner_uuid = ? " +
                "group by begin_time::date, task " +
                "order by begin_time::date desc";
        return jdbcTemplate.query(sql,
                new Object[]{userId},
                (rs, rowNum) -> new DailyUsage(
                        rs.getString("beg_date"),
                        rs.getString("task"),
                        rs.getLong("secs"))
        );
    }

}
