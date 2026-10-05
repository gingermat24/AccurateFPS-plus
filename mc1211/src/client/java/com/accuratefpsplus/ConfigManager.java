package com.accuratefpsplus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
@SuppressWarnings("unused")
public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("AccurateFPS+");
    private static Config config;
    private static final Path configPath;
    private static final int CURRENT_CONFIG_VERSION = 4;

    protected static void loadConfig() {
        Gson gson = new Gson();
        if (Files.exists(configPath)) {
            try (BufferedReader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                config = gson.fromJson(reader, Config.class);
                if (config == null) {
                    LOGGER.warn("Configuration at {} is empty; keeping the file and using defaults.", configPath);
                    ConfigManager.preserveInvalidConfig();
                    ConfigManager.restoreDefaultConfig(false);
                }
            }
            catch (JsonSyntaxException | IOException exception) {
                LOGGER.warn("Could not load configuration at {}; using defaults.", configPath, exception);
                ConfigManager.preserveInvalidConfig();
                ConfigManager.restoreDefaultConfig(false);
            }
        } else {
            ConfigManager.restoreDefaultConfig(true);
        }
        ConfigManager.applyConfig();
    }

    private static void preserveInvalidConfig() {
        Path backupPath = configPath.resolveSibling(configPath.getFileName() + ".corrupt-" + System.currentTimeMillis());
        try {
            Files.copy(configPath, backupPath);
            LOGGER.warn("Preserved the unreadable configuration as {}.", backupPath);
        } catch (IOException exception) {
            LOGGER.error("Could not preserve unreadable configuration at {}; the original file was left untouched.", configPath, exception);
        }
    }

    private static void restoreDefaultConfig(boolean save) {
        config = new Config();
        config.configVersion = CURRENT_CONFIG_VERSION;
        ConfigManager.config.toggleHUD = AccurateFpsPlusClient.toggleHUD;
        ConfigManager.config.hideWithF3 = AccurateFpsPlusClient.hideWithF3;
        ConfigManager.config.xPos = AccurateFpsPlusClient.xPos;
        ConfigManager.config.yPos = AccurateFpsPlusClient.yPos;
        ConfigManager.config.shadow = AccurateFpsPlusClient.shadow;
        ConfigManager.config.pollingRate = AccurateFpsPlusClient.pollingRate;
        ConfigManager.config.calculationMode = AccurateFpsPlusClient.calculationMode;
        ConfigManager.config.historyWindowSeconds = AccurateFpsPlusClient.historyWindowSeconds;
        ConfigManager.config.anchorCorner = AccurateFpsPlusClient.anchorCorner;
        ConfigManager.config.darkModeOverlay = AccurateFpsPlusClient.darkModeOverlay;
        ConfigManager.config.overlayColor = AccurateFpsPlusClient.overlayColor;
        ConfigManager.config.overlayTransparency = AccurateFpsPlusClient.overlayTransparency;
        ConfigManager.config.currentPreset = AccurateFpsPlusClient.currentPreset;
        ConfigManager.config.showFps = AccurateFpsPlusClient.showFps;
        ConfigManager.config.fpsPosition = AccurateFpsPlusClient.fpsPosition;
        ConfigManager.config.fpsPrecision = AccurateFpsPlusClient.fpsPrecision;
        ConfigManager.config.beforeFps = AccurateFpsPlusClient.beforeFps;
        ConfigManager.config.afterFps = AccurateFpsPlusClient.afterFps;
        ConfigManager.config.mainUpdateInterval = AccurateFpsPlusClient.mainUpdateInterval;
        ConfigManager.config.fpsColor = AccurateFpsPlusClient.fpsColor;
        ConfigManager.config.fpsAutoColor = AccurateFpsPlusClient.fpsAutoColor;
        ConfigManager.config.showFrametime = AccurateFpsPlusClient.showFrametime;
        ConfigManager.config.frametimePosition = AccurateFpsPlusClient.frametimePosition;
        ConfigManager.config.frametimePrecision = AccurateFpsPlusClient.frametimePrecision;
        ConfigManager.config.beforeFrametime = AccurateFpsPlusClient.beforeFrametime;
        ConfigManager.config.afterFrametime = AccurateFpsPlusClient.afterFrametime;
        ConfigManager.config.frametimeColor = AccurateFpsPlusClient.frametimeColor;
        ConfigManager.config.frametimeAutoColor = AccurateFpsPlusClient.frametimeAutoColor;
        ConfigManager.config.showAvr = AccurateFpsPlusClient.showAvr;
        ConfigManager.config.avrPosition = AccurateFpsPlusClient.avrPosition;
        ConfigManager.config.avrPrecision = AccurateFpsPlusClient.avrPrecision;
        ConfigManager.config.beforeAvr = AccurateFpsPlusClient.beforeAvr;
        ConfigManager.config.afterAvr = AccurateFpsPlusClient.afterAvr;
        ConfigManager.config.avrUpdateInterval = AccurateFpsPlusClient.avrUpdateInterval;
        ConfigManager.config.avrColor = AccurateFpsPlusClient.avrColor;
        ConfigManager.config.avrAutoColor = AccurateFpsPlusClient.avrAutoColor;
        ConfigManager.config.showMax = AccurateFpsPlusClient.showMax;
        ConfigManager.config.maxPosition = AccurateFpsPlusClient.maxPosition;
        ConfigManager.config.maxPrecision = AccurateFpsPlusClient.maxPrecision;
        ConfigManager.config.beforeMax = AccurateFpsPlusClient.beforeMax;
        ConfigManager.config.afterMax = AccurateFpsPlusClient.afterMax;
        ConfigManager.config.maxUpdateInterval = AccurateFpsPlusClient.maxUpdateInterval;
        ConfigManager.config.maxColor = AccurateFpsPlusClient.maxColor;
        ConfigManager.config.maxAutoColor = AccurateFpsPlusClient.maxAutoColor;
        ConfigManager.config.showMin = AccurateFpsPlusClient.showMin;
        ConfigManager.config.minPosition = AccurateFpsPlusClient.minPosition;
        ConfigManager.config.minPrecision = AccurateFpsPlusClient.minPrecision;
        ConfigManager.config.beforeMin = AccurateFpsPlusClient.beforeMin;
        ConfigManager.config.afterMin = AccurateFpsPlusClient.afterMin;
        ConfigManager.config.minUpdateInterval = AccurateFpsPlusClient.minUpdateInterval;
        ConfigManager.config.minColor = AccurateFpsPlusClient.minColor;
        ConfigManager.config.minAutoColor = AccurateFpsPlusClient.minAutoColor;
        ConfigManager.config.showOnePercentLow = AccurateFpsPlusClient.showOnePercentLow;
        ConfigManager.config.onePercentLowPosition = AccurateFpsPlusClient.onePercentLowPosition;
        ConfigManager.config.onePercentLowPrecision = AccurateFpsPlusClient.onePercentLowPrecision;
        ConfigManager.config.beforeOnePercentLow = AccurateFpsPlusClient.beforeOnePercentLow;
        ConfigManager.config.afterOnePercentLow = AccurateFpsPlusClient.afterOnePercentLow;
        ConfigManager.config.onePercentLowUpdateInterval = AccurateFpsPlusClient.onePercentLowUpdateInterval;
        ConfigManager.config.onePercentLowColor = AccurateFpsPlusClient.onePercentLowColor;
        ConfigManager.config.onePercentLowAutoColor = AccurateFpsPlusClient.onePercentLowAutoColor;
        ConfigManager.config.showZeroOnePercentLow = AccurateFpsPlusClient.showZeroOnePercentLow;
        ConfigManager.config.zeroOnePercentLowPosition = AccurateFpsPlusClient.zeroOnePercentLowPosition;
        ConfigManager.config.zeroOnePercentLowPrecision = AccurateFpsPlusClient.zeroOnePercentLowPrecision;
        ConfigManager.config.beforeZeroOnePercentLow = AccurateFpsPlusClient.beforeZeroOnePercentLow;
        ConfigManager.config.afterZeroOnePercentLow = AccurateFpsPlusClient.afterZeroOnePercentLow;
        ConfigManager.config.zeroOnePercentLowUpdateInterval = AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval;
        ConfigManager.config.zeroOnePercentLowColor = AccurateFpsPlusClient.zeroOnePercentLowColor;
        ConfigManager.config.zeroOnePercentLowAutoColor = AccurateFpsPlusClient.zeroOnePercentLowAutoColor;
        ConfigManager.config.colorThresholdMode = AccurateFpsPlusClient.colorThresholdMode;
        ConfigManager.config.gradientColorLow = AccurateFpsPlusClient.gradientColorLow;
        ConfigManager.config.gradientColorMed = AccurateFpsPlusClient.gradientColorMed;
        ConfigManager.config.gradientColorHigh = AccurateFpsPlusClient.gradientColorHigh;
        ConfigManager.config.manualThresholdLow = AccurateFpsPlusClient.manualThresholdLow;
        ConfigManager.config.manualThresholdMed = AccurateFpsPlusClient.manualThresholdMed;
        ConfigManager.config.manualThresholdHigh = AccurateFpsPlusClient.manualThresholdHigh;
        if (save) {
            ConfigManager.saveConfig();
        }
    }

    private static void applyConfig() {
        AccurateFpsPlusClient.toggleHUD = ConfigManager.config.toggleHUD;
        AccurateFpsPlusClient.hideWithF3 = ConfigManager.config.hideWithF3;
        AccurateFpsPlusClient.xPos = ConfigManager.config.xPos;
        AccurateFpsPlusClient.yPos = ConfigManager.config.yPos;
        AccurateFpsPlusClient.shadow = ConfigManager.config.shadow;
        AccurateFpsPlusClient.pollingRate = Math.max(1, ConfigManager.config.pollingRate);
        AccurateFpsPlusClient.calculationMode = ConfigManager.config.calculationMode;
        AccurateFpsPlusClient.historyWindowSeconds = Math.max(1, ConfigManager.config.historyWindowSeconds);
        AccurateFpsPlusClient.anchorCorner = ConfigManager.config.anchorCorner;
        AccurateFpsPlusClient.darkModeOverlay = ConfigManager.config.darkModeOverlay;
        AccurateFpsPlusClient.overlayColor = ConfigManager.config.overlayColor;
        AccurateFpsPlusClient.overlayTransparency = ConfigManager.config.overlayTransparency;
        AccurateFpsPlusClient.currentPreset = ConfigManager.config.currentPreset;
        AccurateFpsPlusClient.showFps = ConfigManager.config.showFps;
        AccurateFpsPlusClient.fpsPosition = ConfigManager.config.fpsPosition;
        AccurateFpsPlusClient.fpsPrecision = ConfigManager.config.fpsPrecision;
        AccurateFpsPlusClient.beforeFps = ConfigManager.config.beforeFps;
        AccurateFpsPlusClient.afterFps = ConfigManager.config.afterFps;
        AccurateFpsPlusClient.mainUpdateInterval = Math.max(1, ConfigManager.config.mainUpdateInterval);
        AccurateFpsPlusClient.fpsColor = ConfigManager.config.fpsColor;
        AccurateFpsPlusClient.fpsAutoColor = ConfigManager.config.fpsAutoColor;
        AccurateFpsPlusClient.showFrametime = ConfigManager.config.showFrametime;
        AccurateFpsPlusClient.frametimePosition = ConfigManager.config.frametimePosition;
        AccurateFpsPlusClient.frametimePrecision = ConfigManager.config.frametimePrecision;
        AccurateFpsPlusClient.beforeFrametime = ConfigManager.config.beforeFrametime;
        AccurateFpsPlusClient.afterFrametime = ConfigManager.config.afterFrametime;
        AccurateFpsPlusClient.frametimeColor = ConfigManager.config.frametimeColor;
        AccurateFpsPlusClient.frametimeAutoColor = ConfigManager.config.frametimeAutoColor;
        AccurateFpsPlusClient.showAvr = ConfigManager.config.showAvr;
        AccurateFpsPlusClient.avrPosition = ConfigManager.config.avrPosition;
        AccurateFpsPlusClient.avrPrecision = ConfigManager.config.avrPrecision;
        AccurateFpsPlusClient.beforeAvr = ConfigManager.config.beforeAvr;
        AccurateFpsPlusClient.afterAvr = ConfigManager.config.afterAvr;
        AccurateFpsPlusClient.avrUpdateInterval = Math.max(1, ConfigManager.config.avrUpdateInterval);
        AccurateFpsPlusClient.avrColor = ConfigManager.config.avrColor;
        AccurateFpsPlusClient.avrAutoColor = ConfigManager.config.avrAutoColor;
        AccurateFpsPlusClient.showMax = ConfigManager.config.showMax;
        AccurateFpsPlusClient.maxPosition = ConfigManager.config.maxPosition;
        AccurateFpsPlusClient.maxPrecision = ConfigManager.config.maxPrecision;
        AccurateFpsPlusClient.beforeMax = ConfigManager.config.beforeMax;
        AccurateFpsPlusClient.afterMax = ConfigManager.config.afterMax;
        AccurateFpsPlusClient.maxUpdateInterval = Math.max(1, ConfigManager.config.maxUpdateInterval);
        AccurateFpsPlusClient.maxColor = ConfigManager.config.maxColor;
        AccurateFpsPlusClient.maxAutoColor = ConfigManager.config.maxAutoColor;
        AccurateFpsPlusClient.showMin = ConfigManager.config.showMin;
        AccurateFpsPlusClient.minPosition = ConfigManager.config.minPosition;
        AccurateFpsPlusClient.minPrecision = ConfigManager.config.minPrecision;
        AccurateFpsPlusClient.beforeMin = ConfigManager.config.beforeMin;
        AccurateFpsPlusClient.afterMin = ConfigManager.config.afterMin;
        AccurateFpsPlusClient.minUpdateInterval = Math.max(1, ConfigManager.config.minUpdateInterval);
        AccurateFpsPlusClient.minColor = ConfigManager.config.minColor;
        AccurateFpsPlusClient.minAutoColor = ConfigManager.config.minAutoColor;
        AccurateFpsPlusClient.showOnePercentLow = ConfigManager.config.showOnePercentLow;
        AccurateFpsPlusClient.onePercentLowPosition = ConfigManager.config.onePercentLowPosition;
        AccurateFpsPlusClient.onePercentLowPrecision = ConfigManager.config.onePercentLowPrecision;
        AccurateFpsPlusClient.beforeOnePercentLow = ConfigManager.config.beforeOnePercentLow;
        AccurateFpsPlusClient.afterOnePercentLow = ConfigManager.config.afterOnePercentLow;
        AccurateFpsPlusClient.onePercentLowUpdateInterval = Math.max(1, ConfigManager.config.onePercentLowUpdateInterval);
        AccurateFpsPlusClient.onePercentLowColor = ConfigManager.config.onePercentLowColor;
        AccurateFpsPlusClient.onePercentLowAutoColor = ConfigManager.config.onePercentLowAutoColor;
        AccurateFpsPlusClient.showZeroOnePercentLow = ConfigManager.config.showZeroOnePercentLow;
        AccurateFpsPlusClient.zeroOnePercentLowPosition = ConfigManager.config.zeroOnePercentLowPosition;
        AccurateFpsPlusClient.zeroOnePercentLowPrecision = ConfigManager.config.zeroOnePercentLowPrecision;
        AccurateFpsPlusClient.beforeZeroOnePercentLow = ConfigManager.config.beforeZeroOnePercentLow;
        AccurateFpsPlusClient.afterZeroOnePercentLow = ConfigManager.config.afterZeroOnePercentLow;
        AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval = Math.max(1, ConfigManager.config.zeroOnePercentLowUpdateInterval);
        AccurateFpsPlusClient.zeroOnePercentLowColor = ConfigManager.config.zeroOnePercentLowColor;
        AccurateFpsPlusClient.zeroOnePercentLowAutoColor = ConfigManager.config.zeroOnePercentLowAutoColor;
        AccurateFpsPlusClient.colorThresholdMode = ConfigManager.config.colorThresholdMode;
        AccurateFpsPlusClient.gradientColorLow = ConfigManager.config.gradientColorLow;
        AccurateFpsPlusClient.gradientColorMed = ConfigManager.config.gradientColorMed;
        AccurateFpsPlusClient.gradientColorHigh = ConfigManager.config.gradientColorHigh;
        AccurateFpsPlusClient.manualThresholdLow = ConfigManager.config.manualThresholdLow;
        AccurateFpsPlusClient.manualThresholdMed = ConfigManager.config.manualThresholdMed;
        AccurateFpsPlusClient.manualThresholdHigh = ConfigManager.config.manualThresholdHigh;
        ConfigManager.migrateConfig();
        ConfigManager.normalizeRuntimeConfig();
        AccurateFpsPlusClient.telemetryEngine.setCalculationMode(AccurateFpsPlusClient.calculationMode);
        AccurateFpsPlusClient.telemetryEngine.setComputePercentiles(AccurateFpsPlusClient.showOnePercentLow || AccurateFpsPlusClient.showZeroOnePercentLow);
        AccurateFpsPlusClient.markHudDirty();
    }

    private static void migrateConfig() {
        boolean migrated = false;
        if (ConfigManager.config.configVersion < 1) {
            if (ConfigManager.config.textColor != 0 && ConfigManager.config.fpsColor == 0 && ConfigManager.config.avrColor == 0 && ConfigManager.config.maxColor == 0 && ConfigManager.config.minColor == 0) {
                AccurateFpsPlusClient.fpsColor = ConfigManager.config.textColor;
                AccurateFpsPlusClient.avrColor = ConfigManager.config.textColor;
                AccurateFpsPlusClient.maxColor = ConfigManager.config.textColor;
                AccurateFpsPlusClient.minColor = ConfigManager.config.textColor;
                AccurateFpsPlusClient.fpsAutoColor = ConfigManager.config.colorCodeFps;
            }
            if (AccurateFpsPlusClient.beforeAvr != null && AccurateFpsPlusClient.beforeAvr.equalsIgnoreCase("Avr: ")) {
                AccurateFpsPlusClient.beforeAvr = "AVG: ";
            }
            if (AccurateFpsPlusClient.beforeMax != null && AccurateFpsPlusClient.beforeMax.equalsIgnoreCase("Max: ")) {
                AccurateFpsPlusClient.beforeMax = "MAX: ";
            }
            if (AccurateFpsPlusClient.beforeMin != null && AccurateFpsPlusClient.beforeMin.equalsIgnoreCase("Min: ")) {
                AccurateFpsPlusClient.beforeMin = "MIN: ";
            }
            if (" | ".equals(AccurateFpsPlusClient.afterFps)) {
                AccurateFpsPlusClient.afterFps = "";
            }
            if (" | ".equals(AccurateFpsPlusClient.afterAvr)) {
                AccurateFpsPlusClient.afterAvr = "";
            }
            if (" | ".equals(AccurateFpsPlusClient.afterMax)) {
                AccurateFpsPlusClient.afterMax = "";
            }
            if (" | ".equals(AccurateFpsPlusClient.afterMin)) {
                AccurateFpsPlusClient.afterMin = "";
            }
            if (ConfigManager.config.calculationMode == 0) {
                AccurateFpsPlusClient.calculationMode = 1;
            }
            migrated = true;
        }
        if (ConfigManager.config.configVersion < 2) {
            if (AccurateFpsPlusClient.onePercentLowPosition < 1 || AccurateFpsPlusClient.onePercentLowPosition > 2) {
                AccurateFpsPlusClient.onePercentLowPosition = 1;
            }
            if (AccurateFpsPlusClient.zeroOnePercentLowPosition < 1 || AccurateFpsPlusClient.zeroOnePercentLowPosition > 2) {
                AccurateFpsPlusClient.zeroOnePercentLowPosition = 2;
            }
            if (AccurateFpsPlusClient.beforeOnePercentLow == null) {
                AccurateFpsPlusClient.beforeOnePercentLow = "1% LOW: ";
            }
            if (AccurateFpsPlusClient.beforeZeroOnePercentLow == null) {
                AccurateFpsPlusClient.beforeZeroOnePercentLow = "0.1% LOW: ";
            }
            if (AccurateFpsPlusClient.afterOnePercentLow == null) {
                AccurateFpsPlusClient.afterOnePercentLow = "";
            }
            if (AccurateFpsPlusClient.afterZeroOnePercentLow == null) {
                AccurateFpsPlusClient.afterZeroOnePercentLow = "";
            }
            if (AccurateFpsPlusClient.onePercentLowUpdateInterval < 1) {
                AccurateFpsPlusClient.onePercentLowUpdateInterval = 2000;
            }
            if (AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval < 1) {
                AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval = 2000;
            }
            migrated = true;
        }
        if (ConfigManager.config.configVersion < 3) {
            if (AccurateFpsPlusClient.gradientColorLow == 0) {
                AccurateFpsPlusClient.gradientColorLow = 0xFF5555;
            }
            if (AccurateFpsPlusClient.gradientColorMed == 0) {
                AccurateFpsPlusClient.gradientColorMed = 0xFFFF55;
            }
            if (AccurateFpsPlusClient.gradientColorHigh == 0) {
                AccurateFpsPlusClient.gradientColorHigh = 0x55FF55;
            }
            if (AccurateFpsPlusClient.manualThresholdLow <= 0) {
                AccurateFpsPlusClient.manualThresholdLow = 30;
            }
            if (AccurateFpsPlusClient.manualThresholdMed <= 0) {
                AccurateFpsPlusClient.manualThresholdMed = 60;
            }
            if (AccurateFpsPlusClient.manualThresholdHigh <= 0) {
                AccurateFpsPlusClient.manualThresholdHigh = 120;
            }
            migrated = true;
        }
        if (ConfigManager.config.configVersion < 4) {
            if (AccurateFpsPlusClient.frametimePosition < 1 || AccurateFpsPlusClient.frametimePosition > 3) {
                AccurateFpsPlusClient.frametimePosition = 3;
            }
            if (AccurateFpsPlusClient.beforeFrametime == null) {
                AccurateFpsPlusClient.beforeFrametime = "FT: ";
            }
            if (AccurateFpsPlusClient.afterFrametime == null) {
                AccurateFpsPlusClient.afterFrametime = "ms";
            }
            migrated = true;
        }
        if (migrated) {
            ConfigManager.config.configVersion = CURRENT_CONFIG_VERSION;
            ConfigManager.saveConfig();
        }
    }

    protected static void saveConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Config currentConfig = new Config();
        currentConfig.configVersion = CURRENT_CONFIG_VERSION;
        currentConfig.toggleHUD = AccurateFpsPlusClient.toggleHUD;
        currentConfig.hideWithF3 = AccurateFpsPlusClient.hideWithF3;
        currentConfig.xPos = AccurateFpsPlusClient.xPos;
        currentConfig.yPos = AccurateFpsPlusClient.yPos;
        currentConfig.shadow = AccurateFpsPlusClient.shadow;
        currentConfig.pollingRate = AccurateFpsPlusClient.pollingRate;
        currentConfig.calculationMode = AccurateFpsPlusClient.calculationMode;
        currentConfig.historyWindowSeconds = AccurateFpsPlusClient.historyWindowSeconds;
        currentConfig.anchorCorner = AccurateFpsPlusClient.anchorCorner;
        currentConfig.darkModeOverlay = AccurateFpsPlusClient.darkModeOverlay;
        currentConfig.overlayColor = AccurateFpsPlusClient.overlayColor;
        currentConfig.overlayTransparency = AccurateFpsPlusClient.overlayTransparency;
        currentConfig.currentPreset = AccurateFpsPlusClient.currentPreset;
        currentConfig.showFps = AccurateFpsPlusClient.showFps;
        currentConfig.fpsPosition = AccurateFpsPlusClient.fpsPosition;
        currentConfig.fpsPrecision = AccurateFpsPlusClient.fpsPrecision;
        currentConfig.beforeFps = AccurateFpsPlusClient.beforeFps;
        currentConfig.afterFps = AccurateFpsPlusClient.afterFps;
        currentConfig.mainUpdateInterval = AccurateFpsPlusClient.mainUpdateInterval;
        currentConfig.fpsColor = AccurateFpsPlusClient.fpsColor;
        currentConfig.fpsAutoColor = AccurateFpsPlusClient.fpsAutoColor;
        currentConfig.showFrametime = AccurateFpsPlusClient.showFrametime;
        currentConfig.frametimePosition = AccurateFpsPlusClient.frametimePosition;
        currentConfig.frametimePrecision = AccurateFpsPlusClient.frametimePrecision;
        currentConfig.beforeFrametime = AccurateFpsPlusClient.beforeFrametime;
        currentConfig.afterFrametime = AccurateFpsPlusClient.afterFrametime;
        currentConfig.frametimeColor = AccurateFpsPlusClient.frametimeColor;
        currentConfig.frametimeAutoColor = AccurateFpsPlusClient.frametimeAutoColor;
        currentConfig.showAvr = AccurateFpsPlusClient.showAvr;
        currentConfig.avrPosition = AccurateFpsPlusClient.avrPosition;
        currentConfig.avrPrecision = AccurateFpsPlusClient.avrPrecision;
        currentConfig.beforeAvr = AccurateFpsPlusClient.beforeAvr;
        currentConfig.afterAvr = AccurateFpsPlusClient.afterAvr;
        currentConfig.avrUpdateInterval = AccurateFpsPlusClient.avrUpdateInterval;
        currentConfig.avrColor = AccurateFpsPlusClient.avrColor;
        currentConfig.avrAutoColor = AccurateFpsPlusClient.avrAutoColor;
        currentConfig.showMax = AccurateFpsPlusClient.showMax;
        currentConfig.maxPosition = AccurateFpsPlusClient.maxPosition;
        currentConfig.maxPrecision = AccurateFpsPlusClient.maxPrecision;
        currentConfig.beforeMax = AccurateFpsPlusClient.beforeMax;
        currentConfig.afterMax = AccurateFpsPlusClient.afterMax;
        currentConfig.maxUpdateInterval = AccurateFpsPlusClient.maxUpdateInterval;
        currentConfig.maxColor = AccurateFpsPlusClient.maxColor;
        currentConfig.maxAutoColor = AccurateFpsPlusClient.maxAutoColor;
        currentConfig.showMin = AccurateFpsPlusClient.showMin;
        currentConfig.minPosition = AccurateFpsPlusClient.minPosition;
        currentConfig.minPrecision = AccurateFpsPlusClient.minPrecision;
        currentConfig.beforeMin = AccurateFpsPlusClient.beforeMin;
        currentConfig.afterMin = AccurateFpsPlusClient.afterMin;
        currentConfig.minUpdateInterval = AccurateFpsPlusClient.minUpdateInterval;
        currentConfig.minColor = AccurateFpsPlusClient.minColor;
        currentConfig.minAutoColor = AccurateFpsPlusClient.minAutoColor;
        currentConfig.showOnePercentLow = AccurateFpsPlusClient.showOnePercentLow;
        currentConfig.onePercentLowPosition = AccurateFpsPlusClient.onePercentLowPosition;
        currentConfig.onePercentLowPrecision = AccurateFpsPlusClient.onePercentLowPrecision;
        currentConfig.beforeOnePercentLow = AccurateFpsPlusClient.beforeOnePercentLow;
        currentConfig.afterOnePercentLow = AccurateFpsPlusClient.afterOnePercentLow;
        currentConfig.onePercentLowUpdateInterval = AccurateFpsPlusClient.onePercentLowUpdateInterval;
        currentConfig.onePercentLowColor = AccurateFpsPlusClient.onePercentLowColor;
        currentConfig.onePercentLowAutoColor = AccurateFpsPlusClient.onePercentLowAutoColor;
        currentConfig.showZeroOnePercentLow = AccurateFpsPlusClient.showZeroOnePercentLow;
        currentConfig.zeroOnePercentLowPosition = AccurateFpsPlusClient.zeroOnePercentLowPosition;
        currentConfig.zeroOnePercentLowPrecision = AccurateFpsPlusClient.zeroOnePercentLowPrecision;
        currentConfig.beforeZeroOnePercentLow = AccurateFpsPlusClient.beforeZeroOnePercentLow;
        currentConfig.afterZeroOnePercentLow = AccurateFpsPlusClient.afterZeroOnePercentLow;
        currentConfig.zeroOnePercentLowUpdateInterval = AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval;
        currentConfig.zeroOnePercentLowColor = AccurateFpsPlusClient.zeroOnePercentLowColor;
        currentConfig.zeroOnePercentLowAutoColor = AccurateFpsPlusClient.zeroOnePercentLowAutoColor;
        currentConfig.colorThresholdMode = AccurateFpsPlusClient.colorThresholdMode;
        currentConfig.gradientColorLow = AccurateFpsPlusClient.gradientColorLow;
        currentConfig.gradientColorMed = AccurateFpsPlusClient.gradientColorMed;
        currentConfig.gradientColorHigh = AccurateFpsPlusClient.gradientColorHigh;
        currentConfig.manualThresholdLow = AccurateFpsPlusClient.manualThresholdLow;
        currentConfig.manualThresholdMed = AccurateFpsPlusClient.manualThresholdMed;
        currentConfig.manualThresholdHigh = AccurateFpsPlusClient.manualThresholdHigh;
        try {
            Files.createDirectories(configPath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
                gson.toJson(currentConfig, writer);
            }
        }
        catch (IOException exception) {
            LOGGER.warn("Could not save configuration at {}; keeping the current settings in memory.", configPath, exception);
        }
    }

    private static void normalizeRuntimeConfig() {
        AccurateFpsPlusClient.calculationMode = Math.max(0, Math.min(2, AccurateFpsPlusClient.calculationMode));
        AccurateFpsPlusClient.anchorCorner = Math.max(0, Math.min(4, AccurateFpsPlusClient.anchorCorner));
        AccurateFpsPlusClient.overlayColor &= 0xFFFFFF;
        AccurateFpsPlusClient.overlayTransparency = Math.max(0, Math.min(100, AccurateFpsPlusClient.overlayTransparency));
        AccurateFpsPlusClient.fpsPosition = Math.max(1, Math.min(4, AccurateFpsPlusClient.fpsPosition));
        AccurateFpsPlusClient.avrPosition = Math.max(1, Math.min(4, AccurateFpsPlusClient.avrPosition));
        AccurateFpsPlusClient.maxPosition = Math.max(1, Math.min(4, AccurateFpsPlusClient.maxPosition));
        AccurateFpsPlusClient.minPosition = Math.max(1, Math.min(4, AccurateFpsPlusClient.minPosition));
        AccurateFpsPlusClient.onePercentLowPosition = Math.max(1, Math.min(3, AccurateFpsPlusClient.onePercentLowPosition));
        AccurateFpsPlusClient.zeroOnePercentLowPosition = Math.max(1, Math.min(3, AccurateFpsPlusClient.zeroOnePercentLowPosition));
        AccurateFpsPlusClient.frametimePosition = Math.max(1, Math.min(3, AccurateFpsPlusClient.frametimePosition == 0 ? 3 : AccurateFpsPlusClient.frametimePosition));
        AccurateFpsPlusClient.fpsPrecision = Math.max(0, Math.min(4, AccurateFpsPlusClient.fpsPrecision));
        AccurateFpsPlusClient.frametimePrecision = Math.max(0, Math.min(4, AccurateFpsPlusClient.frametimePrecision));
        AccurateFpsPlusClient.avrPrecision = Math.max(0, Math.min(4, AccurateFpsPlusClient.avrPrecision));
        AccurateFpsPlusClient.maxPrecision = Math.max(0, Math.min(4, AccurateFpsPlusClient.maxPrecision));
        AccurateFpsPlusClient.minPrecision = Math.max(0, Math.min(4, AccurateFpsPlusClient.minPrecision));
        AccurateFpsPlusClient.onePercentLowPrecision = Math.max(0, Math.min(4, AccurateFpsPlusClient.onePercentLowPrecision));
        AccurateFpsPlusClient.zeroOnePercentLowPrecision = Math.max(0, Math.min(4, AccurateFpsPlusClient.zeroOnePercentLowPrecision));
        AccurateFpsPlusClient.fpsColor &= 0xFFFFFF;
        AccurateFpsPlusClient.frametimeColor &= 0xFFFFFF;
        AccurateFpsPlusClient.avrColor &= 0xFFFFFF;
        AccurateFpsPlusClient.maxColor &= 0xFFFFFF;
        AccurateFpsPlusClient.minColor &= 0xFFFFFF;
        AccurateFpsPlusClient.onePercentLowColor &= 0xFFFFFF;
        AccurateFpsPlusClient.zeroOnePercentLowColor &= 0xFFFFFF;
        AccurateFpsPlusClient.beforeFps = ConfigManager.emptyIfNull(AccurateFpsPlusClient.beforeFps);
        AccurateFpsPlusClient.afterFps = ConfigManager.emptyIfNull(AccurateFpsPlusClient.afterFps);
        AccurateFpsPlusClient.beforeFrametime = ConfigManager.emptyIfNull(AccurateFpsPlusClient.beforeFrametime != null ? AccurateFpsPlusClient.beforeFrametime : "FT: ");
        AccurateFpsPlusClient.afterFrametime = ConfigManager.emptyIfNull(AccurateFpsPlusClient.afterFrametime != null ? AccurateFpsPlusClient.afterFrametime : "ms");
        AccurateFpsPlusClient.beforeAvr = ConfigManager.emptyIfNull(AccurateFpsPlusClient.beforeAvr);
        AccurateFpsPlusClient.afterAvr = ConfigManager.emptyIfNull(AccurateFpsPlusClient.afterAvr);
        AccurateFpsPlusClient.beforeMax = ConfigManager.emptyIfNull(AccurateFpsPlusClient.beforeMax);
        AccurateFpsPlusClient.afterMax = ConfigManager.emptyIfNull(AccurateFpsPlusClient.afterMax);
        AccurateFpsPlusClient.beforeMin = ConfigManager.emptyIfNull(AccurateFpsPlusClient.beforeMin);
        AccurateFpsPlusClient.afterMin = ConfigManager.emptyIfNull(AccurateFpsPlusClient.afterMin);
        AccurateFpsPlusClient.beforeOnePercentLow = ConfigManager.emptyIfNull(AccurateFpsPlusClient.beforeOnePercentLow);
        AccurateFpsPlusClient.afterOnePercentLow = ConfigManager.emptyIfNull(AccurateFpsPlusClient.afterOnePercentLow);
        AccurateFpsPlusClient.beforeZeroOnePercentLow = ConfigManager.emptyIfNull(AccurateFpsPlusClient.beforeZeroOnePercentLow);
        AccurateFpsPlusClient.afterZeroOnePercentLow = ConfigManager.emptyIfNull(AccurateFpsPlusClient.afterZeroOnePercentLow);
        AccurateFpsPlusClient.colorThresholdMode = Math.max(0, Math.min(1, AccurateFpsPlusClient.colorThresholdMode));
        AccurateFpsPlusClient.gradientColorLow &= 0xFFFFFF;
        AccurateFpsPlusClient.gradientColorMed &= 0xFFFFFF;
        AccurateFpsPlusClient.gradientColorHigh &= 0xFFFFFF;
        if (AccurateFpsPlusClient.gradientColorLow == 0 && AccurateFpsPlusClient.gradientColorMed == 0 && AccurateFpsPlusClient.gradientColorHigh == 0) {
            AccurateFpsPlusClient.gradientColorLow = 0xFF5555;
            AccurateFpsPlusClient.gradientColorMed = 0xFFFF55;
            AccurateFpsPlusClient.gradientColorHigh = 0x55FF55;
        }
        AccurateFpsPlusClient.manualThresholdLow = Math.max(1, Math.min(1000, AccurateFpsPlusClient.manualThresholdLow));
        AccurateFpsPlusClient.manualThresholdMed = Math.max(1, Math.min(1000, AccurateFpsPlusClient.manualThresholdMed));
        AccurateFpsPlusClient.manualThresholdHigh = Math.max(1, Math.min(1000, AccurateFpsPlusClient.manualThresholdHigh));
        if (AccurateFpsPlusClient.manualThresholdMed <= AccurateFpsPlusClient.manualThresholdLow) {
            AccurateFpsPlusClient.manualThresholdMed = AccurateFpsPlusClient.manualThresholdLow + 1;
        }
        if (AccurateFpsPlusClient.manualThresholdHigh <= AccurateFpsPlusClient.manualThresholdMed) {
            AccurateFpsPlusClient.manualThresholdHigh = AccurateFpsPlusClient.manualThresholdMed + 1;
        }
    }

    private static String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    static {
        configPath = FabricLoader.getInstance().getConfigDir().resolve("accuratefpsplus.json");
    }

    @Environment(value=EnvType.CLIENT)
    private static class Config {
        int configVersion;
        boolean toggleHUD;
        boolean hideWithF3;
        int xPos;
        int yPos;
        boolean shadow;
        int pollingRate;
        int calculationMode;
        int historyWindowSeconds;
        int anchorCorner;
        boolean darkModeOverlay;
        int overlayColor;
        int overlayTransparency;
        int currentPreset;
        boolean showFps;
        int fpsPosition;
        int fpsPrecision;
        String beforeFps;
        String afterFps;
        int mainUpdateInterval;
        int fpsColor;
        boolean fpsAutoColor;
        boolean showFrametime;
        int frametimePosition;
        int frametimePrecision;
        String beforeFrametime;
        String afterFrametime;
        int frametimeColor;
        boolean frametimeAutoColor;
        boolean showAvr;
        int avrPosition;
        int avrPrecision;
        String beforeAvr;
        String afterAvr;
        int avrUpdateInterval;
        int avrColor;
        boolean avrAutoColor;
        boolean showMax;
        int maxPosition;
        int maxPrecision;
        String beforeMax;
        String afterMax;
        int maxUpdateInterval;
        int maxColor;
        boolean maxAutoColor;
        boolean showMin;
        int minPosition;
        int minPrecision;
        String beforeMin;
        String afterMin;
        int minUpdateInterval;
        int minColor;
        boolean minAutoColor;
        boolean showOnePercentLow;
        int onePercentLowPosition;
        int onePercentLowPrecision;
        String beforeOnePercentLow;
        String afterOnePercentLow;
        int onePercentLowUpdateInterval;
        int onePercentLowColor;
        boolean onePercentLowAutoColor;
        boolean showZeroOnePercentLow;
        int zeroOnePercentLowPosition;
        int zeroOnePercentLowPrecision;
        String beforeZeroOnePercentLow;
        String afterZeroOnePercentLow;
        int zeroOnePercentLowUpdateInterval;
        int zeroOnePercentLowColor;
        boolean zeroOnePercentLowAutoColor;
        int colorThresholdMode;
        int gradientColorLow;
        int gradientColorMed;
        int gradientColorHigh;
        int manualThresholdLow;
        int manualThresholdMed;
        int manualThresholdHigh;
        int textColor;
        boolean colorCodeFps;

        private Config() {
        }
    }
}
