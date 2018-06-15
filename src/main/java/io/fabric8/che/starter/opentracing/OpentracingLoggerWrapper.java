/*-
 * #%L
 * che-starter
 * %%
 * Copyright (C) 2018 Red Hat, Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package io.fabric8.che.starter.opentracing;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;

@Component
public class OpentracingLoggerWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(OpentracingLoggerWrapper.class);

    @Autowired
    Tracer tracer;

    public void info(String message) {
        Scope scope = tracer.scopeManager().active();
        if (scope != null) {
            Map map = new HashMap<>();
            map.put(Fields.EVENT, "info");
            map.put(Fields.MESSAGE, message);
            scope.span().log(map);
        }
        LOG.info(message);
    }

    public void info(String message, Object... object) {
        Scope scope = tracer.scopeManager().active();
        if (scope != null) {
            Map map = new HashMap<>();
            map.put(Fields.EVENT, "info");
            map.put(Fields.MESSAGE, message);
            scope.span().log(map);
        }
        LOG.info(message, object);
    }

    public void warn(String message) {
        Scope scope = tracer.scopeManager().active();
        if (scope != null) {
            Map map = new HashMap<>();
            map.put(Fields.EVENT, "warn");
            map.put(Fields.MESSAGE, message);
            scope.span().log(map);
        }
        LOG.warn(message);
    }

    public void warn(String message, Object... object) {
        Scope scope = tracer.scopeManager().active();
        if (scope != null) {
            Map map = new HashMap<>();
            map.put(Fields.EVENT, "warn");
            map.put(Fields.MESSAGE, message);
            scope.span().log(map);
        }
        LOG.warn(message, object);
    }

    public void debug(String message) {
        Scope scope = tracer.scopeManager().active();
        if (scope != null) {
            Map map = new HashMap<>();
            map.put(Fields.EVENT, "debug");
            map.put(Fields.MESSAGE, message);
            scope.span().log(map);
        }
        LOG.debug(message);
    }

    public void error(String message) {
        Scope scope = tracer.scopeManager().active();
        if (scope != null) {;
            Map map = new HashMap<>();
            map.put(Fields.EVENT, "error");
            map.put(Fields.ERROR_OBJECT, message);
            map.put(Fields.MESSAGE, message);
            scope.span().log(map);
        }
        LOG.error(message);
    }

    public void error(String message, Exception e) {
        Scope scope = tracer.scopeManager().active();
        if (scope != null) {
            Map map = new HashMap<>();
            map.put(Fields.EVENT, "error");
            map.put(Fields.ERROR_OBJECT, e);
            map.put(Fields.MESSAGE, e.getMessage());
            scope.span().log(map);
        }
        LOG.error(message, e);
    }

    public void debug(String message, Object... object) {
        Scope scope = tracer.scopeManager().active();
        if (scope != null) {
            Map map = new HashMap<>();
            map.put(Fields.EVENT, "debug");
            map.put(Fields.MESSAGE, message);
            scope.span().log(map);
        }
        LOG.warn(message, object);
    }

}
