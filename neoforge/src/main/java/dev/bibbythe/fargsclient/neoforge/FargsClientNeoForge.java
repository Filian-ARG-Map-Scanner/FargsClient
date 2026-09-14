package dev.bibbythe.fargsclient.neoforge;

import io.sentry.Sentry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import dev.bibbythe.fargsclient.FargsClient;
import net.neoforged.fml.loading.FMLPaths;

@Mod(value = FargsClient.MOD_ID, dist = Dist.CLIENT)
public final class FargsClientNeoForge {
    public FargsClientNeoForge(ModContainer container) {
        Sentry.init(options -> {
            options.setDsn("https://dae1d1b92cd43f5d9ed93118138de1c0@sentry.bibbythe.dev/4");
            options.setEnvironment("neoforge");
            // Add data like request headers and IP for users,
            // see https://docs.sentry.io/platforms/java/data-management/data-collected/ for more info
            options.setSendDefaultPii(false);
            options.setRelease(container.getModInfo().getVersion().toString());
        });
        // Run our common setup.
        FargsClient.init(FMLPaths.CONFIGDIR.get());
    }
}
