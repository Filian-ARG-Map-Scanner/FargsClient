package dev.bibbythe.fargsclient;

import io.sentry.protocol.SentryId;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.LoggerFactory;
import io.sentry.Sentry;

public class Logger {
    org.slf4j.Logger logger = LoggerFactory.getLogger(FargsClient.MOD_ID);


    public void error(String message, Exception e) {
        SentryId sentryId = Sentry.captureException(e);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("The Fargs client has run into an error. Error ID: " + sentryId), false);
        }
        logger.error("{} SentryID: {}", message, sentryId);
    }

    public void info(String message) {
        Sentry.addBreadcrumb(message);
        logger.info(message);
    }

    public void debug(String message) {
        Sentry.addBreadcrumb(message);
        logger.debug(message);
    }
}
