package com.lztimer.server.repository;

import com.lztimer.server.entity.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for the Period entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PeriodRepository extends JpaRepository<Period, Long> {

    @Modifying
    @Query("delete from Period " +
            "where id in " +
            "(select period.id " +
            "   from Period period " +
            "   where period.owner.uuid = ?1 " +
            "     and period.beginTime >= ?2 and period.endTime <= ?3)")
    int deleteFromInterval(UUID userId, Instant startDate, Instant endDate);

    @Modifying
    @Query("select period from Period period " +
            " where period.owner.uuid = ?1 " +
            " and " +
            "   (period.beginTime > ?2 and period.beginTime < ?3) or " +
            "   (period.endTime   > ?2 and period.beginTime < ?3) ")
    List<Period> findPeriodsIntersectingInterval(UUID userId, Instant startDate, Instant endDate);

    @Query("select period from Period period " +
            "where period.owner.id = ?1 " +
            "and period.endTime >= ?2")
    List<Period> findEndedAfter(UUID userId, Instant dateTime);
}
