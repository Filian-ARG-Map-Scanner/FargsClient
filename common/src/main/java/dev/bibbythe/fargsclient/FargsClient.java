package dev.bibbythe.fargsclient;


import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.bibbythe.fargsclient.commands.FargsCommands;
import dev.bibbythe.fargsclient.communication.CommsManager;
import dev.bibbythe.fargsclient.scanning.ScanManager;
import dev.bibbythe.fargsclient.traversal.Autopilot;
import dev.bibbythe.fargsclient.types.ClientType;
import io.sentry.Sentry;
import io.sentry.protocol.User;
import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;

import static dev.bibbythe.fargsclient.scanning.ScanManager.scanEnabled;
import static dev.bibbythe.fargsclient.traversal.Autopilot.autoPilotMasterArm;

public final class FargsClient {
    public static final String MOD_ID = "fargs_client";
    public static boolean enabled = true;
    public static Logger LOGGER = new Logger();
    public static Config config;
    public static CommsManager commsManager;

    private static String clientDifId = null;
    private static String commonDifId = null;

    public static void setupSentry(ClientType client, String version) {
        try (InputStream in = FargsClient.class.getResourceAsStream("/sentry-common-dif-id")) {
            if (in == null) {
                commonDifId = null;
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    commonDifId = reader.readLine();
                }
            }
        } catch (IOException ignored) {
        }
        if (commonDifId.equals("1")) {
            return;
        }
        try (InputStream in = FargsClient.class.getResourceAsStream("/sentry-" + client.getValue() + "-dif-id")) {
            if (in == null) {
                clientDifId = null;
            } else {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    clientDifId = reader.readLine();
                }
            }

        } catch (IOException ignored) {
        }
        Sentry.init(options -> {
            if (!Objects.equals(clientDifId, null) && !Objects.equals(clientDifId, "00000000-0000-0000-0000-000000000000")) {
                options.addBundleId(clientDifId);
            }
            if (!Objects.equals(commonDifId, null) && !Objects.equals(commonDifId, "00000000-0000-0000-0000-000000000000")) {
                options.addBundleId(commonDifId);
            }
            options.setDsn("https://dae1d1b92cd43f5d9ed93118138de1c0@sentry.bibbythe.dev/4");
            options.setEnvironment(client.getValue());
            // Add data like request headers and IP for users,
            // see https://docs.sentry.io/platforms/java/data-management/data-collected/ for more info
            options.setSendDefaultPii(false);
            if (!version.equals("0.0.1")) {
                options.setRelease(version);
            }
        });
    }

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
        enabled = true;
        ClientCommandRegistrationEvent.EVENT.register(FargsCommands::registerCommands);
        ClientTickEvent.CLIENT_POST.register(FargsClient::tickLoop);
    }

    public static void tickLoop(MinecraftClient client) {
        if (autoPilotMasterArm) {
            Autopilot.autoPilotRun(client);
        }
        if (!scanEnabled) {
            ScanManager.scanManagerRun(client);
        }
    }

    public static void disable() {
        if (commsManager != null) {
            commsManager.disable();
        }
        ClientCommandRegistrationEvent.EVENT.unregister(FargsCommands::registerCommands);
        ClientTickEvent.CLIENT_POST.unregister(FargsClient::tickLoop);
        enabled = false;
    }
}
