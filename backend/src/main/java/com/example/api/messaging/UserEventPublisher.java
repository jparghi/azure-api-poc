package com.example.api.messaging;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.example.api.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventPublisher.class);

    private final ServiceBusProperties properties;
    private final ObjectMapper objectMapper;
    private final Optional<ServiceBusSenderClient> senderClient;

    public UserEventPublisher(ServiceBusProperties properties,
                              ObjectMapper objectMapper,
                              Optional<ServiceBusSenderClient> senderClient) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.senderClient = senderClient;
    }

    public void publishUserCreated(User user) {
        send("USER_CREATED", user);
    }

    public void publishUserUpdated(User user) {
        send("USER_UPDATED", user);
    }

    public void publishUserDeleted(User user) {
        send("USER_DELETED", user);
    }

    private void send(String eventType, User user) {
        if (!properties.isEnabled()) {
            LOGGER.debug("Service Bus publishing disabled. Skipping {} event for user {}", eventType, user.getId());
            return;
        }

        senderClient.ifPresentOrElse(client -> doSend(client, eventType, user),
                () -> LOGGER.warn("Service Bus sender client not available. Event {} for user {} not published.", eventType, user.getId()));
    }

    private void doSend(ServiceBusSenderClient client, String eventType, User user) {
        UserEventPayload payload = new UserEventPayload(
                eventType,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                OffsetDateTime.now()
        );

        try {
            byte[] body = objectMapper.writeValueAsBytes(payload);
            ServiceBusMessage message = new ServiceBusMessage(body);
            message.setSubject(eventType);
            message.setContentType("application/json");
            message.setMessageId(UUID.randomUUID().toString());
            client.sendMessage(message);
            LOGGER.info("Published {} event for user {} to Azure Service Bus queue {}", eventType, user.getId(), properties.getQueueName());
        } catch (JsonProcessingException ex) {
            LOGGER.error("Failed to serialize Service Bus message for user {}", user.getId(), ex);
        }
    }
}
