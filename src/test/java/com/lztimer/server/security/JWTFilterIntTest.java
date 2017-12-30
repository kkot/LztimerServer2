package com.lztimer.server.security;

import com.lztimer.server.LztimerServerApplication;
import com.lztimer.server.entity.Period;
import com.lztimer.server.repository.PeriodRepository;
import com.lztimer.server.service.AuthorityService;
import com.lztimer.server.service.UserService;
import com.lztimer.server.util.DbTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Integration test to check if JWT token can be used to perform HTTP REST request on a controller (as example
 * PeriodController is used).
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LztimerServerApplication.class, webEnvironment = RANDOM_PORT)
public class JWTFilterIntTest {

    private static final String TEST_USER_LOGIN = "test_user";

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    DbTestUtil dbTestUtil;

    @LocalServerPort
    int port;

    @Autowired
    UserService userService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    PeriodRepository periodRepository;

    @Autowired
    AuthorityService authorityService;

    @Before
    public void setUp() {
        dbTestUtil.resetDb(); // for integration test @Transactional doesn't work, different Sessions
    }

    @Test
    public void shouldCreateNewPeriodWithCorrectUser_whenJWTTokenIsProvided() {
        // given
        authorityService.addStandard();
        userService.createUser(TEST_USER_LOGIN, "x", "X", "X@a.pl", "", "pl");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                TEST_USER_LOGIN, null, Arrays.asList(Authorities.USER.toGrantedAuthority())
        );
        String jwtToken = tokenProvider.createToken(authenticationToken, false);
        Period period = new Period(Instant.now(), Instant.now().plusSeconds(1), true);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTConfigurer.AUTHORIZATION_HEADER, JWTFilter.BEARER_PREFIX + jwtToken);
        HttpEntity<Period> periodEntity = new HttpEntity<>(period, headers);

        // when
        ResponseEntity<String> exchange = restTemplate.postForEntity("http://localhost:" + port + "/api/periods",
                periodEntity, String.class);

        // then
        assertThat(exchange.getStatusCode(), equalTo(HttpStatus.CREATED));
        List<Period> all = periodRepository.findAll();
        assertThat(all, hasSize(1));
        assertThat(all.get(0).getOwner().getLogin(), equalTo(TEST_USER_LOGIN));
    }

    // TODO: test for wrong token, expired etc.
}