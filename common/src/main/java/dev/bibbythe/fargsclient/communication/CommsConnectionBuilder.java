package dev.bibbythe.fargsclient.communication;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoveryListener;
import dev.bibbythe.fargsclient.FargsClient;
import dev.bibbythe.fargsclient.events.CommunicationEvents;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CommsConnectionBuilder {
    private String host;
    private int port;
    private String username;
    private String password;
    private String virtualHost;

    public CommsConnectionBuilder host(String host) {
        this.host = host;
        return this;
    }

    public CommsConnectionBuilder port(int port) {
        this.port = port;
        return this;
    }

    public CommsConnectionBuilder username(String username) {
        this.username = username;
        return this;
    }

    public CommsConnectionBuilder password(String password) {
        this.password = password;
        return this;
    }

    public CommsConnectionBuilder virtualHost(String virtualhost) {
        this.virtualHost = virtualhost;
        return this;
    }

    public CommsConnection build() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        Connection connection = factory.newConnection();
        ((Recoverable) connection).addRecoveryListener(new RecoveryListener() {
            @Override
            public void handleRecovery(Recoverable recoverable) {
                FargsClient.LOGGER.info("Comms Connection recovered");
                CommunicationEvents.RECOVERY_SUCCESS.invoker().onRecoverySuccess();
            }
            @Override
            public void handleRecoveryStarted(Recoverable recoverable) {
                FargsClient.LOGGER.info("Comms Connection lost");
                CommunicationEvents.RECOVERY_STARTED.invoker().onRecoveryStarted();
            }
        });
        return new CommsConnection(connection);

    }
}
