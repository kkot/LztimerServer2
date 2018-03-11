package com.lztimer.server.service;

import com.lztimer.server.LztimerServerApplication;
import com.lztimer.server.entity.Period;
import com.lztimer.server.entity.User;
import com.lztimer.server.repository.PeriodRepository;
import com.lztimer.server.security.AuthenticationService;
import com.lztimer.server.security.SecurityService;
import com.lztimer.server.util.MovingClock;
import com.lztimer.server.util.UserTestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LztimerServerApplication.class)
@ActiveProfiles("test")
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

    @MockBean
    SecurityService securityService;

    private MovingClock movingClock = new MovingClock();

    @Before
    public void setUp() throws Exception {
        User user = userTestService.createUser("user");
        when(securityService.getLoggedUser()).thenReturn(user);
        when(securityService.getLoggedUserOpt()).thenReturn(Optional.of(user));
    }

    @Test
    public void addAndReplace_shouldReplacePreviouslyExistingPeriod() {
        // given
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
        Instant now = Instant.now();

        Period period1 = new Period(now, now.plusSeconds(5), false);
        periodService.save(period1);
        Period period2 = new Period(now.plusSeconds(1), now.plusSeconds(6), false);

        // when
        periodService.addAndReplace(period2);

        // then
        assertThat(periodRepository.findAll(), contains(period1, period2));
        verify(periodService, times(1)).reportIncorrectPeriodUpdater(any(), any());
    }
}