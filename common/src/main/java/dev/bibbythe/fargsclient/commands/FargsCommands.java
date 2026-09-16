package dev.bibbythe.fargsclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack;
import dev.bibbythe.fargsclient.FargsClient;
import dev.bibbythe.fargsclient.communication.types.Message;
import dev.bibbythe.fargsclient.communication.types.MessageType;
import dev.bibbythe.fargsclient.scanning.ScanManager;
import dev.bibbythe.fargsclient.traversal.Autopilot;
import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.command.CommandRegistryAccess;

import java.util.Objects;

import static net.minecraft.text.Text.literal;

public class FargsCommands {

    public static void registerCommands(CommandDispatcher<ClientCommandSourceStack> dispatcher, CommandRegistryAccess   registry) {
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("startScan").executes(FargsCommands::startScanCommand));
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("pauseScan").executes(FargsCommands::pauseScanCommand));
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("resumeScan").executes(FargsCommands::resumeScanCommand));
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("stopScan").executes(FargsCommands::stopScanCommand));
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("goto").executes(FargsCommands::goToCommand));
    }

    static int stopScanCommand(CommandContext<ClientCommandSourceStack> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getCurrentServerEntry() == null || !Objects.equals(client.getCurrentServerEntry().address, "50.115.0.25:25565")) {
            context.getSource().arch$sendFailure(literal("You are not connected to the filian ARG Server"));
            return 0;
        }
        try {
            if (!Autopilot.autoPilotMasterArm) {
                context.getSource().arch$sendSuccess(() -> literal("No scan is running"), false);
            } else {
                ScanManager.stopScan();context.getSource().arch$sendSuccess(() -> literal("Scan stopped"), false);
            }
            return 1;
        } catch (Exception e) {
            SentryId sentryId = Sentry.captureException(e);
            context.getSource().arch$sendFailure(literal("Failed to stop scan. ErrorID: " + sentryId));
            return 0;
        }

    }

    static int goToCommand(CommandContext<ClientCommandSourceStack> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getCurrentServerEntry() == null || !Objects.equals(client.getCurrentServerEntry().address, "50.115.0.25:25565")) {
            context.getSource().arch$sendFailure(literal("You are not connected to the filian ARG Server"));
            return 0;
        }
        try {
            if (Autopilot.autoPilotMasterArm) {
                context.getSource().arch$sendSuccess(() -> literal("Scan currently running or goto already in progress"), false);
            } else {
                // TODO: implement goto command
                context.getSource().arch$sendSuccess(() -> literal("Currently not implemented"), false);
            }
            return 1;
        } catch (Exception e) {
            SentryId sentryId = Sentry.captureException(e);
            context.getSource().arch$sendFailure(literal("Autopilot failed to disable. ErrorID: " + sentryId));
            return 0;
        }
    }

    static int startScanCommand(CommandContext<ClientCommandSourceStack> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getCurrentServerEntry() == null || !Objects.equals(client.getCurrentServerEntry().address, "50.115.0.25:25565")) {
            context.getSource().arch$sendFailure(literal("You are not connected to the filian ARG Server"));
            return 0;
        }
        try (ClientWorld world = context.getSource().arch$getLevel()) {
            if (Autopilot.autoPilotMasterArm) {
                context.getSource().arch$sendSuccess(() -> literal("Scan already running"), false);
            } else {
                FargsClient.commsManager.sendMessage(new Message<>(MessageType.REGION_REQUEST, world.getRegistryKey().toString()));
                context.getSource().arch$sendSuccess(() -> literal("Scan job successfully requested"), false);
            }
            return 1;
        } catch (Exception e) {
            SentryId sentryId = Sentry.captureException(e);
            context.getSource().arch$sendFailure(literal("Failed to request scan job from server. ErrorID: " + sentryId));
            return 0;
        }
    }

    static int resumeScanCommand(CommandContext<ClientCommandSourceStack> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getCurrentServerEntry() == null || !Objects.equals(client.getCurrentServerEntry().address, "50.115.0.25:25565")) {
            context.getSource().arch$sendFailure(literal("You are not connected to the filian ARG Server"));
            return 0;
        }
        try {
            if (Autopilot.autoPilotMasterArm) {
                context.getSource().arch$sendSuccess(() -> literal("Scan already running"), false);
            } else {
                Autopilot.enable();
                context.getSource().arch$sendSuccess(() -> literal("Scan resumed"), false);
            }
            return 1;
        } catch (Exception e) {
            SentryId sentryId = Sentry.captureException(e);
            context.getSource().arch$sendFailure(literal("Failed to resume scan. ErrorID: " + sentryId));
            return 0;
        }

    }

    static int pauseScanCommand(CommandContext<ClientCommandSourceStack> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getCurrentServerEntry() == null || !Objects.equals(client.getCurrentServerEntry().address, "50.115.0.25:25565")) {
            context.getSource().arch$sendFailure(literal("You are not connected to the filian ARG Server"));
            return 0;
        }
        try {
            if (Autopilot.autoPilotMasterArm) {
                Autopilot.pause();
                context.getSource().arch$sendSuccess(() -> literal("Scan paused"), false);
            } else {
                context.getSource().arch$sendSuccess(() -> literal("Scan not currently running"), false);
            }
            return 1;
        } catch (Exception e) {
            SentryId sentryId = Sentry.captureException(e);
            context.getSource().arch$sendFailure(literal("Failed to pause scan. ErrorID: " + sentryId));
            return 0;
        }
    }
}
