package dev.bibbythe.fargsclient.communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import dev.bibbythe.fargsclient.FargsClient;
import dev.bibbythe.fargsclient.events.CommunicationEvents;

import java.io.IOException;

public class CommsDirectSub {
    public static CommsDirectSub instance;

    private final Channel rabbitmqChannel;

    public CommsDirectSub(CommsConnection commsConnection) {
        try {
            this.rabbitmqChannel = commsConnection.createChannel();
            rabbitmqChannel.exchangeDeclare(FargsClient.config.directExchangeName, "direct");
        } catch (IOException e) {
            FargsClient.LOGGER.error("Error while creating RabbitMQ exchange", e);
            throw new RuntimeException(e);
        }
        instance = this;
    }

    public void subscribe(String routingKey) {
        try {
            String queueName = rabbitmqChannel.queueDeclare().getQueue();
            rabbitmqChannel.queueBind(queueName, FargsClient.config.directExchangeName, routingKey);
            rabbitmqChannel.basicConsume(queueName, true, this::listener,consumerTag -> {});
        } catch (IOException e) {
            FargsClient.LOGGER.error("Error while subscribing to RabbitMQ exchange", e);
            throw new RuntimeException(e);
        }
    }

    void listener(String consumerTag, Delivery message) {
        CommunicationEvents.RECEIVED_DIRECT_MESSAGE.invoker().onReceivedDirectMessage(consumerTag, message);
    }
}