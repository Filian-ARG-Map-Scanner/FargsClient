package dev.bibbythe.fargsclient.types;

public enum ClientType {
    FABRIC("fabric"),
    NEOFORGE("neoforge");
    ClientType(String v) {
        value = v;
    }
    private final String value;
    public String getValue() {
        return value;
    }
}
