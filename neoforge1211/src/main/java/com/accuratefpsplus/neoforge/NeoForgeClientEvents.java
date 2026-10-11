package com.accuratefpsplus.neoforge;

import com.accuratefpsplus.AccurateFpsPlusClient;
import com.accuratefpsplus.ConfigScreen;
import com.accuratefpsplus.NativeConfigScreen;
import com.accuratefpsplus.YaclConfigScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

final class NeoForgeClientEvents {
    private static final String KEY_CATEGORY = "category.accuratefpsplus.title";
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

    private NeoForgeClientEvents() {
    }

    static void register(IEventBus modEventBus, ModContainer modContainer) {
        AccurateFpsPlusClient.initialize(FMLPaths.CONFIGDIR.get());
        AccurateFpsPlusClient.setKeyMappings(TOGGLE_HUD, CYCLE_PRESET);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, NeoForgeClientEvents::createConfigScreen);
        modEventBus.addListener(NeoForgeClientEvents::registerKeyMappings);
        modEventBus.addListener(NeoForgeClientEvents::registerGuiLayers);
        NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::onClientTick);
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_HUD);
        event.register(CYCLE_PRESET);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
            ResourceLocation.fromNamespaceAndPath("accuratefpsplus", "hud"),
            (graphics, deltaTracker) -> AccurateFpsPlusClient.renderHud(graphics)
        );
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        AccurateFpsPlusClient.onClientTick(Minecraft.getInstance());
    }

    private static Screen createConfigScreen(ModContainer container, Screen parent) {
        if (ModList.get().isLoaded("cloth_config")) {
            return ClothConfigScreenHelper.open(parent);
        }
        if (ModList.get().isLoaded("yet_another_config_lib_v3")) {
            return YaclConfigScreenHelper.open(parent);
        }
        return NativeConfigScreen.create(parent);
    }

    private static final class ClothConfigScreenHelper {
        private static Screen open(Screen parent) {
            return ConfigScreen.getConfigScreen(parent);
        }
    }

    private static final class YaclConfigScreenHelper {
        private static Screen open(Screen parent) {
            return YaclConfigScreen.getConfigScreen(parent);
        }
    }
}
