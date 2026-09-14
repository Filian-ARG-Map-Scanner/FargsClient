package dev.bibbythe.fargsclient.neoforge;

import io.sentry.Sentry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import dev.bibbythe.fargsclient.FargsClient;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(value = FargsClient.MOD_ID, dist = Dist.CLIENT)
public final class FargsClientNeoForge {
    public FargsClientNeoForge(IEventBus modBus, ModContainer container) {
        Sentry.init(options -> {
            options.setDsn("https://991b96ebe502771ad32908d09b524506@sentry.bibbythe.dev/5");

            // Add data like request headers and IP for users,
            // see https://docs.sentry.io/platforms/java/data-management/data-collected/ for more info
            options.setSendDefaultPii(false);
            // When first trying Sentry it's good to see what the SDK is doing:
            options.setDebug(true);
        });
        // Run our common setup.
        FargsClient.init(FMLPaths.CONFIGDIR.get());
    }
}
