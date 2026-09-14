package dev.bibbythe.fargsclient.neoforge;

import dev.bibbythe.fargsclient.types.ClientType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import dev.bibbythe.fargsclient.FargsClient;
import net.neoforged.fml.loading.FMLPaths;

@Mod(value = FargsClient.MOD_ID, dist = Dist.CLIENT)
public final class FargsClientNeoForge {
    public FargsClientNeoForge(ModContainer container) {
        FargsClient.setupSentry(ClientType.NEOFORGE, container.getModInfo().getVersion().toString());
        // Run our common setup.
        FargsClient.init(FMLPaths.CONFIGDIR.get());
    }
}
