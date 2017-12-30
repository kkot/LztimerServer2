package com.lztimer.server.service;

import com.lztimer.server.LztimerServerApplication;
import com.lztimer.server.entity.Period;
import com.lztimer.server.entity.User;
import com.lztimer.server.repository.PeriodRepository;
import com.lztimer.server.security.AuthenticationService;
import com.lztimer.server.util.MovingClock;
import com.lztimer.server.util.UserTestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LztimerServerApplication.class)
@Transactional
public class PeriodServiceTest {

    @SpyBean
    PeriodService periodService;

    @Autowired
    PeriodRepository periodRepository;

    @Autowired
    UserTestService userTestService;

    @Autowired
    AuthenticationService authenticationService;

    private MovingClock movingClock = new MovingClock();

    @Test
    public void addAndReplace_shouldReplacePreviouslyExistingPeriod() {
        // given
        User user = userTestService.createUser("user");
        authenticationService.authenticate(user.getLogin());
        Period period1 = new Period(movingClock.getCurrent(), movingClock.shiftSeconds(1), false);
        Period period2 = new Period(movingClock.getCurrent(), movingClock.shiftSeconds(1), true);
        periodService.save(period1);
        periodService.save(period2);
        Period period2ext = new Period(period2.getBeginTime(), movingClock.shiftSeconds(1), true);
        assertThat(periodRepository.findAll(), contains(period1, period2));

        // when
        periodService.addAndReplace(period2ext);

        // then
        assertThat(periodRepository.findAll(), contains(period1, period2ext));
    }

    @Test
    public void addAndReplace_shouldReportErrorWhenThereArePeriodsPartiallyOutSideCurrent() {
        // given
        User user = userTestService.createUser("user");
        authenticationService.authenticate(user.getLogin());
        Instant now = Instant.now();

        Period period1 = new Period(now, now.plusSeconds(5), false);
        periodService.save(period1);
        Period period2 = new Period(now.plusSeconds(1), now.plusSeconds(6), false);

        // when
        periodService.addAndReplace(period2);

        // then
        assertThat(periodRepository.findAll(), contains(period1, period2));
        verify(periodService, times(1)).reportIncorrectPeriodUpdater(any());
    }
}