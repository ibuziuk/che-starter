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
package io.fabric8.che.starter.util;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UrlHelperTest {
    private static final String TOKEN = "kjhKJhLKJHSLKJDHDSKJAHLKAHSdshjs";
    private static final String SCOPE = "scope";
    private static final String RESPONSE_BODY = "access_token=" + TOKEN + "&scope=" + SCOPE;
    private static final String ACCESS_TOKEN = "access_token";

    @Test
    public void processQuery() {
        Map<String, String> parameters = UrlHelper.splitQuery(RESPONSE_BODY);
        String token = parameters.get(ACCESS_TOKEN);
        String scope = parameters.get(SCOPE);
        assertEquals(token, TOKEN);
        assertEquals(scope, SCOPE);
    }
}
