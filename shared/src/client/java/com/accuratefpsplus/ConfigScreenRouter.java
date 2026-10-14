package com.accuratefpsplus;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ConfigScreenRouter {
    private ConfigScreenRouter() {
    }

    public static Screen create(Screen parent) {
        if (AccurateFpsPlusClient.configScreenPreference == 1 && isClothConfigLoaded()) {
            return ConfigScreen.getConfigScreen(parent);
        }
        if (AccurateFpsPlusClient.configScreenPreference == 2 && isYaclConfigLoaded()) {
            return YaclConfigScreen.getConfigScreen(parent);
        }
        if (isClothConfigLoaded()) {
            return ConfigScreen.getConfigScreen(parent);
        }
        if (isYaclConfigLoaded()) {
            return YaclConfigScreen.getConfigScreen(parent);
        }
        return null;
    }

    public static Screen createPreferred(Screen parent) {
        return create(parent);
    }

    public static boolean hasAvailableScreen() {
        return isClothConfigLoaded() || isYaclConfigLoaded();
    }

    public static List<Integer> availablePreferences() {
        boolean cloth = isClothConfigLoaded();
        boolean yacl = isYaclConfigLoaded();
        if (cloth && yacl) {
            return List.of(1, 2);
        }
        if (cloth) {
            return List.of(1);
        }
        if (yacl) {
            return List.of(2);
        }
        return List.of();
    }

    public static int defaultPreference() {
        if (isClothConfigLoaded()) {
            return 1;
        }
        if (isYaclConfigLoaded()) {
            return 2;
        }
        return 0;
    }

    public static Component preferenceLabel(int value) {
        return Component.translatable(switch (value) {
            case 1 -> "value.accuratefpsplus.configScreenPreference.cloth";
            case 2 -> "value.accuratefpsplus.configScreenPreference.yacl";
            default -> "value.accuratefpsplus.configScreenPreference.cloth";
        });
    }

    public static ScreenPreference preferenceFromValue(int value) {
        return value == 2 ? ScreenPreference.YACL : ScreenPreference.CLOTH;
    }

    public enum ScreenPreference {
        CLOTH(1),
        YACL(2);

        private final int value;

        ScreenPreference(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    private static boolean isClothConfigLoaded() {
        return AccurateFpsPlusPlatform.isModLoaded("cloth-config2")
            || AccurateFpsPlusPlatform.isModLoaded("cloth-config")
            || AccurateFpsPlusPlatform.isModLoaded("cloth_config");
    }

    private static boolean isYaclConfigLoaded() {
        return AccurateFpsPlusPlatform.isModLoaded("yet_another_config_lib_v3")
            || AccurateFpsPlusPlatform.isModLoaded("yet-another-config-lib");
    }
}
