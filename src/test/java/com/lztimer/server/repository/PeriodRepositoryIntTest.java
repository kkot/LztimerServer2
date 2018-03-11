package com.lztimer.server.repository;

import com.lztimer.server.LztimerServerApplication;
import com.lztimer.server.entity.Period;
import com.lztimer.server.entity.User;
import com.lztimer.server.service.AuthorityService;
import com.lztimer.server.util.MovingClock;
import com.lztimer.server.util.UserTestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Integration test for {@link PeriodRepository}.
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LztimerServerApplication.class)
@Transactional
public class PeriodRepositoryIntTest {

    @Autowired
    private PeriodRepository periodRepositoryUnderTest;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private UserTestService userTestService;

    @Autowired
    private AuthorityService authorityService;

    private MovingClock clock;

    @Before
    public void setUp() {
        clock = new MovingClock(Instant.now());
        authorityService.addStandard();
    }

    @Test
    public void findEndedAfter_shouldOnlyReturnPeriodsAfterDate() {
        // arrange
        User user = userTestService.createUser("user1");
        Period beforePeriod = new Period(clock.getCurrent(), clock.shiftSeconds(1), false, user);
        Instant thresholdDate = clock.shiftSeconds(1);
        Period afterPeriod = new Period(clock.shiftSeconds(1), clock.shiftSeconds(1), false, user);

        periodRepositoryUnderTest.save(beforePeriod);
        periodRepositoryUnderTest.save(afterPeriod);

        // act
        List<Period> periodsAfter = periodRepositoryUnderTest.findEndedAfter(user.getUuid(), thresholdDate);

        // assert
        assertThat(periodsAfter, contains(afterPeriod));
    }

    @Test
    public void findEndedAfter_shouldOnlyReturnPeriodsFromUser() {
        // arrange
        User user1 = userTestService.createUser("user1");
        User user2 = userTestService.createUser("user2");
        Instant thresholdDate = clock.getCurrent();
        Period period1 = new Period(clock.getCurrent(), clock.shiftSeconds(1), false, user1);
        Period period2 = new Period(clock.shiftSeconds(1), clock.shiftSeconds(1), false, user2);

        periodRepositoryUnderTest.save(period1);
        periodRepositoryUnderTest.save(period2);

        // act
        List<Period> periodsUser1 = periodRepositoryUnderTest.findEndedAfter(user1.getUuid(), thresholdDate);
        List<Period> periodsUser2 = periodRepositoryUnderTest.findEndedAfter(user2.getUuid(), thresholdDate);

        // assert
        assertThat(periodsUser1, contains(period1));
        assertThat(periodsUser2, contains(period2));
    }

    @Test
    public void findPeriodsIntersectingInterval_shouldNotReturningTouching() {
        // given
        User user1 = userTestService.createUser("user1");

        Instant thresholdDate = clock.getCurrent();
        Period period = new Period(clock.getCurrent(), clock.shiftSeconds(1), false, user1);

        periodRepositoryUnderTest.save(period);

        // when
        List<Period> intersectingIntervalsBefore = periodRepositoryUnderTest.findPeriodsIntersectingInterval(
                user1.getUuid(), period.getBeginTime().minusSeconds(1), period.getBeginTime());

        List<Period> intersectingIntervalsAfter = periodRepositoryUnderTest.findPeriodsIntersectingInterval(
                user1.getUuid(), period.getEndTime(), period.getEndTime().plusSeconds(1));

        // assert
        assertThat(intersectingIntervalsAfter, empty());
        assertThat(intersectingIntervalsBefore, empty());
    }

    @Test
    public void findPeriodsIntersectingInterval_shouldReturningIncluding() {
        // given
        User user1 = userTestService.createUser("user1");

        Instant thresholdDate = clock.getCurrent();
        Period period = new Period(clock.getCurrent(), clock.shiftSeconds(1), false, user1);

        periodRepositoryUnderTest.save(period);

        // when
        List<Period> intersectingIntervalsLeft = periodRepositoryUnderTest.findPeriodsIntersectingInterval(
                user1.getUuid(), period.getBeginTime().minusSeconds(1), period.getBeginTime().plusMillis(1));

        List<Period> intersectingIntervalsRight = periodRepositoryUnderTest.findPeriodsIntersectingInterval(
                user1.getUuid(), period.getEndTime().minusMillis(1), period.getEndTime().plusSeconds(1));

        List<Period> intersectingIntervalsBigger = periodRepositoryUnderTest.findPeriodsIntersectingInterval(
                user1.getUuid(), period.getBeginTime().minusMillis(1), period.getEndTime().plusMillis(1));

        // assert
        assertThat(intersectingIntervalsRight, not(empty()));
        assertThat(intersectingIntervalsLeft, not(empty()));
        assertThat(intersectingIntervalsBigger, not(empty()));
    }
}
