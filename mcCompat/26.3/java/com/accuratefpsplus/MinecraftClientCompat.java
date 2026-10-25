package com.accuratefpsplus;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.VideoMode;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

final class MinecraftClientCompat {
    private MinecraftClientCompat() {
    }

    static KeyMapping createKeyMapping(String translationKey, KeyMapping.Category category) {
        return new KeyMapping(translationKey, InputConstants.Type.KEYBOARD, InputConstants.UNKNOWN.getValue(), category);
    }

    static int detectDisplayRefreshRate() {
        VideoMode activeMode = Minecraft.getInstance().getWindow().getActiveVideoMode();
        if (activeMode == null) {
            return 60;
        }
        int refreshRate = Math.round(activeMode.getRefreshRate());
        return refreshRate > 0 ? refreshRate : 60;
    }

    static boolean isScreenOpen(Minecraft client) {
        return client.gui.screen() != null;
    }

    static void setScreen(Minecraft client, Screen screen) {
        client.gui.setScreen(screen);
    }
}
