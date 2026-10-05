package com.accuratefpsplus.mixin.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
@Mixin(Options.class)
public abstract class GameOptionsPreciseFpsMixin {
    @Unique
    private static final Logger ACCURATEFPSPLUS_LOGGER = LoggerFactory.getLogger("AccurateFPS+");
    @Unique
    private static final int AFPS_MIN = 1;
    @Unique
    private static final int AFPS_MAX = 1000;
    @Unique
    private static final int AFPS_DEFAULT = 120;
    @Unique
    private static final String AFPS_KEY = "customMaxFps";
    @Mutable
    @Shadow
    @Final
    public OptionInstance<Integer> framerateLimit;

    @Unique
    private static boolean accuratefpsplus$hasConflictingFpsMod() {
        FabricLoader loader = FabricLoader.getInstance();
        return loader.isModLoaded("sodiumfpscapfix")
            || loader.isModLoaded("fpscapfix")
            || loader.isModLoaded("fps-textbox")
            || loader.isModLoaded("fpstextbox");
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void accuratefpsplus$rebuildMaxFpsSlider(Minecraft client, File optionsFile, CallbackInfo ci) {
        if (accuratefpsplus$hasConflictingFpsMod()) {
            return;
        }
        int saved = GameOptionsPreciseFpsMixin.accuratefpsplus$readOurFps();
        this.framerateLimit = new OptionInstance<>("options.framerateLimit", OptionInstance.noTooltip(), (caption, value) -> {
            if (value >= AFPS_MAX) {
                return Options.genericValueLabel((Component)caption, (Component)Component.translatable((String)"options.framerateLimit.max"));
            }
            return Options.genericValueLabel((Component)caption, (Component)Component.translatable((String)"options.framerate", (Object[])new Object[]{value}));
        }, new OptionInstance.IntRange(AFPS_MIN, AFPS_MAX), saved, value -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.getFramerateLimitTracker() != null) {
                mc.getFramerateLimitTracker().setFramerateLimit(value);
            }
            GameOptionsPreciseFpsMixin.accuratefpsplus$writeOurFps(value);
        });
        if (client != null && client.getFramerateLimitTracker() != null) {
            client.getFramerateLimitTracker().setFramerateLimit(saved);
        }
    }

    @Inject(method={"load"}, at={@At(value="TAIL")})
    private void accuratefpsplus$afterLoad(CallbackInfo ci) {
        if (accuratefpsplus$hasConflictingFpsMod()) {
            return;
        }
        int saved = GameOptionsPreciseFpsMixin.accuratefpsplus$readOurFps();
        this.framerateLimit.set(saved);
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getFramerateLimitTracker() != null) {
            mc.getFramerateLimitTracker().setFramerateLimit(saved);
        }
    }

    @Unique
    private static Path accuratefpsplus$configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("accuratefpsplus-fps.json");
    }

    @Unique
    private static int accuratefpsplus$readOurFps() {
        Path path = GameOptionsPreciseFpsMixin.accuratefpsplus$configPath();
        if (!Files.isRegularFile(path)) {
            return AFPS_DEFAULT;
        }
        try {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonElement root = JsonParser.parseReader(reader);
                if (root == null || !root.isJsonObject()) {
                    ACCURATEFPSPLUS_LOGGER.warn("Invalid FPS limit config at {}; using {} FPS.", path, AFPS_DEFAULT);
                    return AFPS_DEFAULT;
                }
                JsonElement value = root.getAsJsonObject().get(AFPS_KEY);
                if (value == null) {
                    return AFPS_DEFAULT;
                }
                if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
                    ACCURATEFPSPLUS_LOGGER.warn("Invalid '{}' value in {}; using {} FPS.", AFPS_KEY, path, AFPS_DEFAULT);
                    return AFPS_DEFAULT;
                }
                return Mth.clamp(value.getAsInt(), AFPS_MIN, AFPS_MAX);
            }
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            ACCURATEFPSPLUS_LOGGER.warn("Could not read FPS limit config at {}; using {} FPS.", path, AFPS_DEFAULT, exception);
            return AFPS_DEFAULT;
        }
    }

    @Unique
    private static void accuratefpsplus$writeOurFps(int value) {
        Path path = GameOptionsPreciseFpsMixin.accuratefpsplus$configPath();
        JsonObject config = new JsonObject();
        if (Files.isRegularFile(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonElement root = JsonParser.parseReader(reader);
                if (root == null || !root.isJsonObject()) {
                    ACCURATEFPSPLUS_LOGGER.warn("Will not overwrite invalid FPS limit config at {}.", path);
                    return;
                }
                config = root.getAsJsonObject();
            } catch (IOException | JsonParseException | IllegalStateException exception) {
                ACCURATEFPSPLUS_LOGGER.warn("Will not overwrite unreadable FPS limit config at {}.", path, exception);
                return;
            }
        }
        config.addProperty(AFPS_KEY, value);
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                new Gson().toJson(config, writer);
            }
        } catch (IOException exception) {
            ACCURATEFPSPLUS_LOGGER.warn("Could not save FPS limit config at {}.", path, exception);
        }
    }
}
