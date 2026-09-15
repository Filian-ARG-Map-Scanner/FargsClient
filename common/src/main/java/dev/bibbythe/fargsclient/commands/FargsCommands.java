package dev.bibbythe.fargsclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack;
import dev.bibbythe.fargsclient.traversal.Autopilot;
import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import net.minecraft.command.CommandRegistryAccess;

import static net.minecraft.text.Text.literal;

public class FargsCommands {

    public static void registerCommands(CommandDispatcher<ClientCommandSourceStack> dispatcher, CommandRegistryAccess registry) {
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("startHilbert").executes(FargsCommands::startHilbertCommand));
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("stopHilbert").executes(FargsCommands::stopHilbertCommand));
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("enableAutopilot").executes(FargsCommands::enableAutopilot));
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("disableAutopilot").executes(FargsCommands::disableAutopilot));
    }

    static int enableAutopilot(CommandContext<ClientCommandSourceStack> context) {
        try {
            Autopilot.enable();
        } catch (Exception e) {
            SentryId sentryId = Sentry.captureException(e);
            context.getSource().arch$sendFailure(literal("Autopilot failed to enable. ErrorID: " + sentryId));
            return 0;
        }
        context.getSource().arch$sendSuccess(() -> literal("Autopilot enabled"), false);
        return 1;
    }

    static int disableAutopilot(CommandContext<ClientCommandSourceStack> context) {
        try {
            Autopilot.disable();
        } catch (Exception e) {
            SentryId sentryId = Sentry.captureException(e);
            context.getSource().arch$sendFailure(literal("Autopilot failed to disable. ErrorID: " + sentryId));
            return 0;
        }
        context.getSource().arch$sendSuccess(() -> literal("Autopilot disabled"), false);
        return 1;
    }

    static int startHilbertCommand(CommandContext<ClientCommandSourceStack> context) {
        try {
            Autopilot.startHilbert(524288);
        } catch (Exception e) {
            SentryId sentryId = Sentry.captureException(e);
            context.getSource().arch$sendFailure(literal("Hilbert failed to start. ErrorID: " + sentryId));
            return 0;
        }
        context.getSource().arch$sendSuccess(() -> literal("Hilbert curve started"), false);
        return 1;
    }

    static int stopHilbertCommand(CommandContext<ClientCommandSourceStack> context) {
        try {
            Autopilot.stopHilbert();
        } catch (Exception e) {
            SentryId sentryId = Sentry.captureException(e);
            context.getSource().arch$sendFailure(literal("Hilbert failed to stop. ErrorID: " + sentryId));
            return 0;
        }
        context.getSource().arch$sendSuccess(() -> literal("Hilbert curve stopped"), false);
        return 1;
    }
}
