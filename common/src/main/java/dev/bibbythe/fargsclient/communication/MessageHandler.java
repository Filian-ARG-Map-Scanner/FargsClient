package dev.bibbythe.fargsclient.communication;

import dev.bibbythe.fargsclient.FargsClient;
import dev.bibbythe.fargsclient.communication.types.Packet;
import dev.bibbythe.fargsclient.communication.types.datatypes.*;
import dev.bibbythe.fargsclient.events.CommunicationEvents;
import dev.bibbythe.fargsclient.scanning.ScanManager;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Map;

public class MessageHandler {
    public static void handleBlacklistMessage(Packet<Map<String, ArrayList<String>>> message) {
        for (String key : message.data.data.keySet()) {
            ArrayList<Block> newBlocklist = new ArrayList<>();
            for (String block : message.data.data.get(key)) {
                Identifier id = Identifier.tryParse(block);
                if (id == null) {
                    FargsClient.LOGGER.error("Invalid block identifier in " + key + " blocklist: " + block, new IllegalArgumentException("Invalid block identifier: " + block));
                    continue;
                }
                newBlocklist.add(Registries.BLOCK.get(id));
            }
            ScanManager.ignoredBlocks.put(key, newBlocklist);
        }
        FargsClient.LOGGER.info("Blocklist updated: " + message.data.data.size());
        CommunicationEvents.BLOCKLIST_UPDATED.invoker().onUpdatedBlocklist(message.data.data);
    }

    public static void handleRegionRequestResponseMessage(Packet<RegionRequestResponseData> message) {
        RegionRequestResponseData data = message.data.data;
        CommunicationEvents.REGION_REQUEST_RESPONSE_RECEIVED.invoker().onRegionRequestResponseReceived(data);
    }

    public static void handleUpdateAvailable(Packet<String> message) {
        CommunicationEvents.UPDATE_AVAILABLE.invoker().onUpdateAvailable(message.data.data);
    }

    public static void handleDimensionComplete() {
        CommunicationEvents.DIMENSION_COMPLETE.invoker().onDimensionComplete();
    }
}
