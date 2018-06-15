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
package io.fabric8.che.starter.client.github;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.che.starter.client.keycloak.KeycloakRestTemplate;
import io.fabric8.che.starter.exception.KeycloakException;
import io.fabric8.che.starter.opentracing.OpentracingLoggerWrapper;

@Component
public class GitHubTokenProvider {
    private static final String ACCESS_TOKEN = "access_token";

    @Autowired
    OpentracingLoggerWrapper LOG;

    @Value("${GITHUB_TOKEN_URL:https://auth.prod-preview.openshift.io/api/token?for=https://github.com}")
    private String gitHubTokenUrl;

    public String getGitHubToken(String keycloakToken) throws KeycloakException, JsonProcessingException, IOException {
        LOG.info("GitHub token url - {}", gitHubTokenUrl);
        // {"access_token":"token","scope":"admin:repo_hook,gist,read:org,repo,user","token_type":"bearer"}
        String token = getAccessToken(gitHubTokenUrl, keycloakToken);
        return token;
    }

    private String getAccessToken(String endpoint, String keycloakToken) throws KeycloakException, JsonProcessingException, IOException {
        String responseBody = getResponseBody(endpoint, keycloakToken);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(responseBody);
        JsonNode accessToken = json.get(ACCESS_TOKEN);
        if (accessToken == null) {
            throw new KeycloakException("Unable to obtain token from endpoint: " + endpoint);
        }
        return accessToken.asText();
    }

    private String getResponseBody(String endpoint, String keycloakToken) {
        RestTemplate template = new KeycloakRestTemplate(keycloakToken);
        ResponseEntity<String> response = template.exchange(endpoint.toString(), HttpMethod.GET, null, String.class);
        return response.getBody();
    }

}
