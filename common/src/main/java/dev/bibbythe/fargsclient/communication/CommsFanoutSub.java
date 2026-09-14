package dev.bibbythe.fargsclient.communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import dev.bibbythe.fargsclient.FargsClient;
import dev.bibbythe.fargsclient.events.CommunicationEvents;

import java.io.IOException;

public class CommsFanoutSub {
    public static CommsFanoutSub instance;

    private final Channel rabbitmqChannel;

    public CommsFanoutSub(CommsConnection commsConnection) {
        try {
            this.rabbitmqChannel = commsConnection.createChannel();
            rabbitmqChannel.exchangeDeclare(FargsClient.config.fanoutExchangeName, "fanout");
        } catch (IOException e) {
            FargsClient.LOGGER.error("Error while creating RabbitMQ exchange", e);
            throw new RuntimeException(e);
        }
        instance = this;
    }

    public void subscribe() {
        try {
            String queueName = rabbitmqChannel.queueDeclare().getQueue();
            rabbitmqChannel.queueBind(queueName, FargsClient.config.fanoutExchangeName, "");
            rabbitmqChannel.basicConsume(queueName, true, this::listener,consumerTag -> {});
        } catch (IOException e) {
            FargsClient.LOGGER.error("Error while subscribing to RabbitMQ exchange", e);
            throw new RuntimeException(e);
        }
    }

    void listener(String consumerTag, Delivery message) {
        CommunicationEvents.RECEIVED_FANOUT_MESSAGE.invoker().onReceivedFanoutMessage(consumerTag, message);
    }
}