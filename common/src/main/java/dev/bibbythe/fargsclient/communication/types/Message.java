package dev.bibbythe.fargsclient.communication.types;

import dev.bibbythe.fargsclient.FargsClient;

import java.io.Serializable;

public class Message<T> implements Serializable {
    public final String from = FargsClient.config.clientId;
    public final MessageType type;
    public T data;

    public Message(MessageType type) {
        this.type = type;
    }

    public Message(MessageType type, T data) {
        this.type = type;
        this.data = data;
    }
}
