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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import io.opentracing.contrib.spring.web.client.TracingRestTemplateInterceptor;

public class KeycloakRestTemplate extends RestTemplate {

    public KeycloakRestTemplate(final String keycloakToken) {
        if (StringUtils.isNotBlank(keycloakToken)) {
            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new KeycloakInterceptor(keycloakToken));
            interceptors.add(new TracingRestTemplateInterceptor());
            this.setInterceptors(interceptors);
        }
    }

}
