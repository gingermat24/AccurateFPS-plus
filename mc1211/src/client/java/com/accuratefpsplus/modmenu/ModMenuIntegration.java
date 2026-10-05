package com.accuratefpsplus.modmenu;

import com.accuratefpsplus.ConfigScreen;
import com.accuratefpsplus.YaclConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            FabricLoader loader = FabricLoader.getInstance();
            if (loader.isModLoaded("cloth-config2") || loader.isModLoaded("cloth-config")) {
                return ClothConfigScreenHelper.open(parent);
            }
            if (loader.isModLoaded("yet_another_config_lib_v3") || loader.isModLoaded("yet-another-config-lib")) {
                return YaclConfigScreenHelper.open(parent);
            }
            return null;
        };
    }

    private static class ClothConfigScreenHelper {
        public static Screen open(Screen parent) {
            return ConfigScreen.getConfigScreen(parent);
        }
    }

    private static class YaclConfigScreenHelper {
        public static Screen open(Screen parent) {
            return YaclConfigScreen.getConfigScreen(parent);
        }
    }
}
