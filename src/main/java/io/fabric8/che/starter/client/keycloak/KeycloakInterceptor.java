/*-
 * #%L
 * che-starter
 * %%
 * Copyright (C) 2017 Red Hat, Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package io.fabric8.che.starter.client.keycloak;

import java.io.IOException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

class KeycloakInterceptor implements ClientHttpRequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private String keycloakToken;
    private String requestId;

    public KeycloakInterceptor(String keycloakToken, String requestId) {
        this.keycloakToken = keycloakToken;
        this.requestId = requestId;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        requestId = StringUtils.isBlank(requestId) ? generateRequestId() : requestId;

        HttpHeaders headers = request.getHeaders();
        headers.add(REQUEST_ID_HEADER, requestId);
        headers.add(AUTHORIZATION_HEADER, keycloakToken);
        return execution.execute(request, body);
    }

    private String generateRequestId() {
        return RandomStringUtils.random(16, true, true).toLowerCase();
    }

}
