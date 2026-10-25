package com.accuratefpsplus.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;

@Mod(value = "accuratefpsplus", dist = Dist.CLIENT)
public final class AccurateFpsPlusNeoForge {
    public AccurateFpsPlusNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        NeoForgeClientEvents.register(modEventBus, modContainer);
    }
}
