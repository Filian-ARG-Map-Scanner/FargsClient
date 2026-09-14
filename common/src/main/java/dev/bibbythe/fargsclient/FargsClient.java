package dev.bibbythe.fargsclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class FargsClient {
    public static final String MOD_ID = "fargs-client";
    public static Logger LOGGER = FargsClient.LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Config config;
    public static void init(Path configDir) {
        config = Config.loadConfig(configDir);

    }
}
