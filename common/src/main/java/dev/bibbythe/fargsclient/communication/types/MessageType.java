package dev.bibbythe.fargsclient.communication.types;

import java.io.Serializable;

public enum MessageType implements Serializable {
    INIT,
    REGION_REQUEST,
    REGION,
    SERVER_RESTART,
    REJOIN,
    SCAN_RESULT,
}
