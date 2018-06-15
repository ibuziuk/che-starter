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

import java.util.Arrays;

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.Propagation.Factory;
import io.opentracing.Tracer;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Component
public class TracerPicker {
    private static final String SERVICE_NAME = "che-starter";

    @Value("${che.opentracing.tracer:jaeger}")
    private String tracerImpl;

    public Tracer getTracer() throws ConfigurationException {
        Tracer tracer;
        if (tracerImpl.equals("jaeger")) {
            tracer = new io.jaegertracing.Tracer.Builder(SERVICE_NAME).build();
        } else if (tracerImpl.equals("zipkin")) {
            OkHttpSender sender = OkHttpSender.create("http://127.0.0.1:9411/api/v2/spans");
            AsyncReporter<Span> spanReporter = AsyncReporter.create(sender);

            Factory propagationFactory = ExtraFieldPropagation.newFactoryBuilder(B3Propagation.FACTORY)
            .addPrefixedFields("baggage-", Arrays.asList("country-code", "user-id"))
            .build();

            Tracing braveTracing = Tracing.newBuilder()
                    .localServiceName(SERVICE_NAME)
                    .propagationFactory(propagationFactory)
                    .spanReporter(spanReporter)
                    .build();

            tracer = BraveTracer.create(braveTracing);
        } else {
            throw new ConfigurationException(tracerImpl + " is not supported");
        }
        return tracer;
    }
}
