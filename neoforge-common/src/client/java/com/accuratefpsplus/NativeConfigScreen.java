package com.accuratefpsplus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class NativeConfigScreen extends Screen {
    private static final int PAGE_GENERAL = 0;
    private static final int PAGE_METRICS = 1;
    private static final int PAGE_COLORING = 2;
    private static final int PAGE_ENGINE = 3;
    private static final int CONTENT_TOP = 82;
    private static final int ROW_HEIGHT = 25;
    private static final int[] HISTORY_WINDOWS = {1, 3, 5, 10, 30, 60};
    private static final String[] PAGE_NAMES = {
        "category.accuratefpsplus.general",
        "category.accuratefpsplus.metrics",
        "category.accuratefpsplus.coloring",
        "category.accuratefpsplus.engine"
    };
    private static final String[] METRIC_NAMES = {
        "category.accuratefpsplus.metricsMain",
        "category.accuratefpsplus.metricsFrametime",
        "category.accuratefpsplus.metricsAvg",
        "category.accuratefpsplus.metricsMax",
        "category.accuratefpsplus.metricsMin",
        "category.accuratefpsplus.metricsOnePercentLow",
        "category.accuratefpsplus.metricsZeroOnePercentLow"
    };

    private final Screen parent;
    private final List<Control> controls = new ArrayList<>();
    private final List<BooleanSupplier> validators = new ArrayList<>();
    private final List<Button> pageButtons = new ArrayList<>();
    private Button previousMetric;
    private Button metricTitle;
    private Button nextMetric;
    private int page;
    private int metric;
    private int scrollRows;
    private int pendingPreset;
    private Component validationMessage;

    private NativeConfigScreen(Screen parent) {
        super(Component.translatable("title.accuratefpsplus.config"));
        this.parent = parent;
        this.pendingPreset = AccurateFpsPlusClient.currentPreset;
    }

    public static Screen create(Screen parent) {
        return new NativeConfigScreen(parent);
    }

    @Override
    protected void init() {
        controls.clear();
        validators.clear();
        pageButtons.clear();

        int tabGap = 3;
        int tabWidth = Math.min(96, (this.width - 36 - tabGap * 3) / 4);
        int tabsWidth = tabWidth * 4 + tabGap * 3;
        int tabX = (this.width - tabsWidth) / 2;
        for (int i = 0; i < PAGE_NAMES.length; i++) {
            addPageButton(tabX + i * (tabWidth + tabGap), PAGE_NAMES[i], i, tabWidth);
        }

        this.previousMetric = Button.builder(Component.literal("<"), ignored -> changeMetric(-1))
            .bounds(this.width / 2 - 100, 53, 20, 20).build();
        this.metricTitle = Button.builder(Component.translatable(METRIC_NAMES[this.metric]), ignored -> { })
            .bounds(this.width / 2 - 76, 53, 152, 20).build();
        this.nextMetric = Button.builder(Component.literal(">"), ignored -> changeMetric(1))
            .bounds(this.width / 2 + 80, 53, 20, 20).build();
        addRenderableWidget(this.previousMetric);
        addRenderableWidget(this.metricTitle);
        addRenderableWidget(this.nextMetric);

        createGeneralControls();
        createMetricControls();
        createColoringControls();
        createEngineControls();
        addRenderableWidget(Button.builder(
            Component.translatable("gui.done"),
            ignored -> saveAndClose()
        ).bounds(this.width / 2 - 70, this.height - 28, 140, 20).build());
        updateControlVisibility();
    }

    private void addPageButton(int x, String labelKey, int targetPage, int width) {
        Button button = Button.builder(Component.translatable(labelKey), ignored -> {
            this.page = targetPage;
            this.scrollRows = 0;
            this.validationMessage = null;
            updateControlVisibility();
        }).bounds(x, 26, width, 20).build();
        this.pageButtons.add(button);
        addRenderableWidget(button);
    }

    private void changeMetric(int direction) {
        this.metric = (this.metric + direction + METRIC_NAMES.length) % METRIC_NAMES.length;
        this.metricTitle.setMessage(Component.translatable(METRIC_NAMES[this.metric]));
        this.scrollRows = 0;
        this.validationMessage = null;
        updateControlVisibility();
    }

    private void createGeneralControls() {
        int row = 0;
        addToggle(PAGE_GENERAL, -1, row++, "option.accuratefpsplus.toggleHUD",
            () -> AccurateFpsPlusClient.toggleHUD, value -> AccurateFpsPlusClient.toggleHUD = value);
        addToggle(PAGE_GENERAL, -1, row++, "option.accuratefpsplus.hideWithF3",
            () -> AccurateFpsPlusClient.hideWithF3, value -> AccurateFpsPlusClient.hideWithF3 = value);
        addCycle(PAGE_GENERAL, -1, row++, "option.accuratefpsplus.presetProfile",
            () -> this.pendingPreset, value -> {
                this.pendingPreset = value;
                if (value == 0) {
                    AccurateFpsPlusClient.currentPreset = 0;
                }
            }, 0, 3, NativeConfigScreen::presetLabel);
        addCycle(PAGE_GENERAL, -1, row++, "option.accuratefpsplus.anchorCorner",
            () -> AccurateFpsPlusClient.anchorCorner, value -> AccurateFpsPlusClient.anchorCorner = value,
            0, 4, NativeConfigScreen::anchorLabel);
        addInteger(PAGE_GENERAL, -1, row++, "option.accuratefpsplus.xPos",
            AccurateFpsPlusClient.xPos, 0, Integer.MAX_VALUE - 1, value -> AccurateFpsPlusClient.xPos = value);
        addInteger(PAGE_GENERAL, -1, row++, "option.accuratefpsplus.yPos",
            AccurateFpsPlusClient.yPos, 0, Integer.MAX_VALUE - 1, value -> AccurateFpsPlusClient.yPos = value);
        addToggle(PAGE_GENERAL, -1, row++, "option.accuratefpsplus.shadow",
            () -> AccurateFpsPlusClient.shadow, value -> AccurateFpsPlusClient.shadow = value);
        addToggle(PAGE_GENERAL, -1, row++, "option.accuratefpsplus.darkModeOverlay",
            () -> AccurateFpsPlusClient.darkModeOverlay, value -> AccurateFpsPlusClient.darkModeOverlay = value);
        addColor(PAGE_GENERAL, -1, row++, "option.accuratefpsplus.overlayColor",
            AccurateFpsPlusClient.overlayColor, value -> {
                AccurateFpsPlusClient.overlayColor = value;
                AccurateFpsPlusClient.updateCachedOverlayColor();
            });
        addInteger(PAGE_GENERAL, -1, row, "option.accuratefpsplus.overlayTransparency",
            AccurateFpsPlusClient.overlayTransparency, 0, 100, value -> {
                AccurateFpsPlusClient.overlayTransparency = value;
                AccurateFpsPlusClient.updateCachedOverlayColor();
            });
    }

    private void createMetricControls() {
        MetricSettings[] metrics = {
            new MetricSettings("showFps", () -> AccurateFpsPlusClient.showFps,
                value -> { AccurateFpsPlusClient.showFps = value; markCustomPreset(); },
                "fpsPosition", () -> AccurateFpsPlusClient.fpsPosition, value -> AccurateFpsPlusClient.fpsPosition = value, 4,
                "fpsPrecision", () -> AccurateFpsPlusClient.fpsPrecision, value -> AccurateFpsPlusClient.fpsPrecision = value,
                "beforeFps", () -> AccurateFpsPlusClient.beforeFps, value -> AccurateFpsPlusClient.beforeFps = value,
                "afterFps", () -> AccurateFpsPlusClient.afterFps, value -> AccurateFpsPlusClient.afterFps = value,
                "fpsColor", () -> AccurateFpsPlusClient.fpsColor, value -> AccurateFpsPlusClient.fpsColor = value,
                "fpsAutoColor", () -> AccurateFpsPlusClient.fpsAutoColor, value -> AccurateFpsPlusClient.fpsAutoColor = value,
                "mainUpdateInterval", () -> AccurateFpsPlusClient.mainUpdateInterval, value -> AccurateFpsPlusClient.mainUpdateInterval = value),
            new MetricSettings("showFrametime", () -> AccurateFpsPlusClient.showFrametime,
                value -> { AccurateFpsPlusClient.showFrametime = value; markCustomPreset(); },
                "frametimePosition", () -> AccurateFpsPlusClient.frametimePosition, value -> AccurateFpsPlusClient.frametimePosition = value, 3,
                "frametimePrecision", () -> AccurateFpsPlusClient.frametimePrecision, value -> AccurateFpsPlusClient.frametimePrecision = value,
                "beforeFrametime", () -> AccurateFpsPlusClient.beforeFrametime, value -> AccurateFpsPlusClient.beforeFrametime = value,
                "afterFrametime", () -> AccurateFpsPlusClient.afterFrametime, value -> AccurateFpsPlusClient.afterFrametime = value,
                "frametimeColor", () -> AccurateFpsPlusClient.frametimeColor, value -> AccurateFpsPlusClient.frametimeColor = value,
                "frametimeAutoColor", () -> AccurateFpsPlusClient.frametimeAutoColor, value -> AccurateFpsPlusClient.frametimeAutoColor = value,
                null, null, null),
            new MetricSettings("showAvr", () -> AccurateFpsPlusClient.showAvr,
                value -> { AccurateFpsPlusClient.showAvr = value; markCustomPreset(); },
                "avrPosition", () -> AccurateFpsPlusClient.avrPosition, value -> AccurateFpsPlusClient.avrPosition = value, 4,
                "avrPrecision", () -> AccurateFpsPlusClient.avrPrecision, value -> AccurateFpsPlusClient.avrPrecision = value,
                "beforeAvr", () -> AccurateFpsPlusClient.beforeAvr, value -> AccurateFpsPlusClient.beforeAvr = value,
                "afterAvr", () -> AccurateFpsPlusClient.afterAvr, value -> AccurateFpsPlusClient.afterAvr = value,
                "avrColor", () -> AccurateFpsPlusClient.avrColor, value -> AccurateFpsPlusClient.avrColor = value,
                "avrAutoColor", () -> AccurateFpsPlusClient.avrAutoColor, value -> AccurateFpsPlusClient.avrAutoColor = value,
                "avrUpdateInterval", () -> AccurateFpsPlusClient.avrUpdateInterval, value -> AccurateFpsPlusClient.avrUpdateInterval = value),
            new MetricSettings("showMax", () -> AccurateFpsPlusClient.showMax,
                value -> { AccurateFpsPlusClient.showMax = value; markCustomPreset(); },
                "maxPosition", () -> AccurateFpsPlusClient.maxPosition, value -> AccurateFpsPlusClient.maxPosition = value, 4,
                "maxPrecision", () -> AccurateFpsPlusClient.maxPrecision, value -> AccurateFpsPlusClient.maxPrecision = value,
                "beforeMax", () -> AccurateFpsPlusClient.beforeMax, value -> AccurateFpsPlusClient.beforeMax = value,
                "afterMax", () -> AccurateFpsPlusClient.afterMax, value -> AccurateFpsPlusClient.afterMax = value,
                "maxColor", () -> AccurateFpsPlusClient.maxColor, value -> AccurateFpsPlusClient.maxColor = value,
                "maxAutoColor", () -> AccurateFpsPlusClient.maxAutoColor, value -> AccurateFpsPlusClient.maxAutoColor = value,
                "maxUpdateInterval", () -> AccurateFpsPlusClient.maxUpdateInterval, value -> AccurateFpsPlusClient.maxUpdateInterval = value),
            new MetricSettings("showMin", () -> AccurateFpsPlusClient.showMin,
                value -> { AccurateFpsPlusClient.showMin = value; markCustomPreset(); },
                "minPosition", () -> AccurateFpsPlusClient.minPosition, value -> AccurateFpsPlusClient.minPosition = value, 4,
                "minPrecision", () -> AccurateFpsPlusClient.minPrecision, value -> AccurateFpsPlusClient.minPrecision = value,
                "beforeMin", () -> AccurateFpsPlusClient.beforeMin, value -> AccurateFpsPlusClient.beforeMin = value,
                "afterMin", () -> AccurateFpsPlusClient.afterMin, value -> AccurateFpsPlusClient.afterMin = value,
                "minColor", () -> AccurateFpsPlusClient.minColor, value -> AccurateFpsPlusClient.minColor = value,
                "minAutoColor", () -> AccurateFpsPlusClient.minAutoColor, value -> AccurateFpsPlusClient.minAutoColor = value,
                "minUpdateInterval", () -> AccurateFpsPlusClient.minUpdateInterval, value -> AccurateFpsPlusClient.minUpdateInterval = value),
            new MetricSettings("showOnePercentLow", () -> AccurateFpsPlusClient.showOnePercentLow,
                value -> { AccurateFpsPlusClient.showOnePercentLow = value; markCustomPreset(); },
                "onePercentLowPosition", () -> AccurateFpsPlusClient.onePercentLowPosition, value -> AccurateFpsPlusClient.onePercentLowPosition = value, 3,
                "onePercentLowPrecision", () -> AccurateFpsPlusClient.onePercentLowPrecision, value -> AccurateFpsPlusClient.onePercentLowPrecision = value,
                "beforeOnePercentLow", () -> AccurateFpsPlusClient.beforeOnePercentLow, value -> AccurateFpsPlusClient.beforeOnePercentLow = value,
                "afterOnePercentLow", () -> AccurateFpsPlusClient.afterOnePercentLow, value -> AccurateFpsPlusClient.afterOnePercentLow = value,
                "onePercentLowColor", () -> AccurateFpsPlusClient.onePercentLowColor, value -> AccurateFpsPlusClient.onePercentLowColor = value,
                "onePercentLowAutoColor", () -> AccurateFpsPlusClient.onePercentLowAutoColor, value -> AccurateFpsPlusClient.onePercentLowAutoColor = value,
                "onePercentLowUpdateInterval", () -> AccurateFpsPlusClient.onePercentLowUpdateInterval, value -> AccurateFpsPlusClient.onePercentLowUpdateInterval = value),
            new MetricSettings("showZeroOnePercentLow", () -> AccurateFpsPlusClient.showZeroOnePercentLow,
                value -> { AccurateFpsPlusClient.showZeroOnePercentLow = value; markCustomPreset(); },
                "zeroOnePercentLowPosition", () -> AccurateFpsPlusClient.zeroOnePercentLowPosition, value -> AccurateFpsPlusClient.zeroOnePercentLowPosition = value, 3,
                "zeroOnePercentLowPrecision", () -> AccurateFpsPlusClient.zeroOnePercentLowPrecision, value -> AccurateFpsPlusClient.zeroOnePercentLowPrecision = value,
                "beforeZeroOnePercentLow", () -> AccurateFpsPlusClient.beforeZeroOnePercentLow, value -> AccurateFpsPlusClient.beforeZeroOnePercentLow = value,
                "afterZeroOnePercentLow", () -> AccurateFpsPlusClient.afterZeroOnePercentLow, value -> AccurateFpsPlusClient.afterZeroOnePercentLow = value,
                "zeroOnePercentLowColor", () -> AccurateFpsPlusClient.zeroOnePercentLowColor, value -> AccurateFpsPlusClient.zeroOnePercentLowColor = value,
                "zeroOnePercentLowAutoColor", () -> AccurateFpsPlusClient.zeroOnePercentLowAutoColor, value -> AccurateFpsPlusClient.zeroOnePercentLowAutoColor = value,
                "zeroOnePercentLowUpdateInterval", () -> AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval, value -> AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval = value)
        };

        for (int i = 0; i < metrics.length; i++) {
            MetricSettings settings = metrics[i];
            int row = 0;
            addToggle(PAGE_METRICS, i, row++, "option.accuratefpsplus." + settings.visibleKey,
                settings.visible, settings.setVisible);
            addCycle(PAGE_METRICS, i, row++, "option.accuratefpsplus." + settings.positionKey,
                settings.position, settings.setPosition, 1, settings.maxPosition, NativeConfigScreen::slotLabel);
            addInteger(PAGE_METRICS, i, row++, "option.accuratefpsplus." + settings.precisionKey,
                settings.precision.getAsInt(), 0, 4, settings.setPrecision);
            addString(PAGE_METRICS, i, row++, "option.accuratefpsplus." + settings.prefixKey,
                settings.prefix.get(), settings.setPrefix);
            addString(PAGE_METRICS, i, row++, "option.accuratefpsplus." + settings.suffixKey,
                settings.suffix.get(), settings.setSuffix);
            addColor(PAGE_METRICS, i, row++, "option.accuratefpsplus." + settings.colorKey,
                settings.color.getAsInt(), settings.setColor);
            addToggle(PAGE_METRICS, i, row++, "option.accuratefpsplus." + settings.autoColorKey,
                settings.autoColor, settings.setAutoColor);
            if (settings.refreshKey != null) {
                addInteger(PAGE_METRICS, i, row, "option.accuratefpsplus." + settings.refreshKey,
                    settings.refresh.getAsInt(), 1, 3_600_000, settings.setRefresh);
            }
        }
    }

    private void createColoringControls() {
        int row = 0;
        addCycle(PAGE_COLORING, -1, row++, "option.accuratefpsplus.colorThresholdMode",
            () -> AccurateFpsPlusClient.colorThresholdMode,
            value -> AccurateFpsPlusClient.colorThresholdMode = value,
            0, 1, value -> Component.translatable(value == 1
                ? "value.accuratefpsplus.thresholdMode.manual"
                : "value.accuratefpsplus.thresholdMode.automatic"));
        addColor(PAGE_COLORING, -1, row++, "option.accuratefpsplus.gradientColorLow",
            AccurateFpsPlusClient.gradientColorLow, value -> AccurateFpsPlusClient.gradientColorLow = value);
        addColor(PAGE_COLORING, -1, row++, "option.accuratefpsplus.gradientColorMed",
            AccurateFpsPlusClient.gradientColorMed, value -> AccurateFpsPlusClient.gradientColorMed = value);
        addColor(PAGE_COLORING, -1, row++, "option.accuratefpsplus.gradientColorHigh",
            AccurateFpsPlusClient.gradientColorHigh, value -> AccurateFpsPlusClient.gradientColorHigh = value);
        addInteger(PAGE_COLORING, -1, row++, "option.accuratefpsplus.manualThresholdLow",
            AccurateFpsPlusClient.manualThresholdLow, 1, 1000, value -> AccurateFpsPlusClient.manualThresholdLow = value);
        addInteger(PAGE_COLORING, -1, row++, "option.accuratefpsplus.manualThresholdMed",
            AccurateFpsPlusClient.manualThresholdMed, 1, 1000, value -> AccurateFpsPlusClient.manualThresholdMed = value);
        addInteger(PAGE_COLORING, -1, row++, "option.accuratefpsplus.manualThresholdHigh",
            AccurateFpsPlusClient.manualThresholdHigh, 1, 1000, value -> AccurateFpsPlusClient.manualThresholdHigh = value);
        addButton(PAGE_COLORING, -1, row, "option.accuratefpsplus.detectDisplayHz", () -> {
            int refreshRate = AccurateFpsPlusClient.detectDisplayRefreshRate();
            AccurateFpsPlusClient.manualThresholdHigh = refreshRate;
            AccurateFpsPlusClient.manualThresholdMed = Math.max(1, (int) Math.round(refreshRate * 0.75));
            AccurateFpsPlusClient.manualThresholdLow = Math.max(1, (int) Math.round(refreshRate * 0.50));
            AccurateFpsPlusClient.colorThresholdMode = 1;
            this.validationMessage = null;
        });
    }

    private void createEngineControls() {
        int row = 0;
        addCycle(PAGE_ENGINE, -1, row++, "option.accuratefpsplus.calculationMode",
            () -> AccurateFpsPlusClient.calculationMode,
            value -> AccurateFpsPlusClient.calculationMode = value, 0, 2,
            value -> Component.translatable(switch (value) {
                case 0 -> "value.accuratefpsplus.calculationMode.averaged";
                case 2 -> "value.accuratefpsplus.calculationMode.ema";
                default -> "value.accuratefpsplus.calculationMode.realtime";
            }));
        addCycle(PAGE_ENGINE, -1, row++, "option.accuratefpsplus.historyWindow",
            () -> historyWindowIndex(AccurateFpsPlusClient.historyWindowSeconds),
            value -> AccurateFpsPlusClient.historyWindowSeconds = HISTORY_WINDOWS[value],
            0, HISTORY_WINDOWS.length - 1,
            value -> Component.translatable("value.accuratefpsplus.historyWindow", HISTORY_WINDOWS[value]));
        addInteger(PAGE_ENGINE, -1, row++, "option.accuratefpsplus.pollingRate",
            AccurateFpsPlusClient.pollingRate, 1, 5000, value -> AccurateFpsPlusClient.pollingRate = value);
        addInteger(PAGE_ENGINE, -1, row++, "option.accuratefpsplus.mainUpdateInterval",
            AccurateFpsPlusClient.mainUpdateInterval, 1, 3_600_000, value -> AccurateFpsPlusClient.mainUpdateInterval = value);
        addInteger(PAGE_ENGINE, -1, row++, "option.accuratefpsplus.avrUpdateInterval",
            AccurateFpsPlusClient.avrUpdateInterval, 1, 3_600_000, value -> AccurateFpsPlusClient.avrUpdateInterval = value);
        addInteger(PAGE_ENGINE, -1, row++, "option.accuratefpsplus.maxUpdateInterval",
            AccurateFpsPlusClient.maxUpdateInterval, 1, 3_600_000, value -> AccurateFpsPlusClient.maxUpdateInterval = value);
        addInteger(PAGE_ENGINE, -1, row++, "option.accuratefpsplus.minUpdateInterval",
            AccurateFpsPlusClient.minUpdateInterval, 1, 3_600_000, value -> AccurateFpsPlusClient.minUpdateInterval = value);
        addInteger(PAGE_ENGINE, -1, row++, "option.accuratefpsplus.onePercentLowUpdateInterval",
            AccurateFpsPlusClient.onePercentLowUpdateInterval, 1, 3_600_000, value -> AccurateFpsPlusClient.onePercentLowUpdateInterval = value);
        addInteger(PAGE_ENGINE, -1, row, "option.accuratefpsplus.zeroOnePercentLowUpdateInterval",
            AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval, 1, 3_600_000, value -> AccurateFpsPlusClient.zeroOnePercentLowUpdateInterval = value);
    }

    private void addToggle(int targetPage, int targetMetric, int row, String labelKey,
                           BooleanSupplier getter, Consumer<Boolean> setter) {
        boolean[] value = {getter.getAsBoolean()};
        Component label = Component.translatable(labelKey);
        Button button = Button.builder(toggleLabel(label, value[0]), widget -> {
            value[0] = !value[0];
            setter.accept(value[0]);
            widget.setMessage(toggleLabel(label, value[0]));
            this.validationMessage = null;
        }).bounds(controlX(), rowY(row), controlWidth(), 20).build();
        addControl(targetPage, targetMetric, row, label, button, () -> true);
    }

    private void addCycle(int targetPage, int targetMetric, int row, String labelKey,
                          IntSupplier getter, IntConsumer setter, int min, int max,
                          IntFunction<Component> formatter) {
        int[] value = {Math.max(min, Math.min(max, getter.getAsInt()))};
        Component label = Component.translatable(labelKey);
        Button button = Button.builder(cycleLabel(label, formatter.apply(value[0])), widget -> {
            value[0] = value[0] >= max ? min : value[0] + 1;
            setter.accept(value[0]);
            widget.setMessage(cycleLabel(label, formatter.apply(value[0])));
            this.validationMessage = null;
        }).bounds(controlX(), rowY(row), controlWidth(), 20).build();
        addControl(targetPage, targetMetric, row, label, button, () -> true);
    }

    private void addInteger(int targetPage, int targetMetric, int row, String labelKey,
                            int initialValue, int min, int max, IntConsumer setter) {
        Component label = Component.translatable(labelKey);
        EditBox field = new EditBox(this.font, controlX(), rowY(row), controlWidth(), 20, label);
        field.setMaxLength(10);
        field.setFilter(value -> digitsOnly(value));
        field.setValue(Integer.toString(initialValue));
        field.setResponder(value -> {
            Integer parsed = parseInteger(value, min, max);
            if (parsed != null) {
                setter.accept(parsed);
                this.validationMessage = null;
            }
        });
        addControl(targetPage, targetMetric, row, label, field,
            () -> parseInteger(field.getValue(), min, max) != null);
    }

    private void addColor(int targetPage, int targetMetric, int row, String labelKey,
                          int initialValue, IntConsumer setter) {
        Component label = Component.translatable(labelKey);
        EditBox field = new EditBox(this.font, controlX(), rowY(row), controlWidth(), 20, label);
        field.setMaxLength(6);
        field.setFilter(NativeConfigScreen::hexDigitsOnly);
        field.setValue(String.format("%06X", initialValue & 0xFFFFFF));
        field.setResponder(value -> {
            if (value.length() == 6) {
                setter.accept(Integer.parseInt(value, 16));
                this.validationMessage = null;
            }
        });
        addControl(targetPage, targetMetric, row, label, field,
            () -> field.getValue().length() == 6);
    }

    private void addString(int targetPage, int targetMetric, int row, String labelKey,
                           String initialValue, Consumer<String> setter) {
        Component label = Component.translatable(labelKey);
        EditBox field = new EditBox(this.font, controlX(), rowY(row), controlWidth(), 20, label);
        field.setMaxLength(128);
        field.setValue(initialValue == null ? "" : initialValue);
        field.setResponder(value -> setter.accept(value));
        addControl(targetPage, targetMetric, row, label, field, () -> true);
    }

    private void addButton(int targetPage, int targetMetric, int row, String labelKey, Runnable action) {
        Component label = Component.translatable(labelKey);
        Button button = Button.builder(label, ignored -> action.run())
            .bounds(controlX(), rowY(row), controlWidth(), 20)
            .build();
        addControl(targetPage, targetMetric, row, label, button, () -> true);
    }

    private void addControl(int targetPage, int targetMetric, int row, Component label,
                            AbstractWidget widget, BooleanSupplier validator) {
        Control control = new Control(targetPage, targetMetric, row, label, widget, validator);
        this.controls.add(control);
        this.validators.add(validator);
        addRenderableWidget(widget);
    }

    private void updateControlVisibility() {
        int maxScroll = maxScrollRows();
        this.scrollRows = Math.max(0, Math.min(this.scrollRows, maxScroll));
        for (int i = 0; i < this.controls.size(); i++) {
            Control control = this.controls.get(i);
            int y = rowY(control.row);
            control.widget.setY(y);
            boolean active = control.page == this.page
                && (control.page != PAGE_METRICS || control.metric == this.metric);
            control.widget.visible = active && y >= CONTENT_TOP && y + control.widget.getHeight() <= contentBottom();
        }
        this.previousMetric.visible = this.page == PAGE_METRICS;
        this.metricTitle.visible = this.page == PAGE_METRICS;
        this.nextMetric.visible = this.page == PAGE_METRICS;
    }

    private int maxScrollRows() {
        int lastRow = -1;
        for (Control control : this.controls) {
            if (control.page == this.page && (this.page != PAGE_METRICS || control.metric == this.metric)) {
                lastRow = Math.max(lastRow, control.row);
            }
        }
        int visibleRows = Math.max(1, (contentBottom() - CONTENT_TOP) / ROW_HEIGHT);
        return Math.max(0, lastRow + 1 - visibleRows);
    }

    private int contentBottom() {
        return this.height - 54;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseY >= CONTENT_TOP && mouseY < contentBottom() && maxScrollRows() > 0 && scrollY != 0) {
            int amount = Math.max(1, (int) Math.ceil(Math.abs(scrollY)));
            int direction = scrollY > 0 ? -1 : 1;
            this.scrollRows = Math.max(0, Math.min(maxScrollRows(), this.scrollRows + direction * amount));
            updateControlVisibility();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void saveAndClose() {
        if (!saveSettings()) {
            return;
        }
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void onClose() {
        if (saveSettings()) {
            this.minecraft.setScreen(this.parent);
        }
    }

    private boolean saveSettings() {
        for (BooleanSupplier validator : this.validators) {
            if (!validator.getAsBoolean()) {
                this.validationMessage = Component.translatable("error.accuratefpsplus.invalidValue");
                return false;
            }
        }
        if (this.pendingPreset > 0 && this.pendingPreset != AccurateFpsPlusClient.currentPreset) {
            AccurateFpsPlusClient.applyHudPreset(this.pendingPreset);
        }
        AccurateFpsPlusClient.telemetryEngine.setCalculationMode(AccurateFpsPlusClient.calculationMode);
        AccurateFpsPlusClient.telemetryEngine.setComputePercentiles(
            AccurateFpsPlusClient.showOnePercentLow || AccurateFpsPlusClient.showZeroOnePercentLow
        );
        AccurateFpsPlusClient.markHudDirty();
        ConfigManager.saveConfig();
        return true;
    }

    private void markCustomPreset() {
        AccurateFpsPlusClient.currentPreset = 0;
        this.pendingPreset = 0;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        updateControlVisibility();
        graphics.fill(0, 0, this.width, this.height, 0xC0101010);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        if (this.page != PAGE_METRICS) {
            graphics.drawCenteredString(this.font, Component.translatable(PAGE_NAMES[this.page]), this.width / 2, 59, 0xFFFFFF);
        }
        Button selectedPage = this.pageButtons.get(this.page);
        graphics.fill(selectedPage.getX(), selectedPage.getY() + selectedPage.getHeight() - 2,
            selectedPage.getX() + selectedPage.getWidth(), selectedPage.getY() + selectedPage.getHeight(),
            0xFFFFFFFF);
        for (Control control : this.controls) {
            if (control.widget.visible) {
                int y = control.widget.getY();
                graphics.fill(18, y + control.widget.getHeight() + 2, this.width - 18, y + control.widget.getHeight() + 3, 0x503F3F3F);
                graphics.enableScissor(24, y, control.widget.getX() - 8, y + control.widget.getHeight());
                graphics.drawString(this.font, control.label, 24, y + 6, 0xFFFFFF);
                graphics.disableScissor();
            }
        }
        int maxScroll = maxScrollRows();
        if (maxScroll > 0) {
            int trackHeight = contentBottom() - CONTENT_TOP;
            int visibleRows = Math.max(1, trackHeight / ROW_HEIGHT);
            int totalRows = visibleRows + maxScroll;
            int thumbHeight = Math.max(14, trackHeight * visibleRows / totalRows);
            int thumbY = CONTENT_TOP + (trackHeight - thumbHeight) * this.scrollRows / maxScroll;
            graphics.fill(this.width - 11, CONTENT_TOP, this.width - 8, contentBottom(), 0x60404040);
            graphics.fill(this.width - 11, thumbY, this.width - 8, thumbY + thumbHeight, 0xFFB0B0B0);
        }
        if (this.validationMessage != null) {
            graphics.drawCenteredString(this.font, this.validationMessage, this.width / 2, this.height - 47, 0xFF5555);
        }
    }

    private int controlX() {
        return this.width - 24 - controlWidth();
    }

    private int controlWidth() {
        return Math.max(96, Math.min(180, this.width / 2 - 28));
    }

    private int rowY(int row) {
        return CONTENT_TOP + (row - this.scrollRows) * ROW_HEIGHT;
    }

    private static Integer parseInteger(String value, int min, int max) {
        if (value.isEmpty()) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value);
            return parsed >= min && parsed <= max ? parsed : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static boolean digitsOnly(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean hexDigitsOnly(String value) {
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (!Character.isDigit(character)
                && (character < 'a' || character > 'f')
                && (character < 'A' || character > 'F')) {
                return false;
            }
        }
        return true;
    }

    private static int historyWindowIndex(int seconds) {
        for (int i = 0; i < HISTORY_WINDOWS.length; i++) {
            if (HISTORY_WINDOWS[i] >= seconds) {
                return i;
            }
        }
        return HISTORY_WINDOWS.length - 1;
    }

    private static Component toggleLabel(Component label, boolean enabled) {
        return Component.empty().append(label).append(": ").append(Component.translatable(
            enabled ? "value.accuratefpsplus.enabled" : "value.accuratefpsplus.disabled"
        ));
    }

    private static Component cycleLabel(Component label, Component value) {
        return Component.empty().append(label).append(": ").append(value);
    }

    private static Component presetLabel(int value) {
        return Component.translatable(switch (value) {
            case 1 -> "value.accuratefpsplus.preset.minimal";
            case 2 -> "value.accuratefpsplus.preset.competitive";
            case 3 -> "value.accuratefpsplus.preset.detailed";
            default -> "value.accuratefpsplus.preset.custom";
        });
    }

    private static Component anchorLabel(int value) {
        return Component.translatable(switch (value) {
            case 1 -> "value.accuratefpsplus.anchor.topLeft";
            case 2 -> "value.accuratefpsplus.anchor.topRight";
            case 3 -> "value.accuratefpsplus.anchor.bottomLeft";
            case 4 -> "value.accuratefpsplus.anchor.bottomRight";
            default -> "value.accuratefpsplus.anchor.custom";
        });
    }

    private static Component slotLabel(int value) {
        return Component.translatable(switch (value) {
            case 1 -> "value.accuratefpsplus.slot.first";
            case 2 -> "value.accuratefpsplus.slot.second";
            case 3 -> "value.accuratefpsplus.slot.third";
            case 4 -> "value.accuratefpsplus.slot.fourth";
            case 5 -> "value.accuratefpsplus.slot.fifth";
            default -> "value.accuratefpsplus.slot.sixth";
        });
    }

    private record Control(int page, int metric, int row, Component label,
                           AbstractWidget widget, BooleanSupplier validator) {
    }

    private record MetricSettings(
        String visibleKey, BooleanSupplier visible, Consumer<Boolean> setVisible,
        String positionKey, IntSupplier position, IntConsumer setPosition, int maxPosition,
        String precisionKey, IntSupplier precision, IntConsumer setPrecision,
        String prefixKey, SupplierValue<String> prefix, Consumer<String> setPrefix,
        String suffixKey, SupplierValue<String> suffix, Consumer<String> setSuffix,
        String colorKey, IntSupplier color, IntConsumer setColor,
        String autoColorKey, BooleanSupplier autoColor, Consumer<Boolean> setAutoColor,
        String refreshKey, IntSupplier refresh, IntConsumer setRefresh
    ) {
    }

    @FunctionalInterface
    private interface SupplierValue<T> {
        T get();
    }
}
