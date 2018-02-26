package com.lztimer.server.security;

import com.lztimer.server.config.ConfigProperties;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private String secretKey;

    private long tokenValidityInMilliseconds;

    private long tokenValidityInMillisecondsForRememberMe;

    private ConfigProperties configProperties;

    public TokenProvider(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @PostConstruct
    public void init() {
        this.secretKey = configProperties.getToken().getSecret();

        this.tokenValidityInMilliseconds =
            1000 * configProperties.getToken().getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe =
            1000 * configProperties.getToken().getTokenValidityInSecondsForRememberMe();
    }

    public String createToken(UUID uuid, Authentication authentication, Boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        return Jwts.builder()
            .setSubject(uuid.toString())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setExpiration(validity)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid Token signature.");
            log.trace("Invalid Token signature trace: {}", e);
        } catch (MalformedJwtException e) {
            log.info("Invalid Token token.");
            log.trace("Invalid Token token trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired Token token.");
            log.trace("Expired Token token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported Token token.");
            log.trace("Unsupported Token token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("Token token compact of handler are invalid.");
            log.trace("Token token compact of handler are invalid trace: {}", e);
        }
        return false;
    }
}
