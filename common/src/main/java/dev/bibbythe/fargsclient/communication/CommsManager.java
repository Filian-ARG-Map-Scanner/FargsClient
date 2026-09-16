package dev.bibbythe.fargsclient.communication;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.rabbitmq.client.Delivery;
import dev.bibbythe.fargsclient.FargsClient;
import dev.bibbythe.fargsclient.communication.types.Message;
import dev.bibbythe.fargsclient.communication.types.MessageType;
import dev.bibbythe.fargsclient.communication.types.Packet;
import dev.bibbythe.fargsclient.events.CommunicationEvents;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static dev.bibbythe.fargsclient.communication.MessageHandler.*;

public class CommsManager {
    CommsConnection connection;
    CommsManager instance;
    String clientId;

    Gson objectMapper = new Gson();
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

        sendMessage(new Message<>(MessageType.INIT, FargsClient.Version));
    }

    public void disable() {
        if (connection != null) {
            connection.close();
        }
        instance = null;
    }

    public <T> void sendMessage(Message<T> message) {
        try {
            CommsDirectPub.instance.publish("server", objectMapper.toJson(message));
        } catch (Exception e) {
            FargsClient.LOGGER.error("Failed to send message", e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    private <T> Packet<T> deserializeData(String message) {
        return objectMapper.fromJson(message, new TypeToken<Packet<T>>() {}.getType());
    }

    private void fanoutMessageHandler(String consumerTag, Delivery delivery) {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        Packet<?> msg = deserializeData(message);
        if (!Objects.equals(msg.data.from, "server")) {
            FargsClient.LOGGER.error("Invalid message source for fanout: " + msg.data.from, new IllegalArgumentException("Invalid message source for fanout: " + msg.data.from));
        }
        switch (msg.data.type) {
            case BLACKLIST:
                handleBlacklistMessage(deserializeData(message));
                break;
            default:
                FargsClient.LOGGER.error("Invalid message type for fanout: " + msg.data.type, new IllegalArgumentException("Invalid message type for fanout: " + msg.data.type));
                break;
        }
    }

    private void directMessageHandler(String consumerTag, Delivery delivery) {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        Packet<?> msg = deserializeData(message);
        if (!Objects.equals(msg.pattern, clientId)) {
            FargsClient.LOGGER.error("Incorrect message destination: " + msg.pattern, new IllegalArgumentException("Incorrect message destination: " + msg.pattern));
            return;
        }
        if (!Objects.equals(msg.data.from, "server")) {
            FargsClient.LOGGER.error("Invalid message source for fanout: " + msg.data.from, new IllegalArgumentException("Invalid message source for fanout: " + msg.data.from));
            return;
        }
        switch (msg.data.type) {
            case DIMENSION_COMPLETE:
                handleDimensionComplete();
                break;
            case BLACKLIST:
                handleBlacklistMessage(deserializeData(message));
                break;
            case REGION_REQUEST_RESPONSE:
                handleRegionRequestResponseMessage(deserializeData(message));
                break;
            case UPDATE_AVAILABLE:
                handleUpdateAvailable(deserializeData(message));
                break;
            default:
                FargsClient.LOGGER.error("Invalid message type for direct: " + msg.data.type, new IllegalArgumentException("Invalid message type for direct: " + msg.data.type));
                break;
        }
    }

}
