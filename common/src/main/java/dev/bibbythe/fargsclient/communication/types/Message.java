package dev.bibbythe.fargsclient.communication.types;

public class Message {
    String from;
    MessageType type;
    Object data;

    public Message(String from, MessageType type, Object data) {
        this.from = from;
        this.type = type;
        this.data = data;
    }
}
