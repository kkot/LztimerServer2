package com.lztimer.server.repository;

import com.lztimer.server.entity.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for the Period entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PeriodRepository extends JpaRepository<Period,Long> {

    @Query("select period from Period period where period.owner.login = ?#{principal.username}")
    List<Period> findByOwnerIsCurrentUser();

    @Query("select period from Period period " +
        "where period.owner.login = ?1 " +
        "and period.endTime >= ?2")
    List<Period> findEndedAfter(String userLogin, Instant dateTime);
}
