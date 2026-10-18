package com.accuratefpsplus;

import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

public final class AccurateFpsPlusPlatform {
    private AccurateFpsPlusPlatform() {
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static Path configDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Screen createConfigScreen(Screen parent) {
        return ConfigScreenRouter.createPreferred(parent);
    }
}
