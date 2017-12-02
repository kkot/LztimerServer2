/**
 * Copyright 2011-2017 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lztimer.server.social;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;

/**
 * Google-specific extension to OAuth2Template.
 *
 * @author Gabriel Axel
 */
public class ExtGoogleOAuth2Template extends OAuth2Template {

    /**
     * Constructor
     */
    public ExtGoogleOAuth2Template(final String authorizeUrl, final String accessTokenUrl,
                                   final String clientId, final String clientSecret) {
        super(clientId, clientSecret,
                authorizeUrl,
                accessTokenUrl);
        setUseParametersForClientAuthentication(true);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected AccessGrant postForAccessGrant(final String accessTokenUrl,
                                             final MultiValueMap<String, String> parameters) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);
        final ResponseEntity<Map> responseEntity =
                getRestTemplate().exchange(accessTokenUrl, HttpMethod.POST, requestEntity, Map.class);
        final Map<String, Object> responseMap = responseEntity.getBody();
        return extractAccessGrant(responseMap);
    }

    private AccessGrant extractAccessGrant(final Map<String, Object> result) {
        final String accessToken = (String) result.get("access_token");
        final String scope = (String) result.get("scope");
        final String refreshToken = (String) result.get("refresh_token");

        // result.get("expires_in") may be an Integer, so cast it to Number first.
        final Number expiresInNumber = (Number) result.get("expires_in");
        final Long expiresIn = (expiresInNumber == null) ? null : expiresInNumber.longValue();

        return createAccessGrant(accessToken, scope, refreshToken, expiresIn, result);
    }
}
