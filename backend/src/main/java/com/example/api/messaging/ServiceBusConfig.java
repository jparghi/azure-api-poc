package com.example.api.messaging;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(ServiceBusProperties.class)
public class ServiceBusConfig {

    @Bean
    @ConditionalOnProperty(prefix = "azure.servicebus", name = "enabled", havingValue = "true")
    public ServiceBusClientBuilder serviceBusClientBuilder(ServiceBusProperties properties) {
        if (!StringUtils.hasText(properties.getConnectionString())) {
            throw new IllegalStateException("azure.servicebus.connection-string must be provided when Service Bus is enabled");
        }
        return new ServiceBusClientBuilder()
                .connectionString(properties.getConnectionString());
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnBean(ServiceBusClientBuilder.class)
    public ServiceBusSenderClient userEventsSenderClient(ServiceBusClientBuilder builder, ServiceBusProperties properties) {
        return builder.sender()
                .queueName(properties.getQueueName())
                .buildClient();
    }
}
