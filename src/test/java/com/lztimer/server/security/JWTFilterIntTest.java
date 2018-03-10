package com.lztimer.server.security;

import com.lztimer.server.LztimerServerApplication;
import com.lztimer.server.dto.PeriodList;
import com.lztimer.server.entity.Period;
import com.lztimer.server.entity.User;
import com.lztimer.server.repository.PeriodRepository;
import com.lztimer.server.service.AuthorityService;
import com.lztimer.server.service.UserService;
import com.lztimer.server.util.DbTestUtil;
import lombok.experimental.var;
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
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
        User user = userService.createUser("email@email.com");
        UUID uuid = user.getUuid();
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                uuid, null, Arrays.asList(Authorities.USER.toGrantedAuthority())
        );
        String jwtToken = tokenProvider.createToken(uuid, authenticationToken, false);
        var period = new Period(Instant.now(), Instant.now().plusSeconds(1), true);
        var periodList = PeriodList.builder().period(period).build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTConfigurer.AUTHORIZATION_HEADER, JWTFilter.BEARER_PREFIX + jwtToken);
        HttpEntity<PeriodList> periodEntity = new HttpEntity<>(periodList, headers);

        // when
        ResponseEntity<String> exchange = restTemplate.postForEntity("http://localhost:" + port + "/api/periods",
                periodEntity, String.class);

        // then
        assertThat(exchange.getStatusCode(), equalTo(HttpStatus.CREATED));
        List<Period> all = periodRepository.findAll();
        assertThat(all, hasSize(1));
        assertThat(all.get(0).getOwner().getUuid(), equalTo(uuid));
    }

    // TODO: test for wrong token, expired etc.
}