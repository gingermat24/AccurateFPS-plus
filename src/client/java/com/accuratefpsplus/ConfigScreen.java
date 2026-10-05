package com.accuratefpsplus;

import java.util.Optional;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
@SuppressWarnings("rawtypes")
public class ConfigScreen {
    private static final int[] HISTORY_WINDOW_OPTIONS = new int[]{1, 3, 5, 10, 30, 60};

    public static Screen getConfigScreen(Screen parent) {
        int initialPreset = AccurateFpsPlusClient.currentPreset;
        int[] selectedPreset = new int[]{initialPreset};
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle((Component)Component.translatable((String)"title.accuratefpsplus.config")).setSavingRunnable(() -> {
            if (selectedPreset[0] > 0 && selectedPreset[0] != initialPreset) {
                ConfigScreen.applyPreset(selectedPreset[0]);
                AccurateFpsPlusClient.currentPreset = selectedPreset[0];
            }
            ConfigManager.saveConfig();
            AccurateFpsPlusClient.telemetryEngine.setComputePercentiles(AccurateFpsPlusClient.showOnePercentLow || AccurateFpsPlusClient.showZeroOnePercentLow);
        });

        // General
        ConfigCategory general = builder.getOrCreateCategory((Component)Component.translatable((String)"category.accuratefpsplus.general"));
        general.addEntry((AbstractConfigListEntry)builder.entryBuilder().startBooleanToggle((Component)Component.translatable((String)"option.accuratefpsplus.toggleHUD"), AccurateFpsPlusClient.toggleHUD).setDefaultValue(true).setSaveConsumer(v -> {
            AccurateFpsPlusClient.toggleHUD = v;
        }).build());
        general.addEntry((AbstractConfigListEntry)builder.entryBuilder().startBooleanToggle((Component)Component.translatable((String)"option.accuratefpsplus.hideWithF3"), AccurateFpsPlusClient.hideWithF3).setDefaultValue(false).setTooltip(new Component[]{Component.translatable((String)"tooltip.accuratefpsplus.hideWithF3")}).setSaveConsumer(v -> {
            AccurateFpsPlusClient.hideWithF3 = v;
        }).build());
        general.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntSlider((Component)Component.translatable((String)"option.accuratefpsplus.presetProfile"), AccurateFpsPlusClient.currentPreset, 0, 3).setDefaultValue(1).setTextGetter(ConfigScreen::presetLabel).setSaveConsumer(v -> {
            AccurateFpsPlusClient.currentPreset = v;
            selectedPreset[0] = v;
        }).setTooltip(new Component[]{Component.translatable((String)"tooltip.accuratefpsplus.presetProfile")}).build());
        general.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntSlider((Component)Component.translatable((String)"option.accuratefpsplus.anchorCorner"), AccurateFpsPlusClient.anchorCorner, 0, 4).setDefaultValue(0).setTextGetter(ConfigScreen::anchorCornerLabel).setSaveConsumer(v -> {
            AccurateFpsPlusClient.anchorCorner = v;
        }).build());
        general.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.xPos"), AccurateFpsPlusClient.xPos).setDefaultValue(5).setMin(0).setMax(0x7FFFFFFE).setSaveConsumer(v -> {
            AccurateFpsPlusClient.xPos = v;
        }).build());
        general.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.yPos"), AccurateFpsPlusClient.yPos).setDefaultValue(5).setMin(0).setMax(0x7FFFFFFE).setSaveConsumer(v -> {
            AccurateFpsPlusClient.yPos = v;
        }).build());
        general.addEntry((AbstractConfigListEntry)builder.entryBuilder().startBooleanToggle((Component)Component.translatable((String)"option.accuratefpsplus.shadow"), AccurateFpsPlusClient.shadow).setDefaultValue(true).setSaveConsumer(v -> {
            AccurateFpsPlusClient.shadow = v;
        }).build());
        general.addEntry((AbstractConfigListEntry)builder.entryBuilder().startBooleanToggle((Component)Component.translatable((String)"option.accuratefpsplus.darkModeOverlay"), AccurateFpsPlusClient.darkModeOverlay).setDefaultValue(false).setSaveConsumer(v -> {
            AccurateFpsPlusClient.darkModeOverlay = v;
        }).build());
        general.addEntry((AbstractConfigListEntry)builder.entryBuilder().startTextField((Component)Component.translatable((String)"option.accuratefpsplus.overlayColor"), String.format("%06X", AccurateFpsPlusClient.overlayColor)).setDefaultValue("000000").setSaveConsumer(v -> {
            String sanitized = v.replace("#", "").trim();
            if (sanitized.isEmpty()) {
                return;
            }
            if (!sanitized.matches("[0-9A-Fa-f]{6}")) {
                return;
            }
            AccurateFpsPlusClient.overlayColor = Integer.parseInt(sanitized, 16);
            AccurateFpsPlusClient.updateCachedOverlayColor();
        }).setErrorSupplier(v -> v.matches("^#?[0-9A-Fa-f]{6}$") ? Optional.empty() : Optional.of(Component.translatable((String)"error.accuratefpsplus.invalidColor"))).build());
        general.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntSlider((Component)Component.translatable((String)"option.accuratefpsplus.overlayTransparency"), AccurateFpsPlusClient.overlayTransparency, 0, 100).setDefaultValue(50).setSaveConsumer(v -> {
            AccurateFpsPlusClient.overlayTransparency = v;
            AccurateFpsPlusClient.updateCachedOverlayColor();
        }).build());

        // Metrics (single consolidated category with expandable subcategories)
        ConfigCategory metrics = builder.getOrCreateCategory((Component)Component.translatable((String)"category.accuratefpsplus.metrics"));
        ConfigScreen.addMetricSubCategory(builder, metrics, "metricsMain", "showFps", AccurateFpsPlusClient.showFps, true, v -> {
            AccurateFpsPlusClient.showFps = v;
        }, "fpsPosition", AccurateFpsPlusClient.fpsPosition, 1, 4, v -> {
            AccurateFpsPlusClient.fpsPosition = v;
        }, "fpsPrecision", AccurateFpsPlusClient.fpsPrecision, 0, v -> {
            AccurateFpsPlusClient.fpsPrecision = v;
        }, "beforeFps", AccurateFpsPlusClient.beforeFps, "FPS: ", v -> {
            AccurateFpsPlusClient.beforeFps = v;
        }, "afterFps", AccurateFpsPlusClient.afterFps, "", v -> {
            AccurateFpsPlusClient.afterFps = v;
        }, "fpsColor", AccurateFpsPlusClient.fpsColor, v -> {
            AccurateFpsPlusClient.fpsColor = v;
        }, "fpsAutoColor", AccurateFpsPlusClient.fpsAutoColor, true, v -> {
            AccurateFpsPlusClient.fpsAutoColor = v;
        });
        ConfigScreen.addMetricSubCategory(builder, metrics, "metricsFrametime", "showFrametime", AccurateFpsPlusClient.showFrametime, false, v -> {
            AccurateFpsPlusClient.showFrametime = v;
        }, "frametimePosition", AccurateFpsPlusClient.frametimePosition, 3, 3, v -> {
            AccurateFpsPlusClient.frametimePosition = v;
        }, "frametimePrecision", AccurateFpsPlusClient.frametimePrecision, 1, v -> {
            AccurateFpsPlusClient.frametimePrecision = v;
        }, "beforeFrametime", AccurateFpsPlusClient.beforeFrametime, "FT: ", v -> {
            AccurateFpsPlusClient.beforeFrametime = v;
        }, "afterFrametime", AccurateFpsPlusClient.afterFrametime, "ms", v -> {
            AccurateFpsPlusClient.afterFrametime = v;
        }, "frametimeColor", AccurateFpsPlusClient.frametimeColor, v -> {
            AccurateFpsPlusClient.frametimeColor = v;
        }, "frametimeAutoColor", AccurateFpsPlusClient.frametimeAutoColor, false, v -> {
            AccurateFpsPlusClient.frametimeAutoColor = v;
        });
        ConfigScreen.addMetricSubCategory(builder, metrics, "metricsAvg", "showAvr", AccurateFpsPlusClient.showAvr, false, v -> {
            AccurateFpsPlusClient.showAvr = v;
        }, "avrPosition", AccurateFpsPlusClient.avrPosition, 2, 4, v -> {
            AccurateFpsPlusClient.avrPosition = v;
        }, "avrPrecision", AccurateFpsPlusClient.avrPrecision, 0, v -> {
            AccurateFpsPlusClient.avrPrecision = v;
        }, "beforeAvr", AccurateFpsPlusClient.beforeAvr, "AVG: ", v -> {
            AccurateFpsPlusClient.beforeAvr = v;
        }, "afterAvr", AccurateFpsPlusClient.afterAvr, "", v -> {
            AccurateFpsPlusClient.afterAvr = v;
        }, "avrColor", AccurateFpsPlusClient.avrColor, v -> {
            AccurateFpsPlusClient.avrColor = v;
        }, "avrAutoColor", AccurateFpsPlusClient.avrAutoColor, false, v -> {
            AccurateFpsPlusClient.avrAutoColor = v;
        });
        ConfigScreen.addMetricSubCategory(builder, metrics, "metricsMax", "showMax", AccurateFpsPlusClient.showMax, false, v -> {
            AccurateFpsPlusClient.showMax = v;
        }, "maxPosition", AccurateFpsPlusClient.maxPosition, 3, 4, v -> {
            AccurateFpsPlusClient.maxPosition = v;
        }, "maxPrecision", AccurateFpsPlusClient.maxPrecision, 0, v -> {
            AccurateFpsPlusClient.maxPrecision = v;
        }, "beforeMax", AccurateFpsPlusClient.beforeMax, "MAX: ", v -> {
            AccurateFpsPlusClient.beforeMax = v;
        }, "afterMax", AccurateFpsPlusClient.afterMax, "", v -> {
            AccurateFpsPlusClient.afterMax = v;
        }, "maxColor", AccurateFpsPlusClient.maxColor, v -> {
            AccurateFpsPlusClient.maxColor = v;
        }, "maxAutoColor", AccurateFpsPlusClient.maxAutoColor, false, v -> {
            AccurateFpsPlusClient.maxAutoColor = v;
        });
        ConfigScreen.addMetricSubCategory(builder, metrics, "metricsMin", "showMin", AccurateFpsPlusClient.showMin, false, v -> {
            AccurateFpsPlusClient.showMin = v;
        }, "minPosition", AccurateFpsPlusClient.minPosition, 4, 4, v -> {
            AccurateFpsPlusClient.minPosition = v;
        }, "minPrecision", AccurateFpsPlusClient.minPrecision, 0, v -> {
            AccurateFpsPlusClient.minPrecision = v;
        }, "beforeMin", AccurateFpsPlusClient.beforeMin, "MIN: ", v -> {
            AccurateFpsPlusClient.beforeMin = v;
        }, "afterMin", AccurateFpsPlusClient.afterMin, "", v -> {
            AccurateFpsPlusClient.afterMin = v;
        }, "minColor", AccurateFpsPlusClient.minColor, v -> {
            AccurateFpsPlusClient.minColor = v;
        }, "minAutoColor", AccurateFpsPlusClient.minAutoColor, false, v -> {
            AccurateFpsPlusClient.minAutoColor = v;
        });
        ConfigScreen.addMetricSubCategory(builder, metrics, "metricsOnePercentLow", "showOnePercentLow", AccurateFpsPlusClient.showOnePercentLow, false, v -> {
            AccurateFpsPlusClient.showOnePercentLow = v;
        }, "onePercentLowPosition", AccurateFpsPlusClient.onePercentLowPosition, 1, 3, v -> {
            AccurateFpsPlusClient.onePercentLowPosition = v;
        }, "onePercentLowPrecision", AccurateFpsPlusClient.onePercentLowPrecision, 0, v -> {
            AccurateFpsPlusClient.onePercentLowPrecision = v;
        }, "beforeOnePercentLow", AccurateFpsPlusClient.beforeOnePercentLow, "1% LOW: ", v -> {
            AccurateFpsPlusClient.beforeOnePercentLow = v;
        }, "afterOnePercentLow", AccurateFpsPlusClient.afterOnePercentLow, "", v -> {
            AccurateFpsPlusClient.afterOnePercentLow = v;
        }, "onePercentLowColor", AccurateFpsPlusClient.onePercentLowColor, v -> {
            AccurateFpsPlusClient.onePercentLowColor = v;
        }, "onePercentLowAutoColor", AccurateFpsPlusClient.onePercentLowAutoColor, false, v -> {
            AccurateFpsPlusClient.onePercentLowAutoColor = v;
        });
        ConfigScreen.addMetricSubCategory(builder, metrics, "metricsZeroOnePercentLow", "showZeroOnePercentLow", AccurateFpsPlusClient.showZeroOnePercentLow, false, v -> {
            AccurateFpsPlusClient.showZeroOnePercentLow = v;
        }, "zeroOnePercentLowPosition", AccurateFpsPlusClient.zeroOnePercentLowPosition, 2, 3, v -> {
            AccurateFpsPlusClient.zeroOnePercentLowPosition = v;
        }, "zeroOnePercentLowPrecision", AccurateFpsPlusClient.zeroOnePercentLowPrecision, 0, v -> {
            AccurateFpsPlusClient.zeroOnePercentLowPrecision = v;
        }, "beforeZeroOnePercentLow", AccurateFpsPlusClient.beforeZeroOnePercentLow, "0.1% LOW: ", v -> {
            AccurateFpsPlusClient.beforeZeroOnePercentLow = v;
        }, "afterZeroOnePercentLow", AccurateFpsPlusClient.afterZeroOnePercentLow, "", v -> {
            AccurateFpsPlusClient.afterZeroOnePercentLow = v;
        }, "zeroOnePercentLowColor", AccurateFpsPlusClient.zeroOnePercentLowColor, v -> {
            AccurateFpsPlusClient.zeroOnePercentLowColor = v;
        }, "zeroOnePercentLowAutoColor", AccurateFpsPlusClient.zeroOnePercentLowAutoColor, false, v -> {
            AccurateFpsPlusClient.zeroOnePercentLowAutoColor = v;
        });

        // Coloring
        ConfigCategory coloring = builder.getOrCreateCategory((Component)Component.translatable((String)"category.accuratefpsplus.coloring"));
        coloring.addEntry((AbstractConfigListEntry)builder.entryBuilder().startEnumSelector(
            (Component)Component.translatable((String)"option.accuratefpsplus.colorThresholdMode"),
            ThresholdMode.class,
            AccurateFpsPlusClient.colorThresholdMode == 1 ? ThresholdMode.MANUAL : ThresholdMode.AUTOMATIC
        ).setDefaultValue(ThresholdMode.AUTOMATIC).setEnumNameProvider(mode -> switch ((ThresholdMode)mode) {
            case MANUAL -> Component.translatable((String)"value.accuratefpsplus.thresholdMode.manual");
            default -> Component.translatable((String)"value.accuratefpsplus.thresholdMode.automatic");
        }).setSaveConsumer(mode -> {
            AccurateFpsPlusClient.colorThresholdMode = (mode == ThresholdMode.MANUAL ? 1 : 0);
        }).setTooltip(new Component[]{Component.translatable((String)"tooltip.accuratefpsplus.colorThresholdMode")}).build());

        coloring.addEntry((AbstractConfigListEntry)builder.entryBuilder().startColorField((Component)Component.translatable((String)"option.accuratefpsplus.gradientColorLow"), AccurateFpsPlusClient.gradientColorLow).setDefaultValue(0xFF5555).setSaveConsumer(v -> {
            AccurateFpsPlusClient.gradientColorLow = v & 0xFFFFFF;
        }).build());
        coloring.addEntry((AbstractConfigListEntry)builder.entryBuilder().startColorField((Component)Component.translatable((String)"option.accuratefpsplus.gradientColorMed"), AccurateFpsPlusClient.gradientColorMed).setDefaultValue(0xFFFF55).setSaveConsumer(v -> {
            AccurateFpsPlusClient.gradientColorMed = v & 0xFFFFFF;
        }).build());
        coloring.addEntry((AbstractConfigListEntry)builder.entryBuilder().startColorField((Component)Component.translatable((String)"option.accuratefpsplus.gradientColorHigh"), AccurateFpsPlusClient.gradientColorHigh).setDefaultValue(0x55FF55).setSaveConsumer(v -> {
            AccurateFpsPlusClient.gradientColorHigh = v & 0xFFFFFF;
        }).build());
        coloring.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.manualThresholdLow"), AccurateFpsPlusClient.manualThresholdLow).setDefaultValue(30).setMin(1).setMax(1000).setSaveConsumer(v -> {
            AccurateFpsPlusClient.manualThresholdLow = v;
        }).build());
        coloring.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.manualThresholdMed"), AccurateFpsPlusClient.manualThresholdMed).setDefaultValue(60).setMin(1).setMax(1000).setSaveConsumer(v -> {
            AccurateFpsPlusClient.manualThresholdMed = v;
        }).build());
        coloring.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.manualThresholdHigh"), AccurateFpsPlusClient.manualThresholdHigh).setDefaultValue(120).setMin(1).setMax(1000).setSaveConsumer(v -> {
            AccurateFpsPlusClient.manualThresholdHigh = v;
        }).build());
        coloring.addEntry((AbstractConfigListEntry)builder.entryBuilder().startBooleanToggle((Component)Component.translatable((String)"option.accuratefpsplus.detectDisplayHz"), false).setDefaultValue(false).setTooltip(new Component[]{Component.translatable((String)"tooltip.accuratefpsplus.detectDisplayHz")}).setSaveConsumer(v -> {
            if (v) {
                int hz = AccurateFpsPlusClient.detectDisplayRefreshRate();
                AccurateFpsPlusClient.manualThresholdHigh = hz;
                AccurateFpsPlusClient.manualThresholdMed = Math.max(1, (int) Math.round(hz * 0.75));
                AccurateFpsPlusClient.manualThresholdLow = Math.max(1, (int) Math.round(hz * 0.50));
                AccurateFpsPlusClient.colorThresholdMode = 1;
            }
        }).build());

        // Engine
        ConfigCategory engine = builder.getOrCreateCategory((Component)Component.translatable((String)"category.accuratefpsplus.engine"));
        engine.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntSlider((Component)Component.translatable((String)"option.accuratefpsplus.calculationMode"), AccurateFpsPlusClient.calculationMode, 0, 2).setDefaultValue(1).setTextGetter(ConfigScreen::calculationModeLabel).setSaveConsumer(v -> {
            AccurateFpsPlusClient.calculationMode = v;
            AccurateFpsPlusClient.telemetryEngine.setCalculationMode((int)v);
        }).setTooltip(new Component[]{Component.translatable((String)"tooltip.accuratefpsplus.calculationMode")}).build());
        engine.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntSlider((Component)Component.translatable((String)"option.accuratefpsplus.historyWindow"), ConfigScreen.historyWindowToIndex(AccurateFpsPlusClient.historyWindowSeconds), 0, HISTORY_WINDOW_OPTIONS.length - 1).setDefaultValue(ConfigScreen.historyWindowToIndex(5)).setTextGetter(i -> Component.literal((String)(HISTORY_WINDOW_OPTIONS[i] + "s"))).setSaveConsumer(i -> {
            AccurateFpsPlusClient.historyWindowSeconds = HISTORY_WINDOW_OPTIONS[i];
        }).setTooltip(new Component[]{Component.translatable((String)"tooltip.accuratefpsplus.historyWindow")}).build());
        engine.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.pollingRate"), AccurateFpsPlusClient.pollingRate).setDefaultValue(100).setMin(1).setMax(5000).setSaveConsumer(v -> {
            AccurateFpsPlusClient.pollingRate = v;
        }).build());
        engine.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.mainUpdateInterval"), AccurateFpsPlusClient.mainUpdateInterval).setDefaultValue(250).setMin(1).setMax(3600000).setSaveConsumer(v -> {
            AccurateFpsPlusClient.mainUpdateInterval = v;
        }).build());
        engine.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.avrUpdateInterval"), AccurateFpsPlusClient.avrUpdateInterval).setDefaultValue(2000).setMin(1).setMax(3600000).setSaveConsumer(v -> {
            AccurateFpsPlusClient.avrUpdateInterval = v;
        }).build());
        engine.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.maxUpdateInterval"), AccurateFpsPlusClient.maxUpdateInterval).setDefaultValue(2000).setMin(1).setMax(3600000).setSaveConsumer(v -> {
            AccurateFpsPlusClient.maxUpdateInterval = v;
        }).build());
        engine.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.minUpdateInterval"), AccurateFpsPlusClient.minUpdateInterval).setDefaultValue(2000).setMin(1).setMax(3600000).setSaveConsumer(v -> {
            AccurateFpsPlusClient.minUpdateInterval = v;
        }).build());
        engine.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.onePercentLowUpdateInterval"), AccurateFpsPlusClient.onePercentLowUpdateInterval).setDefaultValue(2000).setMin(1).setMax(3600000).setSaveConsumer(v -> {
            AccurateFpsPlusClient.onePercentLowUpdateInterval = v;
        }).build());
        engine.addEntry((AbstractConfigListEntry)builder.entryBuilder().startIntField((Component)Component.translatable((String)"option.accuratefpsplus.zeroOnePercentLowUpdateInterval"), AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval).setDefaultValue(2000).setMin(1).setMax(3600000).setSaveConsumer(v -> {
            AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval = v;
        }).build());
        return builder.build();
    }

    private static void addMetricSubCategory(
            ConfigBuilder builder,
            ConfigCategory parentCategory,
            String subCategoryKey,
            String showKey,
            boolean showVal,
            boolean showDefault,
            BoolSetter showSet,
            String posKey,
            int posVal,
            int posDefault,
            int posMax,
            IntSetter posSet,
            String precKey,
            int precVal,
            int precDefault,
            IntSetter precSet,
            String beforeKey,
            String beforeVal,
            String beforeDefault,
            StrSetter beforeSet,
            String afterKey,
            String afterVal,
            String afterDefault,
            StrSetter afterSet,
            String colorKey,
            int colorVal,
            IntSetter colorSet,
            String autoColorKey,
            boolean autoColorVal,
            boolean autoColorDefault,
            BoolSetter autoColorSet
    ) {
        SubCategoryBuilder sub = builder.entryBuilder().startSubCategory(
            (Component)Component.translatable((String)("category.accuratefpsplus." + subCategoryKey))
        );
        sub.setExpanded(true);
        sub.add((AbstractConfigListEntry)builder.entryBuilder().startBooleanToggle(
            (Component)Component.translatable((String)("option.accuratefpsplus." + showKey)), showVal
        ).setDefaultValue(showDefault).setSaveConsumer(v -> {
            showSet.accept((boolean)v);
            AccurateFpsPlusClient.currentPreset = 0;
        }).build());
        sub.add((AbstractConfigListEntry)builder.entryBuilder().startIntSlider(
            (Component)Component.translatable((String)("option.accuratefpsplus." + posKey)), posVal, 1, posMax
        ).setDefaultValue(posDefault).setTextGetter(ConfigScreen::slotLabel).setSaveConsumer(v -> posSet.accept((int)v)).build());
        sub.add((AbstractConfigListEntry)builder.entryBuilder().startIntSlider(
            (Component)Component.translatable((String)("option.accuratefpsplus." + precKey)), precVal, 0, 4
        ).setDefaultValue(precDefault).setSaveConsumer(v -> precSet.accept((int)v)).build());
        sub.add((AbstractConfigListEntry)builder.entryBuilder().startTextField(
            (Component)Component.translatable((String)("option.accuratefpsplus." + beforeKey)), beforeVal != null ? beforeVal : beforeDefault
        ).setDefaultValue(beforeDefault).setSaveConsumer(v -> beforeSet.accept((String)v)).build());
        sub.add((AbstractConfigListEntry)builder.entryBuilder().startTextField(
            (Component)Component.translatable((String)("option.accuratefpsplus." + afterKey)), afterVal != null ? afterVal : afterDefault
        ).setDefaultValue(afterDefault).setSaveConsumer(v -> afterSet.accept((String)v)).build());
        sub.add((AbstractConfigListEntry)builder.entryBuilder().startColorField(
            (Component)Component.translatable((String)("option.accuratefpsplus." + colorKey)), colorVal
        ).setDefaultValue(0xFFFFFF).setSaveConsumer(v -> colorSet.accept(v & 0xFFFFFF)).build());
        sub.add((AbstractConfigListEntry)builder.entryBuilder().startBooleanToggle(
            (Component)Component.translatable((String)("option.accuratefpsplus." + autoColorKey)), autoColorVal
        ).setDefaultValue(autoColorDefault).setSaveConsumer(v -> autoColorSet.accept((boolean)v)).setTooltip(new Component[]{Component.translatable((String)"tooltip.accuratefpsplus.autoColor")}).build());
        parentCategory.addEntry((AbstractConfigListEntry)sub.build());
    }

    private static int historyWindowToIndex(int seconds) {
        for (int i = 0; i < HISTORY_WINDOW_OPTIONS.length; ++i) {
            if (HISTORY_WINDOW_OPTIONS[i] < seconds) continue;
            return i;
        }
        return HISTORY_WINDOW_OPTIONS.length - 1;
    }

    private static Component calculationModeLabel(int value) {
        return switch (value) {
            case 0 -> Component.literal((String)"Averaged");
            case 2 -> Component.literal((String)"EMA");
            default -> Component.literal((String)"Real-Time");
        };
    }

    private static Component slotLabel(int value) {
        return switch (value) {
            case 1 -> Component.translatable((String)"value.accuratefpsplus.slot.first");
            case 2 -> Component.translatable((String)"value.accuratefpsplus.slot.second");
            case 3 -> Component.translatable((String)"value.accuratefpsplus.slot.third");
            case 4 -> Component.translatable((String)"value.accuratefpsplus.slot.fourth");
            case 5 -> Component.translatable((String)"value.accuratefpsplus.slot.fifth");
            case 6 -> Component.translatable((String)"value.accuratefpsplus.slot.sixth");
            default -> Component.literal((String)String.valueOf(value));
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

    protected static void applyPreset(int value) {
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

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    private static interface BoolSetter {
        public void accept(boolean var1);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    private static interface IntSetter {
        public void accept(int var1);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    private static interface StrSetter {
        public void accept(String var1);
    }
}
