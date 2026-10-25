package com.accuratefpsplus;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

final class MinecraftClientCompat {
    private MinecraftClientCompat() {
    }

    static KeyMapping createKeyMapping(String translationKey, KeyMapping.Category category) {
        return new KeyMapping(translationKey, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), category);
    }

    static int detectDisplayRefreshRate() {
        long window = Minecraft.getInstance().getWindow().handle();
        long monitor = GLFW.glfwGetWindowMonitor(window);
        if (monitor == 0L) {
            monitor = GLFW.glfwGetPrimaryMonitor();
        }
        if (monitor == 0L) {
            return 60;
        }
        GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
        return mode != null && mode.refreshRate() > 0 ? mode.refreshRate() : 60;
    }

    static boolean isScreenOpen(Minecraft client) {
        return client.gui.screen() != null;
    }

    static void setScreen(Minecraft client, Screen screen) {
        client.gui.setScreen(screen);
    }
}
