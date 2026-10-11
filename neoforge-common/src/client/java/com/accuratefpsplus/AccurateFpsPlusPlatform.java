package com.accuratefpsplus;

import java.nio.file.Path;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.FMLPaths;

public final class AccurateFpsPlusPlatform {
    private AccurateFpsPlusPlatform() {
    }

    @SuppressWarnings("removal")
    public static boolean isModLoaded(String modId) {
        LoadingModList loadingModList = LoadingModList.get();
        return loadingModList != null
            && loadingModList.getMods().stream().anyMatch(mod -> mod.getModId().equals(modId));
    }

    public static Path configDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
