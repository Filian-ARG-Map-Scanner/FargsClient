package dev.bibbythe.fargsclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack;
import dev.bibbythe.fargsclient.traversal.Autopilot;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public class FargsCommands {

    public static void registerCommands(CommandDispatcher<ClientCommandSourceStack> dispatcher, CommandRegistryAccess registry) {
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("startHilbert").executes(FargsCommands::startHilbertCommand));
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("stopHilbert").executes(FargsCommands::stopHilbertCommand));
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("enableAutopilot").executes(FargsCommands::enableAutopilot));
        dispatcher.register(LiteralArgumentBuilder.<ClientCommandSourceStack>literal("disableAutopilot").executes(FargsCommands::disableAutopilot));
    }

    static int enableAutopilot(CommandContext<ClientCommandSourceStack> context) {
        Autopilot.enable();
        context.getSource().arch$sendSuccess(() -> Text.literal("Autopilot enabled"), false);
        return 1;
    }

    static int disableAutopilot(CommandContext<ClientCommandSourceStack> context) {
        Autopilot.disable();
        context.getSource().arch$sendSuccess(() -> Text.literal("Autopilot disabled"), false);
        return 1;
    }

    static int startHilbertCommand(CommandContext<ClientCommandSourceStack> context) {
        Autopilot.startHilbert(524288);
        context.getSource().arch$sendSuccess(() -> Text.literal("Hilbert curve started"), false);
        return 1;
    }

    static int stopHilbertCommand(CommandContext<ClientCommandSourceStack> context) {
        Autopilot.pauseHilbert();
        context.getSource().arch$sendSuccess(() -> Text.literal("Hilbert curve stopped"), false);
        return 1;
    }
}
