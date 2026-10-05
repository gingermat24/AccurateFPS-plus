package com.accuratefpsplus;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class YaclConfigScreen {
    private static final int[] HISTORY_WINDOW_OPTIONS = new int[]{1, 3, 5, 10, 30, 60};

    public static Screen getConfigScreen(Screen parent) {
        int initialPreset = AccurateFpsPlusClient.currentPreset;
        int[] selectedPreset = new int[]{initialPreset};
        boolean[] customVisibilityChanged = new boolean[]{false};

        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("title.accuratefpsplus.config"))
            .save(() -> {
                if (selectedPreset[0] > 0 && selectedPreset[0] != initialPreset) {
                    applyPreset(selectedPreset[0]);
                    AccurateFpsPlusClient.currentPreset = selectedPreset[0];
                } else if (customVisibilityChanged[0]) {
                    AccurateFpsPlusClient.currentPreset = 0;
                }
                ConfigManager.saveConfig();
                AccurateFpsPlusClient.telemetryEngine.setComputePercentiles(
                    AccurateFpsPlusClient.showOnePercentLow || AccurateFpsPlusClient.showZeroOnePercentLow
                );
            });

        // 1. General Category
        ConfigCategory generalCategory = ConfigCategory.createBuilder()
            .name(Component.translatable("category.accuratefpsplus.general"))
            .option(Option.<Boolean>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.toggleHUD"))
                .binding(true, () -> AccurateFpsPlusClient.toggleHUD, v -> AccurateFpsPlusClient.toggleHUD = v)
                .controller(TickBoxControllerBuilder::create)
                .build())
            .option(Option.<Boolean>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.hideWithF3"))
                .description(OptionDescription.of(Component.translatable("tooltip.accuratefpsplus.hideWithF3")))
                .binding(false, () -> AccurateFpsPlusClient.hideWithF3, v -> AccurateFpsPlusClient.hideWithF3 = v)
                .controller(TickBoxControllerBuilder::create)
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.presetProfile"))
                .description(OptionDescription.of(Component.translatable("tooltip.accuratefpsplus.presetProfile")))
                .binding(1, () -> AccurateFpsPlusClient.currentPreset, v -> {
                    AccurateFpsPlusClient.currentPreset = v;
                    selectedPreset[0] = v;
                })
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 3).step(1).formatValue(YaclConfigScreen::presetLabel))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.anchorCorner"))
                .binding(0, () -> AccurateFpsPlusClient.anchorCorner, v -> AccurateFpsPlusClient.anchorCorner = v)
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 4).step(1).formatValue(YaclConfigScreen::anchorCornerLabel))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.xPos"))
                .binding(5, () -> AccurateFpsPlusClient.xPos, v -> AccurateFpsPlusClient.xPos = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(0).max(0x7FFFFFFE))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.yPos"))
                .binding(5, () -> AccurateFpsPlusClient.yPos, v -> AccurateFpsPlusClient.yPos = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(0).max(0x7FFFFFFE))
                .build())
            .option(Option.<Boolean>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.shadow"))
                .binding(true, () -> AccurateFpsPlusClient.shadow, v -> AccurateFpsPlusClient.shadow = v)
                .controller(TickBoxControllerBuilder::create)
                .build())
            .option(Option.<Boolean>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.darkModeOverlay"))
                .binding(false, () -> AccurateFpsPlusClient.darkModeOverlay, v -> AccurateFpsPlusClient.darkModeOverlay = v)
                .controller(TickBoxControllerBuilder::create)
                .build())
            .option(Option.<Color>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.overlayColor"))
                .binding(new Color(0x000000), () -> new Color(AccurateFpsPlusClient.overlayColor), c -> {
                    AccurateFpsPlusClient.overlayColor = c.getRGB() & 0xFFFFFF;
                    AccurateFpsPlusClient.updateCachedOverlayColor();
                })
                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.overlayTransparency"))
                .binding(50, () -> AccurateFpsPlusClient.overlayTransparency, v -> {
                    AccurateFpsPlusClient.overlayTransparency = v;
                    AccurateFpsPlusClient.updateCachedOverlayColor();
                })
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 100).step(1))
                .build())
            .build();

        // 2. Metrics Category
        ConfigCategory metricsCategory = ConfigCategory.createBuilder()
            .name(Component.translatable("category.accuratefpsplus.metrics"))
            .group(createMetricGroup(
                "metricsMain",
                "showFps", true, () -> AccurateFpsPlusClient.showFps, v -> AccurateFpsPlusClient.showFps = v,
                "fpsPosition", 1, 4, () -> AccurateFpsPlusClient.fpsPosition, v -> AccurateFpsPlusClient.fpsPosition = v,
                "fpsPrecision", 0, () -> AccurateFpsPlusClient.fpsPrecision, v -> AccurateFpsPlusClient.fpsPrecision = v,
                "beforeFps", "FPS: ", () -> AccurateFpsPlusClient.beforeFps, v -> AccurateFpsPlusClient.beforeFps = v,
                "afterFps", "", () -> AccurateFpsPlusClient.afterFps, v -> AccurateFpsPlusClient.afterFps = v,
                "fpsColor", () -> AccurateFpsPlusClient.fpsColor, v -> AccurateFpsPlusClient.fpsColor = v,
                "fpsAutoColor", true, () -> AccurateFpsPlusClient.fpsAutoColor, v -> AccurateFpsPlusClient.fpsAutoColor = v,
                () -> customVisibilityChanged[0] = true
            ))
            .group(createMetricGroup(
                "metricsFrametime",
                "showFrametime", false, () -> AccurateFpsPlusClient.showFrametime, v -> AccurateFpsPlusClient.showFrametime = v,
                "frametimePosition", 3, 3, () -> AccurateFpsPlusClient.frametimePosition, v -> AccurateFpsPlusClient.frametimePosition = v,
                "frametimePrecision", 1, () -> AccurateFpsPlusClient.frametimePrecision, v -> AccurateFpsPlusClient.frametimePrecision = v,
                "beforeFrametime", "FT: ", () -> AccurateFpsPlusClient.beforeFrametime, v -> AccurateFpsPlusClient.beforeFrametime = v,
                "afterFrametime", "ms", () -> AccurateFpsPlusClient.afterFrametime, v -> AccurateFpsPlusClient.afterFrametime = v,
                "frametimeColor", () -> AccurateFpsPlusClient.frametimeColor, v -> AccurateFpsPlusClient.frametimeColor = v,
                "frametimeAutoColor", false, () -> AccurateFpsPlusClient.frametimeAutoColor, v -> AccurateFpsPlusClient.frametimeAutoColor = v,
                () -> customVisibilityChanged[0] = true
            ))
            .group(createMetricGroup(
                "metricsAvg",
                "showAvr", false, () -> AccurateFpsPlusClient.showAvr, v -> AccurateFpsPlusClient.showAvr = v,
                "avrPosition", 2, 4, () -> AccurateFpsPlusClient.avrPosition, v -> AccurateFpsPlusClient.avrPosition = v,
                "avrPrecision", 0, () -> AccurateFpsPlusClient.avrPrecision, v -> AccurateFpsPlusClient.avrPrecision = v,
                "beforeAvr", "AVG: ", () -> AccurateFpsPlusClient.beforeAvr, v -> AccurateFpsPlusClient.beforeAvr = v,
                "afterAvr", "", () -> AccurateFpsPlusClient.afterAvr, v -> AccurateFpsPlusClient.afterAvr = v,
                "avrColor", () -> AccurateFpsPlusClient.avrColor, v -> AccurateFpsPlusClient.avrColor = v,
                "avrAutoColor", false, () -> AccurateFpsPlusClient.avrAutoColor, v -> AccurateFpsPlusClient.avrAutoColor = v,
                () -> customVisibilityChanged[0] = true
            ))
            .group(createMetricGroup(
                "metricsMax",
                "showMax", false, () -> AccurateFpsPlusClient.showMax, v -> AccurateFpsPlusClient.showMax = v,
                "maxPosition", 3, 4, () -> AccurateFpsPlusClient.maxPosition, v -> AccurateFpsPlusClient.maxPosition = v,
                "maxPrecision", 0, () -> AccurateFpsPlusClient.maxPrecision, v -> AccurateFpsPlusClient.maxPrecision = v,
                "beforeMax", "MAX: ", () -> AccurateFpsPlusClient.beforeMax, v -> AccurateFpsPlusClient.beforeMax = v,
                "afterMax", "", () -> AccurateFpsPlusClient.afterMax, v -> AccurateFpsPlusClient.afterMax = v,
                "maxColor", () -> AccurateFpsPlusClient.maxColor, v -> AccurateFpsPlusClient.maxColor = v,
                "maxAutoColor", false, () -> AccurateFpsPlusClient.maxAutoColor, v -> AccurateFpsPlusClient.maxAutoColor = v,
                () -> customVisibilityChanged[0] = true
            ))
            .group(createMetricGroup(
                "metricsMin",
                "showMin", false, () -> AccurateFpsPlusClient.showMin, v -> AccurateFpsPlusClient.showMin = v,
                "minPosition", 4, 4, () -> AccurateFpsPlusClient.minPosition, v -> AccurateFpsPlusClient.minPosition = v,
                "minPrecision", 0, () -> AccurateFpsPlusClient.minPrecision, v -> AccurateFpsPlusClient.minPrecision = v,
                "beforeMin", "MIN: ", () -> AccurateFpsPlusClient.beforeMin, v -> AccurateFpsPlusClient.beforeMin = v,
                "afterMin", "", () -> AccurateFpsPlusClient.afterMin, v -> AccurateFpsPlusClient.afterMin = v,
                "minColor", () -> AccurateFpsPlusClient.minColor, v -> AccurateFpsPlusClient.minColor = v,
                "minAutoColor", false, () -> AccurateFpsPlusClient.minAutoColor, v -> AccurateFpsPlusClient.minAutoColor = v,
                () -> customVisibilityChanged[0] = true
            ))
            .group(createMetricGroup(
                "metricsOnePercentLow",
                "showOnePercentLow", false, () -> AccurateFpsPlusClient.showOnePercentLow, v -> AccurateFpsPlusClient.showOnePercentLow = v,
                "onePercentLowPosition", 1, 3, () -> AccurateFpsPlusClient.onePercentLowPosition, v -> AccurateFpsPlusClient.onePercentLowPosition = v,
                "onePercentLowPrecision", 0, () -> AccurateFpsPlusClient.onePercentLowPrecision, v -> AccurateFpsPlusClient.onePercentLowPrecision = v,
                "beforeOnePercentLow", "1% LOW: ", () -> AccurateFpsPlusClient.beforeOnePercentLow, v -> AccurateFpsPlusClient.beforeOnePercentLow = v,
                "afterOnePercentLow", "", () -> AccurateFpsPlusClient.afterOnePercentLow, v -> AccurateFpsPlusClient.afterOnePercentLow = v,
                "onePercentLowColor", () -> AccurateFpsPlusClient.onePercentLowColor, v -> AccurateFpsPlusClient.onePercentLowColor = v,
                "onePercentLowAutoColor", false, () -> AccurateFpsPlusClient.onePercentLowAutoColor, v -> AccurateFpsPlusClient.onePercentLowAutoColor = v,
                () -> customVisibilityChanged[0] = true
            ))
            .group(createMetricGroup(
                "metricsZeroOnePercentLow",
                "showZeroOnePercentLow", false, () -> AccurateFpsPlusClient.showZeroOnePercentLow, v -> AccurateFpsPlusClient.showZeroOnePercentLow = v,
                "zeroOnePercentLowPosition", 2, 3, () -> AccurateFpsPlusClient.zeroOnePercentLowPosition, v -> AccurateFpsPlusClient.zeroOnePercentLowPosition = v,
                "zeroOnePercentLowPrecision", 0, () -> AccurateFpsPlusClient.zeroOnePercentLowPrecision, v -> AccurateFpsPlusClient.zeroOnePercentLowPrecision = v,
                "beforeZeroOnePercentLow", "0.1% LOW: ", () -> AccurateFpsPlusClient.beforeZeroOnePercentLow, v -> AccurateFpsPlusClient.beforeZeroOnePercentLow = v,
                "afterZeroOnePercentLow", "", () -> AccurateFpsPlusClient.afterZeroOnePercentLow, v -> AccurateFpsPlusClient.afterZeroOnePercentLow = v,
                "zeroOnePercentLowColor", () -> AccurateFpsPlusClient.zeroOnePercentLowColor, v -> AccurateFpsPlusClient.zeroOnePercentLowColor = v,
                "zeroOnePercentLowAutoColor", false, () -> AccurateFpsPlusClient.zeroOnePercentLowAutoColor, v -> AccurateFpsPlusClient.zeroOnePercentLowAutoColor = v,
                () -> customVisibilityChanged[0] = true
            ))
            .build();

        // 3. Coloring Category
        ConfigCategory coloringCategory = ConfigCategory.createBuilder()
            .name(Component.translatable("category.accuratefpsplus.coloring"))
            .option(Option.<ThresholdMode>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.colorThresholdMode"))
                .description(OptionDescription.of(Component.translatable("tooltip.accuratefpsplus.colorThresholdMode")))
                .binding(
                    ThresholdMode.AUTOMATIC,
                    () -> AccurateFpsPlusClient.colorThresholdMode == 1 ? ThresholdMode.MANUAL : ThresholdMode.AUTOMATIC,
                    mode -> AccurateFpsPlusClient.colorThresholdMode = (mode == ThresholdMode.MANUAL ? 1 : 0)
                )
                .controller(opt -> EnumControllerBuilder.create(opt)
                    .enumClass(ThresholdMode.class)
                    .formatValue(mode -> switch (mode) {
                        case MANUAL -> Component.translatable("value.accuratefpsplus.thresholdMode.manual");
                        default -> Component.translatable("value.accuratefpsplus.thresholdMode.automatic");
                    }))
                .build())
            .option(Option.<Color>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.gradientColorLow"))
                .binding(new Color(0xFF5555), () -> new Color(AccurateFpsPlusClient.gradientColorLow), c -> AccurateFpsPlusClient.gradientColorLow = c.getRGB() & 0xFFFFFF)
                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                .build())
            .option(Option.<Color>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.gradientColorMed"))
                .binding(new Color(0xFFFF55), () -> new Color(AccurateFpsPlusClient.gradientColorMed), c -> AccurateFpsPlusClient.gradientColorMed = c.getRGB() & 0xFFFFFF)
                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                .build())
            .option(Option.<Color>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.gradientColorHigh"))
                .binding(new Color(0x55FF55), () -> new Color(AccurateFpsPlusClient.gradientColorHigh), c -> AccurateFpsPlusClient.gradientColorHigh = c.getRGB() & 0xFFFFFF)
                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.manualThresholdLow"))
                .binding(30, () -> AccurateFpsPlusClient.manualThresholdLow, v -> AccurateFpsPlusClient.manualThresholdLow = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(1, 1000))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.manualThresholdMed"))
                .binding(60, () -> AccurateFpsPlusClient.manualThresholdMed, v -> AccurateFpsPlusClient.manualThresholdMed = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(1, 1000))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.manualThresholdHigh"))
                .binding(120, () -> AccurateFpsPlusClient.manualThresholdHigh, v -> AccurateFpsPlusClient.manualThresholdHigh = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(1, 1000))
                .build())
            .option(Option.<Boolean>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.detectDisplayHz"))
                .description(OptionDescription.of(Component.translatable("tooltip.accuratefpsplus.detectDisplayHz")))
                .binding(
                    false,
                    () -> false,
                    v -> {
                        if (v) {
                            int hz = AccurateFpsPlusClient.detectDisplayRefreshRate();
                            AccurateFpsPlusClient.manualThresholdHigh = hz;
                            AccurateFpsPlusClient.manualThresholdMed = Math.max(1, (int) Math.round(hz * 0.75));
                            AccurateFpsPlusClient.manualThresholdLow = Math.max(1, (int) Math.round(hz * 0.50));
                            AccurateFpsPlusClient.colorThresholdMode = 1;
                        }
                    }
                )
                .controller(TickBoxControllerBuilder::create)
                .build())
            .build();

        // 4. Engine Category
        ConfigCategory engineCategory = ConfigCategory.createBuilder()
            .name(Component.translatable("category.accuratefpsplus.engine"))
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.calculationMode"))
                .description(OptionDescription.of(Component.translatable("tooltip.accuratefpsplus.calculationMode")))
                .binding(1, () -> AccurateFpsPlusClient.calculationMode, v -> {
                    AccurateFpsPlusClient.calculationMode = v;
                    AccurateFpsPlusClient.telemetryEngine.setCalculationMode(v);
                })
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 2).step(1).formatValue(YaclConfigScreen::calculationModeLabel))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.historyWindow"))
                .description(OptionDescription.of(Component.translatable("tooltip.accuratefpsplus.historyWindow")))
                .binding(historyWindowToIndex(5), () -> historyWindowToIndex(AccurateFpsPlusClient.historyWindowSeconds), i -> AccurateFpsPlusClient.historyWindowSeconds = HISTORY_WINDOW_OPTIONS[i])
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, HISTORY_WINDOW_OPTIONS.length - 1).step(1).formatValue(i -> Component.literal(HISTORY_WINDOW_OPTIONS[i] + "s")))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.pollingRate"))
                .binding(100, () -> AccurateFpsPlusClient.pollingRate, v -> AccurateFpsPlusClient.pollingRate = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(1, 5000))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.mainUpdateInterval"))
                .binding(250, () -> AccurateFpsPlusClient.mainUpdateInterval, v -> AccurateFpsPlusClient.mainUpdateInterval = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(1, 3600000))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.avrUpdateInterval"))
                .binding(2000, () -> AccurateFpsPlusClient.avrUpdateInterval, v -> AccurateFpsPlusClient.avrUpdateInterval = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(1, 3600000))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.maxUpdateInterval"))
                .binding(2000, () -> AccurateFpsPlusClient.maxUpdateInterval, v -> AccurateFpsPlusClient.maxUpdateInterval = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(1, 3600000))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.minUpdateInterval"))
                .binding(2000, () -> AccurateFpsPlusClient.minUpdateInterval, v -> AccurateFpsPlusClient.minUpdateInterval = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(1, 3600000))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.onePercentLowUpdateInterval"))
                .binding(2000, () -> AccurateFpsPlusClient.onePercentLowUpdateInterval, v -> AccurateFpsPlusClient.onePercentLowUpdateInterval = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(1, 3600000))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus.zeroOnePercentLowUpdateInterval"))
                .binding(2000, () -> AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval, v -> AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval = v)
                .controller(opt -> IntegerFieldControllerBuilder.create(opt).range(1, 3600000))
                .build())
            .build();

        return builder
            .category(generalCategory)
            .category(metricsCategory)
            .category(coloringCategory)
            .category(engineCategory)
            .build()
            .generateScreen(parent);
    }

    private static OptionGroup createMetricGroup(
        String subCategoryKey,
        String showKey,
        boolean showDefault,
        Supplier<Boolean> showGet,
        Consumer<Boolean> showSet,
        String posKey,
        int posDefault,
        int posMax,
        Supplier<Integer> posGet,
        Consumer<Integer> posSet,
        String precKey,
        int precDefault,
        Supplier<Integer> precGet,
        Consumer<Integer> precSet,
        String beforeKey,
        String beforeDefault,
        Supplier<String> beforeGet,
        Consumer<String> beforeSet,
        String afterKey,
        String afterDefault,
        Supplier<String> afterGet,
        Consumer<String> afterSet,
        String colorKey,
        Supplier<Integer> colorGet,
        Consumer<Integer> colorSet,
        String autoColorKey,
        boolean autoColorDefault,
        Supplier<Boolean> autoColorGet,
        Consumer<Boolean> autoColorSet,
        Runnable markCustomVisibilityChanged
    ) {
        return OptionGroup.createBuilder()
            .name(Component.translatable("category.accuratefpsplus." + subCategoryKey))
            .collapsed(false)
            .option(Option.<Boolean>createBuilder()
                .name(Component.translatable("option.accuratefpsplus." + showKey))
                .binding(showDefault, showGet::get, v -> {
                    showSet.accept(v);
                    markCustomVisibilityChanged.run();
                })
                .controller(TickBoxControllerBuilder::create)
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus." + posKey))
                .binding(posDefault, posGet::get, posSet::accept)
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, posMax).step(1).formatValue(YaclConfigScreen::slotLabel))
                .build())
            .option(Option.<Integer>createBuilder()
                .name(Component.translatable("option.accuratefpsplus." + precKey))
                .binding(precDefault, precGet::get, precSet::accept)
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 4).step(1))
                .build())
            .option(Option.<String>createBuilder()
                .name(Component.translatable("option.accuratefpsplus." + beforeKey))
                .binding(beforeDefault, () -> beforeGet.get() != null ? beforeGet.get() : beforeDefault, beforeSet::accept)
                .controller(StringControllerBuilder::create)
                .build())
            .option(Option.<String>createBuilder()
                .name(Component.translatable("option.accuratefpsplus." + afterKey))
                .binding(afterDefault, () -> afterGet.get() != null ? afterGet.get() : afterDefault, afterSet::accept)
                .controller(StringControllerBuilder::create)
                .build())
            .option(Option.<Color>createBuilder()
                .name(Component.translatable("option.accuratefpsplus." + colorKey))
                .binding(new Color(0xFFFFFF), () -> new Color(colorGet.get()), c -> colorSet.accept(c.getRGB() & 0xFFFFFF))
                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(false))
                .build())
            .option(Option.<Boolean>createBuilder()
                .name(Component.translatable("option.accuratefpsplus." + autoColorKey))
                .description(OptionDescription.of(Component.translatable("tooltip.accuratefpsplus.autoColor")))
                .binding(autoColorDefault, autoColorGet::get, autoColorSet::accept)
                .controller(TickBoxControllerBuilder::create)
                .build())
            .build();
    }

    private static int historyWindowToIndex(int seconds) {
        for (int i = 0; i < HISTORY_WINDOW_OPTIONS.length; ++i) {
            if (HISTORY_WINDOW_OPTIONS[i] < seconds) continue;
            return i;
        }
        return HISTORY_WINDOW_OPTIONS.length - 1;
    }

    private static Component calculationModeLabel(Integer value) {
        if (value == null) return Component.empty();
        return switch (value) {
            case 0 -> Component.literal("Averaged");
            case 2 -> Component.literal("EMA");
            default -> Component.literal("Real-Time");
        };
    }

    private static Component slotLabel(Integer value) {
        if (value == null) return Component.empty();
        return switch (value) {
            case 1 -> Component.translatable("value.accuratefpsplus.slot.first");
            case 2 -> Component.translatable("value.accuratefpsplus.slot.second");
            case 3 -> Component.translatable("value.accuratefpsplus.slot.third");
            case 4 -> Component.translatable("value.accuratefpsplus.slot.fourth");
            case 5 -> Component.translatable("value.accuratefpsplus.slot.fifth");
            case 6 -> Component.translatable("value.accuratefpsplus.slot.sixth");
            default -> Component.literal(String.valueOf(value));
        };
    }

    private static Component anchorCornerLabel(Integer value) {
        if (value == null) return Component.empty();
        return switch (value) {
            case 0 -> Component.translatable("value.accuratefpsplus.anchor.custom");
            case 1 -> Component.translatable("value.accuratefpsplus.anchor.topLeft");
            case 2 -> Component.translatable("value.accuratefpsplus.anchor.topRight");
            case 3 -> Component.translatable("value.accuratefpsplus.anchor.bottomLeft");
            case 4 -> Component.translatable("value.accuratefpsplus.anchor.bottomRight");
            default -> Component.literal(String.valueOf(value));
        };
    }

    private static Component presetLabel(Integer value) {
        if (value == null) return Component.empty();
        return switch (value) {
            case 0 -> Component.translatable("value.accuratefpsplus.preset.custom");
            case 1 -> Component.translatable("value.accuratefpsplus.preset.minimal");
            case 2 -> Component.translatable("value.accuratefpsplus.preset.competitive");
            case 3 -> Component.translatable("value.accuratefpsplus.preset.detailed");
            default -> Component.literal(String.valueOf(value));
        };
    }

    private static void applyPreset(int value) {
        switch (value) {
            case 1 -> {
                AccurateFpsPlusClient.showFps = true;
                AccurateFpsPlusClient.showFrametime = false;
                AccurateFpsPlusClient.showAvr = false;
                AccurateFpsPlusClient.showMax = false;
                AccurateFpsPlusClient.showMin = false;
                AccurateFpsPlusClient.showOnePercentLow = false;
                AccurateFpsPlusClient.showZeroOnePercentLow = false;
            }
            case 2 -> {
                AccurateFpsPlusClient.showFps = true;
                AccurateFpsPlusClient.showFrametime = false;
                AccurateFpsPlusClient.showAvr = true;
                AccurateFpsPlusClient.showMax = false;
                AccurateFpsPlusClient.showMin = false;
                AccurateFpsPlusClient.showOnePercentLow = true;
                AccurateFpsPlusClient.showZeroOnePercentLow = false;
            }
            case 3 -> {
                AccurateFpsPlusClient.showFps = true;
                AccurateFpsPlusClient.showFrametime = true;
                AccurateFpsPlusClient.showAvr = true;
                AccurateFpsPlusClient.showMax = true;
                AccurateFpsPlusClient.showMin = true;
                AccurateFpsPlusClient.showOnePercentLow = true;
                AccurateFpsPlusClient.showZeroOnePercentLow = true;
            }
            default -> {
                return;
            }
        }
        AccurateFpsPlusClient.markHudDirty();
    }
}
