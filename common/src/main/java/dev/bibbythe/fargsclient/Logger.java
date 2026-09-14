package dev.bibbythe.fargsclient;

import io.sentry.protocol.SentryId;
import org.slf4j.LoggerFactory;
import io.sentry.Sentry;

import java.io.IOException;

public class Logger {
    org.slf4j.Logger logger = LoggerFactory.getLogger(FargsClient.MOD_ID);


    public void error(String message, Exception e) {
        SentryId sentryId = Sentry.captureException(e);
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
