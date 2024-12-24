package com.OnlineConsultancyApp.services;

import com.OnlineConsultancyApp.models.EmailMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {

    private static final String QUEUE_NAME = "Confirmation_email_queue";
    private static final String HOST = "localhost";
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;

    public RabbitMQService() {
        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    public void sendConfirmationEmail(EmailMessage emailMessage) throws Exception {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            String jsonMessage = objectMapper.writeValueAsString(emailMessage);

            channel.basicPublish("", QUEUE_NAME, null, jsonMessage.getBytes());
        }
    }

}
