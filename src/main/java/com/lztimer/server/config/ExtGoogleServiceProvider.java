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
package com.lztimer.server.config;

import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

/**
 * Google ServiceProvider implementation.
 *
 * @author Gabriel Axel
 */
public class ExtGoogleServiceProvider extends AbstractOAuth2ServiceProvider<Google> {

    public ExtGoogleServiceProvider(
            final String authorizeUrl, final String accessTokenUrl,
            final String clientId, final String clientSecret) {
        super(new ExtGoogleOAuth2Template(authorizeUrl, accessTokenUrl, clientId, clientSecret));
    }

    @Override
    public Google getApi(final String accessToken) {
        return new GoogleTemplate(accessToken);
    }
}
