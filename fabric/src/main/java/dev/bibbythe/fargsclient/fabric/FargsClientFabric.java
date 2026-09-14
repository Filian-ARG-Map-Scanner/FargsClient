package dev.bibbythe.fargsclient.fabric;

import dev.bibbythe.fargsclient.FargsClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

public final class FargsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FargsClient.init(FabricLoader.getInstance().getConfigDir());
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
    }
}
