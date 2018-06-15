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
package io.fabric8.che.starter.mdc.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import io.fabric8.che.starter.client.keycloak.KeycloakTokenParser;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.log.Fields;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tags;

@Component
public class RequestFilter extends GenericFilterBean {
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String REQUEST_ID_MDC_KEY = "req_id";
    private static final String IDENTITY_ID_MDC_KEY = "identity_id";
    private static final String UNKNOWN_IDENTITY_ID = "Unknown";

    @Autowired
    KeycloakTokenParser keycloakTokenParser;

    @Autowired
    io.opentracing.Tracer tracer;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Scope scope = tracer.scopeManager().active();
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            String requestId = req.getHeader(REQUEST_ID_HEADER);
            requestId = (StringUtils.isBlank(requestId)) ? generateRequestId() : requestId;

            String keycloakToken = req.getHeader(AUTHORIZATION_HEADER);
            String identityId = getIdentityId(keycloakToken);

            MDC.put(REQUEST_ID_MDC_KEY, requestId);
            MDC.put(IDENTITY_ID_MDC_KEY, identityId);

            if (scope != null) {
                StringTag identityIdTag = new StringTag(IDENTITY_ID_MDC_KEY);
                identityIdTag.set(scope.span(), identityId);
                
                StringTag requestIdTag = new StringTag(REQUEST_ID_MDC_KEY);
                requestIdTag.set(scope.span(), requestId);
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            if (scope != null) {
                Span span = scope.span();
                Tags.ERROR.set(span, true);
                Map map = new HashMap<>();
                map.put(Fields.EVENT, "error");
                map.put(Fields.ERROR_OBJECT, e);
                map.put(Fields.MESSAGE, e.getMessage());
                span.log(map);
            }
            throw e;
        }finally {
            MDC.clear();
        }
    }

    private String getIdentityId(final String keycloakToken) {
        String identityId;
        if (StringUtils.isBlank(keycloakToken)) {
            identityId = UNKNOWN_IDENTITY_ID;
        } else {
            try {
                identityId = keycloakTokenParser.getIdentityId(keycloakToken);
            } catch (Exception e) {
                identityId = UNKNOWN_IDENTITY_ID;
            }
        }
        return identityId;
    }

    private String generateRequestId() {
        return RandomStringUtils.random(16, true, true).toLowerCase();
    }

}
