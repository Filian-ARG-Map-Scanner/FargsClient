package dev.bibbythe.fargsclient.fabric;

import dev.bibbythe.fargsclient.FargsClient;
import io.sentry.Sentry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class FargsClientFabric implements ClientModInitializer {
    public FargsClientFabric() {
        Sentry.init(options -> {
            options.setDsn("https://dae1d1b92cd43f5d9ed93118138de1c0@sentry.bibbythe.dev/4");
            // Add data like request headers and IP for users,
            // see https://docs.sentry.io/platforms/java/data-management/data-collected/ for more info
            options.setEnvironment("fabric");
            options.setSendDefaultPii(false);
            if (FabricLoader.getInstance().getModContainer("fargsclient").isPresent())
                options.setRelease(FabricLoader.getInstance().getModContainer("fargsclient").get().getMetadata().getVersion().toString());
        });
    }
    @Override
    public void onInitializeClient() {
        FargsClient.init(FabricLoader.getInstance().getConfigDir());
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
    }
}
