package com.accuratefpsplus.neoforge;

import com.accuratefpsplus.AccurateFpsPlusClient;
import com.accuratefpsplus.AccurateFpsPlusPlatform;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

final class NeoForgeClientEvents {
    private static final KeyMapping TOGGLE_HUD = AccurateFpsPlusClient.createToggleKeyMapping();
    private static final KeyMapping CYCLE_PRESET =
        AccurateFpsPlusClient.createKeyMapping("key.accuratefpsplus.cyclePreset");
    private static final KeyMapping OPEN_CONFIG =
        AccurateFpsPlusClient.createKeyMapping("key.accuratefpsplus.openConfig");

    private NeoForgeClientEvents() {
    }

    static void register(IEventBus modEventBus, ModContainer modContainer) {
        AccurateFpsPlusClient.initialize(FMLPaths.CONFIGDIR.get());
        AccurateFpsPlusClient.setKeyMappings(TOGGLE_HUD, CYCLE_PRESET, OPEN_CONFIG);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, NeoForgeClientEvents::createConfigScreen);
        modEventBus.addListener(NeoForgeClientEvents::registerKeyMappings);
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(AccurateFpsPlusClient.getKeyCategory());
        event.register(TOGGLE_HUD);
        event.register(CYCLE_PRESET);
        event.register(OPEN_CONFIG);
    }

    private static Screen createConfigScreen(ModContainer container, Screen parent) {
        return AccurateFpsPlusPlatform.createConfigScreen(parent);
    }
}
