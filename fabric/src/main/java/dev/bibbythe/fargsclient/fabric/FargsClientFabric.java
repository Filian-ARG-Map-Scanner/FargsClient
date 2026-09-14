package dev.bibbythe.fargsclient.fabric;

import dev.bibbythe.fargsclient.FargsClient;
import io.sentry.Sentry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public final class FargsClientFabric implements ClientModInitializer {
    @Nullable
    private String difId;
    public FargsClientFabric() {
        try (InputStream in = getClass().getResourceAsStream("/sentry-dif-id");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            difId = reader.readLine();
        } catch (IOException ignored) {
        }

        Sentry.init(options -> {
            options.setDsn("https://dae1d1b92cd43f5d9ed93118138de1c0@sentry.bibbythe.dev/4");
            // Add data like request headers and IP for users,
            // see https://docs.sentry.io/platforms/java/data-management/data-collected/ for more info

            if (!Objects.equals(difId, null) && !Objects.equals(difId, "00000000-0000-0000-0000-000000000000")) {
                options.addBundleId(difId);
            }

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
