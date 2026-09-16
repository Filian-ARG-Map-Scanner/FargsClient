package dev.bibbythe.fargsclient.communication;

import com.rabbitmq.client.Channel;
import dev.bibbythe.fargsclient.FargsClient;

import java.io.IOException;

public class CommsDirectPub {
    public static CommsDirectPub instance;

    private final Channel rabbitmqChannel;

    public CommsDirectPub(CommsConnection commsConnection) {
        try {
            this.rabbitmqChannel = commsConnection.createChannel();
            rabbitmqChannel.exchangeDeclare(FargsClient.config.directExchangeName, "direct", true);
        } catch (IOException e) {
            FargsClient.LOGGER.error("Error while creating RabbitMQ exchange", e);
            throw new RuntimeException(e);
        }
        instance = this;
    }

    public void publish(String routingKey, String message) throws IOException {
        FargsClient.LOGGER.debug("Publishing message to " + FargsClient.config.directExchangeName + " with routing key " + routingKey);
        rabbitmqChannel.basicPublish(FargsClient.config.directExchangeName, routingKey, null, message.getBytes());
    }
}
