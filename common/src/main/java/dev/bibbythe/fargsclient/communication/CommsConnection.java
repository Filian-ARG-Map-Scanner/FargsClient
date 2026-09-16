package dev.bibbythe.fargsclient.communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import dev.bibbythe.fargsclient.FargsClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class CommsConnection {
    private final Connection connection;
    private final List<Channel> channels = new ArrayList<>();

    public CommsConnection(Connection connection) {
        this.connection = connection;
    }

    public Channel createChannel() throws IOException {
        Channel channel = connection.createChannel();
        channels.add(channel);
        return channel;
    }

    public void close() {
        for (Channel channel : channels) {
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                FargsClient.LOGGER.error(String.format("Error while closing rabbitmq channel %s", e.getMessage()), e);
            }
        }
        try {
            connection.close();
        } catch (IOException e) {
            FargsClient.LOGGER.error(String.format("Error while closing rabbitmq connection %s", e.getMessage()), e);
        }
    }
}
