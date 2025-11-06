package com.example.api.messaging;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.example.api.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserEventPublisherTest {

    private ServiceBusProperties properties;
    private ServiceBusSenderClient senderClient;
    private UserEventPublisher publisher;

    @BeforeEach
    void setUp() {
        properties = new ServiceBusProperties();
        properties.setEnabled(true);
        properties.setQueueName("user-events");
        senderClient = mock(ServiceBusSenderClient.class);
        publisher = new UserEventPublisher(properties, new ObjectMapper(), Optional.of(senderClient));
    }

    @Test
    void publishUserCreatedSendsMessage() {
        User user = new User();
        user.setId(1L);
        user.setUsername("jsmith");
        user.setEmail("jsmith@example.com");
        user.setRole("ADMIN");

        publisher.publishUserCreated(user);

        verify(senderClient).sendMessage(any(ServiceBusMessage.class));
    }
}
