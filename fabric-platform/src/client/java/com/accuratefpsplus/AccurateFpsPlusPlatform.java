package com.accuratefpsplus;

import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class AccurateFpsPlusPlatform {
    private AccurateFpsPlusPlatform() {
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static Path configDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
