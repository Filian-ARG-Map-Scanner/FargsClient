package dev.bibbythe.fargsclient.fabric;

import dev.bibbythe.fargsclient.FargsClient;
import dev.bibbythe.fargsclient.types.ClientType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class FargsClientFabric implements ClientModInitializer {
    public FargsClientFabric() {
        String version;
        if (FabricLoader.getInstance().getModContainer(FargsClient.MOD_ID).isPresent()) {
            version = FabricLoader.getInstance().getModContainer(FargsClient.MOD_ID).get().getMetadata().getVersion().toString();
        } else {
           version = "0.0.1";
        }

        FargsClient.setupSentry(ClientType.FABRIC, version);
    }
    @Override
    public void onInitializeClient() {
        FargsClient.init(FabricLoader.getInstance().getConfigDir());
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
    }
}
