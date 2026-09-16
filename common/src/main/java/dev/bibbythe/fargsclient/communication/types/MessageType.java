package dev.bibbythe.fargsclient.communication.types;

import java.io.Serializable;

public enum MessageType implements Serializable {
    INIT,
    DISCONNECT,
    BLACKLIST,
    REGION_REQUEST,
    REGION_REQUEST_RESPONSE,
    SCAN_RESULT,
    UPDATE_AVAILABLE,
    DIMENSION_COMPLETE,
    REGION_COMPLETE,
}
