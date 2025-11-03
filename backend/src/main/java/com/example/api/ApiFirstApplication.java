package com.example.api;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiFirstApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiFirstApplication.class, args);
    }

    @Bean
    public OpenTelemetry openTelemetry(@Value("${appinsights.connection-string:}") String connectionString) {
        AzureMonitorExporterBuilder builder = new AzureMonitorExporterBuilder();
        if (!connectionString.isBlank()) {
            builder.connectionString(connectionString);
        }
        SpanExporter exporter = builder.buildSpanExporter();
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(exporter))
                .build();
        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();
    }
}
