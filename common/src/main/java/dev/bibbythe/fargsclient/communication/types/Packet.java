package dev.bibbythe.fargsclient.communication.types;

public class Packet<T> {
    public String pattern;
    public Message<T> data;
}
