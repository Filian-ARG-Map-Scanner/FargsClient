package dev.bibbythe.fargsclient.communication;

import com.google.gson.Gson;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import com.rabbitmq.client.Delivery;
import dev.bibbythe.fargsclient.FargsClient;
import dev.bibbythe.fargsclient.communication.types.Message;
import dev.bibbythe.fargsclient.communication.types.MessageType;
import dev.bibbythe.fargsclient.events.CommunicationEvents;
import io.sentry.Sentry;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommsManager {
    CommsConnection connection;
    CommsManager instance;
    String clientId;
    public CommsManager() {
        try {
            connection = new CommsConnectionBuilder()
                    .host(FargsClient.config.host)
                    .port(FargsClient.config.port)
                    .virtualHost(FargsClient.config.virtualHost)
                    .username(FargsClient.config.username)
                    .password(FargsClient.config.password)
                    .build();
        } catch (Exception e) {
            FargsClient.LOGGER.error("Failed to startup Comms Manager", e);
            throw new RuntimeException("Failed to startup Comms Manager", e);
        }
        new CommsDirectPub(connection);
        new CommsDirectSub(connection);
        new CommsFanoutSub(connection);
        FargsClient.LOGGER.info("Connected to Central Server");
        CommunicationEvents.RECEIVED_DIRECT_MESSAGE.register(this::directMessageHandler);
        CommunicationEvents.RECEIVED_FANOUT_MESSAGE.register(this::fanoutMessageHandler);
        clientId = FargsClient.config.clientId;
        instance = this;

        CommsDirectSub.instance.subscribe(clientId);
        CommsFanoutSub.instance.subscribe();

        sendMessage(new Message(clientId, MessageType.INIT, "Test"));

        sendMessage(new Message(clientId, MessageType.REGION_REQUEST, new String[] {"test", "test2"}));
    }

    public void disable() {
        if (connection != null) {
            connection.close();
        }
        instance = null;
    }

    void sendMessage(Message message) {
        try {
            ObjectMapper object = new ObjectMapper(new Gson());
            CommsDirectPub.instance.publish("server", object.writeValueAsString(message));
        } catch (Exception e) {
            FargsClient.LOGGER.error("Failed to send message", e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    private void fanoutMessageHandler(String consumerTag, Delivery delivery) {
        FargsClient.LOGGER.info("Received fanout message: " + new String(delivery.getBody(), StandardCharsets.UTF_8));
    }

    private void directMessageHandler(String consumerTag, Delivery delivery) {
        FargsClient.LOGGER.info("Received direct message: " + new String(delivery.getBody(), StandardCharsets.UTF_8));
    }

}
