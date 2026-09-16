package dev.bibbythe.fargsclient.communication;

import dev.bibbythe.fargsclient.FargsClient;
import dev.bibbythe.fargsclient.communication.types.Message;
import dev.bibbythe.fargsclient.communication.types.datatypes.*;
import dev.bibbythe.fargsclient.events.CommunicationEvents;
import dev.bibbythe.fargsclient.scanning.ScanManager;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Map;

public class MessageHandler {
    public static void handleBlacklistMessage(Message<Map<String, String[]>> message) {
        for (String key : message.data.keySet()) {
            ArrayList<Block> newBlocklist = new ArrayList<>();
            for (String block : message.data.get(key)) {
                Identifier id = Identifier.tryParse(block);
                if (id == null) {
                    FargsClient.LOGGER.error("Invalid block identifier in " + key + " blocklist: " + block, new IllegalArgumentException("Invalid block identifier: " + block));
                    continue;
                }
                newBlocklist.add(Registries.BLOCK.get(id));
            }
            ScanManager.ignoredBlocks.put(key, newBlocklist);
        }
        CommunicationEvents.BLOCKLIST_UPDATED.invoker().onUpdatedBlocklist(message.data);
    }

    public static void handleRegionRequestResponseMessage(Message<RegionRequestResponseData> message) {
        RegionRequestResponseData data = message.data;
        CommunicationEvents.REGION_REQUEST_RESPONSE_RECEIVED.invoker().onRegionRequestResponseReceived(data);
    }

    public static void handleUpdateAvailable(Message<String> message) {
        CommunicationEvents.UPDATE_AVAILABLE.invoker().onUpdateAvailable(message.data);
    }
}
