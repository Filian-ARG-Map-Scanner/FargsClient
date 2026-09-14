package dev.bibbythe.fargsclient.neoforge;

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
        // Run our common setup.
        FargsClient.init(FMLPaths.CONFIGDIR.get());
    }
}
