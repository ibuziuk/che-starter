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

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import brave.sampler.Sampler;
import io.opentracing.Tracer;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Encoding;
import zipkin.reporter.okhttp3.OkHttpSender;

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
            OkHttpSender okHttpSender = OkHttpSender.builder()
                    .encoding(Encoding.JSON)
                    .endpoint("http://localhost:9411/api/v1/spans")
                    .build();
            AsyncReporter<Span> reporter = AsyncReporter.builder(okHttpSender).build();
            Tracing braveTracer = Tracing.newBuilder()
                    .localServiceName(SERVICE_NAME)
                    .reporter(reporter)
                    .traceId128Bit(true)
                    .sampler(Sampler.ALWAYS_SAMPLE)
                    .build();
            tracer = BraveTracer.create(braveTracer);
        } else {
            throw new ConfigurationException(tracerImpl + " is not supported");
        }
        return tracer;
    }
}
