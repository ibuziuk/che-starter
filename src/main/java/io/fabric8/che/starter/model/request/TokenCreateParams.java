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
package io.fabric8.che.starter.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This DTO is used to when a client calls the set GitHub token method.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenCreateParams {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
