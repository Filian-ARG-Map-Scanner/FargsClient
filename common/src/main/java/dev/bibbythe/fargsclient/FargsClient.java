package dev.bibbythe.fargsclient;


import dev.bibbythe.fargsclient.communication.CommsManager;
import io.sentry.Sentry;
import io.sentry.protocol.User;

import java.nio.file.Path;

public final class FargsClient {
    public static final String MOD_ID = "fargs-client";
    public static Logger LOGGER = new Logger();
    public static Config config;
    public static CommsManager commsManager;
    public static void init(Path configDir) {
        config = Config.loadConfig(configDir.resolve("fargs"));
        if (config == null) {
            Sentry.captureException(new NullPointerException("Config is null"));
            throw new NullPointerException("Config is null");
        }
        User user = new User();
        user.setId(config.clientId);
        Sentry.setUser(user);
        commsManager = new CommsManager();
    }
}
