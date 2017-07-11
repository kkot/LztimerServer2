package com.lztimer.server.repository;

import com.lztimer.server.LztimerServerApplication;
import com.lztimer.server.entity.Period;
import com.lztimer.server.entity.User;
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

import static org.hamcrest.Matchers.contains;
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

    @Autowired
    private UserTestService userTestService;

    private MovingClock clock;

    @Before
    public void setUp() throws Exception {
        clock = new MovingClock(Instant.now());
    }

    @Test
    public void findEndedAfter_shouldOnlyReturnPeriodsAfterDate() throws Exception {
        // arrange
        User user = userTestService.createUser("user1");
        Period beforePeriod = new Period(clock.getCurrent(), clock.shiftSeconds(1), false, user);
        Instant thresholdDate = clock.shiftSeconds(1);
        Period afterPeriod = new Period(clock.shiftSeconds(1), clock.shiftSeconds(1), false, user);

        periodRepositoryUnderTest.save(beforePeriod);
        periodRepositoryUnderTest.save(afterPeriod);

        // act
        List<Period> periodsAfter = periodRepositoryUnderTest.findEndedAfter(user.getLogin(), thresholdDate);

        // assert
        assertThat(periodsAfter, contains(afterPeriod));
    }

    @Test
    public void findEndedAfter_shouldOnlyReturnPeriodsFromUser() throws Exception {
        // arrange
        User user1 = userTestService.createUser("user1");
        User user2 = userTestService.createUser("user2");
        Instant thresholdDate = clock.getCurrent();
        Period period1 = new Period(clock.getCurrent(), clock.shiftSeconds(1), false, user1);
        Period period2 = new Period(clock.shiftSeconds(1), clock.shiftSeconds(1), false, user2);

        periodRepositoryUnderTest.save(period1);
        periodRepositoryUnderTest.save(period2);

        // act
        List<Period> periodsUser1 = periodRepositoryUnderTest.findEndedAfter(user1.getLogin(), thresholdDate);
        List<Period> periodsUser2 = periodRepositoryUnderTest.findEndedAfter(user2.getLogin(), thresholdDate);

        // assert
        assertThat(periodsUser1, contains(period1));
        assertThat(periodsUser2, contains(period2));
    }

}
