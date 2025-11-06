package com.example.api.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "azure.servicebus")
public class ServiceBusProperties {

    /**
     * Enables publishing to Azure Service Bus. Defaults to false to avoid attempts to connect
     * during local development when the connection string is not configured.
     */
    private boolean enabled = false;

    /**
     * Fully qualified Service Bus connection string that contains the shared access policy.
     */
    private String connectionString;

    /**
     * Name of the queue that captures user domain events.
     */
    private String queueName = "user-events";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
