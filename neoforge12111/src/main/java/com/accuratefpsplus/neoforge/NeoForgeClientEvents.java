package com.accuratefpsplus.neoforge;

import com.accuratefpsplus.AccurateFpsPlusClient;
import com.accuratefpsplus.AccurateFpsPlusPlatform;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

final class NeoForgeClientEvents {
    private static final KeyMapping.Category KEY_CATEGORY = AccurateFpsPlusClient.getKeyCategory();
    private static final KeyMapping TOGGLE_HUD = new KeyMapping(
        "key.accuratefpsplus.toggleHUD",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KEY_CATEGORY
    );
    private static final KeyMapping CYCLE_PRESET = new KeyMapping(
        "key.accuratefpsplus.cyclePreset",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KEY_CATEGORY
    );
    private static final KeyMapping OPEN_CONFIG = new KeyMapping(
        "key.accuratefpsplus.openConfig",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KEY_CATEGORY
    );

    private NeoForgeClientEvents() {
    }

    static void register(IEventBus modEventBus, ModContainer modContainer) {
        AccurateFpsPlusClient.initialize(FMLPaths.CONFIGDIR.get());
        AccurateFpsPlusClient.setKeyMappings(
            TOGGLE_HUD, CYCLE_PRESET, OPEN_CONFIG
        );

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, NeoForgeClientEvents::createConfigScreen);
        modEventBus.addListener(NeoForgeClientEvents::registerKeyMappings);
        modEventBus.addListener(NeoForgeClientEvents::registerGuiLayers);
        NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::onClientTick);
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(KEY_CATEGORY);
        event.register(TOGGLE_HUD);
        event.register(CYCLE_PRESET);
        event.register(OPEN_CONFIG);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
            Identifier.fromNamespaceAndPath("accuratefpsplus", "hud"),
            (graphics, deltaTracker) -> AccurateFpsPlusClient.renderHud(graphics)
        );
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        AccurateFpsPlusClient.onClientTick(Minecraft.getInstance());
    }

    private static Screen createConfigScreen(ModContainer container, Screen parent) {
        return AccurateFpsPlusPlatform.createConfigScreen(parent);
    }
}
