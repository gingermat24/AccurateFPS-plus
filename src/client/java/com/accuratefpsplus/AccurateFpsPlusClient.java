package com.accuratefpsplus;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

@Environment(EnvType.CLIENT)
public class AccurateFpsPlusClient
implements ClientModInitializer {
    static final FpsTelemetryEngine telemetryEngine = new FpsTelemetryEngine();

    public static FpsTelemetryEngine getTelemetryEngine() {
        return telemetryEngine;
    }
    private static final String METRIC_SEPARATOR = " | ";
    private static final long[] POWERS_OF_10 = new long[]{1L, 10L, 100L, 1000L, 10000L};
    private static HudSnapshot cachedHud = new HudSnapshot(Component.empty(), FormattedCharSequence.EMPTY, "", Component.empty(), FormattedCharSequence.EMPTY, "", 0, 0, 0);
    private static long lastTelemetryRevision = -1L;
    private static long configRevision;
    private static long lastConfigRevision;
    private static int cachedRenderX;
    private static int cachedRenderY;
    private static boolean positionDirty;
    private static int cachedScreenWidth;
    private static int cachedScreenHeight;
    private static KeyMapping toggleHudKey;
    protected static boolean toggleHUD;
    protected static boolean hideWithF3;
    protected static int xPos;
    protected static int yPos;
    protected static boolean shadow;
    protected static int pollingRate;
    protected static int calculationMode;
    protected static int historyWindowSeconds;
    protected static int anchorCorner;
    protected static boolean darkModeOverlay;
    protected static int overlayColor;
    protected static int overlayTransparency;
    private static int cachedOverlayColorWithAlpha;
    private static final java.lang.reflect.Method POSE_GETTER;
    private static final java.lang.reflect.Method POSE_PUSH;
    private static final java.lang.reflect.Method POSE_POP;
    private static final java.lang.reflect.Method POSE_SCALE;
    private static final boolean POSE_SCALE_IS_3D;
    protected static int currentPreset;
    protected static boolean showFps;
    protected static int fpsPosition;
    protected static int fpsPrecision;
    protected static String beforeFps;
    protected static String afterFps;
    protected static int mainUpdateInterval;
    protected static int fpsColor;
    protected static boolean fpsAutoColor;
    protected static boolean showFrametime;
    protected static int frametimePosition;
    protected static int frametimePrecision;
    protected static String beforeFrametime;
    protected static String afterFrametime;
    protected static int frametimeColor;
    protected static boolean frametimeAutoColor;
    protected static boolean showAvr;
    protected static int avrPosition;
    protected static int avrPrecision;
    protected static String beforeAvr;
    protected static String afterAvr;
    protected static int avrUpdateInterval;
    protected static int avrColor;
    protected static boolean avrAutoColor;
    protected static boolean showMax;
    protected static int maxPosition;
    protected static int maxPrecision;
    protected static String beforeMax;
    protected static String afterMax;
    protected static int maxUpdateInterval;
    protected static int maxColor;
    protected static boolean maxAutoColor;
    protected static boolean showMin;
    protected static int minPosition;
    protected static int minPrecision;
    protected static String beforeMin;
    protected static String afterMin;
    protected static int minUpdateInterval;
    protected static int minColor;
    protected static boolean minAutoColor;
    protected static boolean showOnePercentLow;
    protected static int onePercentLowPosition;
    protected static int onePercentLowPrecision;
    protected static String beforeOnePercentLow;
    protected static String afterOnePercentLow;
    protected static int onePercentLowUpdateInterval;
    protected static int onePercentLowColor;
    protected static boolean onePercentLowAutoColor;
    protected static boolean showZeroOnePercentLow;
    protected static int zeroOnePercentLowPosition;
    protected static int zeroOnePercentLowPrecision;
    protected static String beforeZeroOnePercentLow;
    protected static String afterZeroOnePercentLow;
    protected static int zeroOnePercentLowUpdateInterval;
    protected static int zeroOnePercentLowColor;
    protected static boolean zeroOnePercentLowAutoColor;
    protected static int colorThresholdMode;
    protected static int gradientColorLow;
    protected static int gradientColorMed;
    protected static int gradientColorHigh;
    protected static int manualThresholdLow;
    protected static int manualThresholdMed;
    protected static int manualThresholdHigh;

    @Override
    @SuppressWarnings("deprecation")
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        telemetryEngine.setCalculationMode(calculationMode);
        telemetryEngine.setComputePercentiles(showOnePercentLow || showZeroOnePercentLow);
        AccurateFpsPlusClient.updateCachedOverlayColor();

        toggleHudKey = createToggleKeyMapping();
        if (toggleHudKey != null) {
            KeyBindingHelper.registerKeyBinding(toggleHudKey);
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey != null && toggleHudKey.consumeClick()) {
                toggleHUD = !toggleHUD;
                ConfigManager.saveConfig();
            }
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> {
            Minecraft mc = Minecraft.getInstance();
            if (!toggleHUD || mc.screen != null || (hideWithF3 && mc.getDebugOverlay() != null && mc.getDebugOverlay().showDebugScreen())) {
                return;
            }
            telemetryEngine.onFrame(pollingRate, mainUpdateInterval, avrUpdateInterval, maxUpdateInterval, minUpdateInterval, onePercentLowUpdateInterval, zeroOnePercentLowUpdateInterval, (long)historyWindowSeconds * 1000L);
            if (!telemetryEngine.hasData()) {
                return;
            }
            HudSnapshot hud = AccurateFpsPlusClient.getOrBuildHudSnapshot(mc);
            boolean hasMainMetrics = !hud.mainRawText.isEmpty();
            boolean hasLowMetrics = !hud.lowRawText.isEmpty();
            if (!hasMainMetrics && !hasLowMetrics) {
                return;
            }

            float lowScale = 0.75f;
            int mainLineHeight = hasMainMetrics ? hud.fontHeight : 0;
            int lowLineHeight = hasLowMetrics ? (int) Math.ceil(hud.fontHeight * lowScale) : 0;
            int lineSpacing = (hasMainMetrics && hasLowMetrics) ? 2 : 0;
            int totalHeight = mainLineHeight + lineSpacing + lowLineHeight;
            int lowVisualWidth = hasLowMetrics ? (int) Math.ceil(hud.lowTextWidth * lowScale) : 0;
            int maxWidth = Math.max(hasMainMetrics ? hud.mainTextWidth : 0, lowVisualWidth);

            if (positionDirty || anchorCorner != 0) {
                int screenW = mc.getWindow().getGuiScaledWidth();
                int screenH = mc.getWindow().getGuiScaledHeight();
                if (screenW != cachedScreenWidth || screenH != cachedScreenHeight) {
                    cachedScreenWidth = screenW;
                    cachedScreenHeight = screenH;
                }
                AccurateFpsPlusClient.computeRenderPos(maxWidth, totalHeight, cachedScreenWidth, cachedScreenHeight);
                positionDirty = false;
            }

            int mainY = cachedRenderY;
            int lowY = hasMainMetrics ? (cachedRenderY + mainLineHeight + lineSpacing) : cachedRenderY;

            if (darkModeOverlay) {
                context.fill(cachedRenderX - 3, cachedRenderY - 3, cachedRenderX + maxWidth + 3, cachedRenderY + totalHeight + 3, cachedOverlayColorWithAlpha);
            }
            if (hasMainMetrics) {
                context.drawString(mc.font, hud.mainOrderedText, cachedRenderX, mainY, -1, shadow);
            }
            if (hasLowMetrics) {
                drawScaledText(context, mc.font, hud.lowOrderedText, cachedRenderX, lowY, lowScale, -1, shadow);
            }
        });
    }


    private static HudSnapshot getOrBuildHudSnapshot(Minecraft mc) {
        long telemetryRevision = telemetryEngine.getDisplayRevision();
        if (telemetryRevision == lastTelemetryRevision && configRevision == lastConfigRevision) {
            return cachedHud;
        }
        String[] mainSlots = new String[4];
        int[] mainColors = new int[4];
        String[] lowSlots = new String[3];
        int[] lowColors = new int[3];
        double fpsVal = telemetryEngine.getDisplayedMainFps();
        double frametimeVal = telemetryEngine.getDisplayedFrametimeMs();
        double avrVal = telemetryEngine.getDisplayedAverageFps();
        double maxVal = telemetryEngine.getDisplayedMaxFps();
        double minVal = telemetryEngine.getDisplayedMinFps();
        double onePercentLowVal = telemetryEngine.getDisplayedOnePercentLow();
        double zeroOnePercentLowVal = telemetryEngine.getDisplayedZeroOnePercentLow();
        AccurateFpsPlusClient.placeInOrder(mainSlots, mainColors, fpsPosition, showFps, AccurateFpsPlusClient.formatPart(beforeFps, fpsPrecision, fpsVal, afterFps), AccurateFpsPlusClient.resolveColor(fpsAutoColor, fpsColor, fpsVal));
        AccurateFpsPlusClient.placeInOrder(mainSlots, mainColors, avrPosition, showAvr, AccurateFpsPlusClient.formatPart(beforeAvr, avrPrecision, avrVal, afterAvr), AccurateFpsPlusClient.resolveColor(avrAutoColor, avrColor, avrVal));
        AccurateFpsPlusClient.placeInOrder(mainSlots, mainColors, maxPosition, showMax, AccurateFpsPlusClient.formatPart(beforeMax, maxPrecision, maxVal, afterMax), AccurateFpsPlusClient.resolveColor(maxAutoColor, maxColor, maxVal));
        AccurateFpsPlusClient.placeInOrder(mainSlots, mainColors, minPosition, showMin, AccurateFpsPlusClient.formatPart(beforeMin, minPrecision, minVal, afterMin), AccurateFpsPlusClient.resolveColor(minAutoColor, minColor, minVal));
        AccurateFpsPlusClient.placeInOrder(lowSlots, lowColors, onePercentLowPosition, showOnePercentLow, AccurateFpsPlusClient.formatPart(beforeOnePercentLow, onePercentLowPrecision, onePercentLowVal, afterOnePercentLow), AccurateFpsPlusClient.resolveColor(onePercentLowAutoColor, onePercentLowColor, onePercentLowVal));
        AccurateFpsPlusClient.placeInOrder(lowSlots, lowColors, zeroOnePercentLowPosition, showZeroOnePercentLow, AccurateFpsPlusClient.formatPart(beforeZeroOnePercentLow, zeroOnePercentLowPrecision, zeroOnePercentLowVal, afterZeroOnePercentLow), AccurateFpsPlusClient.resolveColor(zeroOnePercentLowAutoColor, zeroOnePercentLowColor, zeroOnePercentLowVal));
        AccurateFpsPlusClient.placeInOrder(lowSlots, lowColors, frametimePosition, showFrametime, AccurateFpsPlusClient.formatPart(beforeFrametime, frametimePrecision, frametimeVal, afterFrametime), AccurateFpsPlusClient.resolveColor(frametimeAutoColor, frametimeColor, fpsVal));
        MutableComponent mainText = Component.empty();
        StringBuilder mainPlain = new StringBuilder();
        boolean firstMetric = true;
        for (int i = 0; i < 4; ++i) {
            if (mainSlots[i] == null) continue;
            if (!firstMetric) {
                mainPlain.append(METRIC_SEPARATOR);
                mainText.append(Component.literal(METRIC_SEPARATOR).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(mainColors[i]))));
            }
            mainPlain.append(mainSlots[i]);
            mainText.append(Component.literal(mainSlots[i]).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(mainColors[i]))));
            firstMetric = false;
        }
        MutableComponent lowText = Component.empty();
        StringBuilder lowPlain = new StringBuilder();
        boolean firstLow = true;
        for (int i = 0; i < 3; ++i) {
            if (lowSlots[i] == null) continue;
            if (!firstLow) {
                lowPlain.append(METRIC_SEPARATOR);
                lowText.append(Component.literal(METRIC_SEPARATOR).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(lowColors[i]))));
            }
            lowPlain.append(lowSlots[i]);
            lowText.append(Component.literal(lowSlots[i]).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(lowColors[i]))));
            firstLow = false;
        }
        String mainRaw = mainPlain.toString();
        String lowRaw = lowPlain.toString();
        FormattedCharSequence mainOrdered = mainText.getVisualOrderText();
        FormattedCharSequence lowOrdered = lowText.getVisualOrderText();
        int mainWidth = mc.font.width(mainRaw);
        int lowWidth = mc.font.width(lowRaw);
        int fontHeight = 9;
        cachedHud = new HudSnapshot(mainText, mainOrdered, mainRaw, lowText, lowOrdered, lowRaw, mainWidth, lowWidth, fontHeight);
        lastTelemetryRevision = telemetryRevision;
        lastConfigRevision = configRevision;
        positionDirty = true;
        return cachedHud;
    }

    private static void computeRenderPos(int textW, int totalH, int screenW, int screenH) {
        if (anchorCorner == 0) {
            cachedRenderX = Math.max(0, Math.min(xPos, Math.max(0, screenW - textW)));
            cachedRenderY = Math.max(0, Math.min(yPos, Math.max(0, screenH - totalH)));
            return;
        }
        int margin = 5;
        cachedRenderX = (anchorCorner == 2 || anchorCorner == 4) ? Math.max(0, screenW - textW - margin) : margin;
        cachedRenderY = (anchorCorner == 3 || anchorCorner == 4) ? Math.max(0, screenH - totalH - margin) : margin;
    }

    private static int resolveColor(boolean autoColor, int staticColor, double fps) {
        if (!autoColor) {
            return staticColor;
        }
        return AccurateFpsPlusClient.interpolateGradientColor(fps);
    }

    public static int interpolateGradientColor(double fps) {
        double lowT;
        double medT;
        double highT;

        if (colorThresholdMode == 0) {
            lowT = telemetryEngine.getDynamicLowThreshold();
            medT = telemetryEngine.getDynamicMedThreshold();
            highT = telemetryEngine.getDynamicHighThreshold();
        } else {
            lowT = (double) manualThresholdLow;
            medT = (double) manualThresholdMed;
            highT = (double) manualThresholdHigh;
        }

        if (medT <= lowT) {
            medT = lowT + 1.0;
        }
        if (highT <= medT) {
            highT = medT + 1.0;
        }

        if (fps <= lowT) {
            return gradientColorLow;
        }
        if (fps >= highT) {
            return gradientColorHigh;
        }
        if (fps < medT) {
            double factor = (fps - lowT) / (medT - lowT);
            return interpolateColor(gradientColorLow, gradientColorMed, factor);
        } else {
            double factor = (fps - medT) / (highT - medT);
            return interpolateColor(gradientColorMed, gradientColorHigh, factor);
        }
    }

    public static int interpolateColor(int color1, int color2, double factor) {
        factor = Math.max(0.0, Math.min(1.0, factor));
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) Math.round(r1 + factor * (r2 - r1));
        int g = (int) Math.round(g1 + factor * (g2 - g1));
        int b = (int) Math.round(b1 + factor * (b2 - b1));

        return (r << 16) | (g << 8) | b;
    }

    private static String formatPart(String prefix, int precision, double value, String suffix) {
        return prefix + AccurateFpsPlusClient.formatDecimalFast(value, precision) + suffix;
    }

    private static String formatDecimalFast(double value, int precision) {
        if (precision < 0 || precision > 4) {
            precision = 0;
        }
        if (precision == 0) {
            return Long.toString(Math.round(value));
        }
        long scaled = Math.round(value * (double)POWERS_OF_10[precision]);
        long whole = scaled / POWERS_OF_10[precision];
        long frac = Math.abs(scaled % POWERS_OF_10[precision]);
        String fracStr = Long.toString(frac);
        while (fracStr.length() < precision) {
            fracStr = "0" + fracStr;
        }
        return whole + "." + fracStr;
    }

    private static void placeInOrder(String[] slots, int[] colors, int preferredPosition, boolean enabled, String part, int color) {
        if (!enabled) {
            return;
        }
        int len = slots.length;
        int startIndex = Math.max(0, Math.min(len - 1, preferredPosition - 1));
        for (int offset = 0; offset < len; ++offset) {
            int index = (startIndex + offset) % len;
            if (slots[index] != null) continue;
            slots[index] = part;
            colors[index] = color;
            return;
        }
        slots[len - 1] = part;
        colors[len - 1] = color;
    }

    protected static int maxPollingRate() {
        int min = Integer.MAX_VALUE;
        if (mainUpdateInterval < min) {
            min = mainUpdateInterval;
        }
        if (avrUpdateInterval < min) {
            min = avrUpdateInterval;
        }
        if (maxUpdateInterval < min) {
            min = maxUpdateInterval;
        }
        if (minUpdateInterval < min) {
            min = minUpdateInterval;
        }
        if (onePercentLowUpdateInterval < min) {
            min = onePercentLowUpdateInterval;
        }
        if (zeroOnePercentLowUpdateInterval < min) {
            min = zeroOnePercentLowUpdateInterval;
        }
        return Math.max(1, min - 1);
    }

    public static void markHudDirty() {
        ++configRevision;
        positionDirty = true;
        AccurateFpsPlusClient.updateCachedOverlayColor();
    }

    static void updateCachedOverlayColor() {
        int alpha = Math.max(0, Math.min(255, (int)((double)(100 - overlayTransparency) * 2.55)));
        cachedOverlayColorWithAlpha = alpha << 24 | overlayColor & 0xFFFFFF;
    }

    private static KeyMapping createToggleKeyMapping() {
        try {
            Class<?> categoryClass = null;
            for (Class<?> nested : KeyMapping.class.getDeclaredClasses()) {
                if ("Category".equals(nested.getSimpleName())) {
                    categoryClass = nested;
                    break;
                }
            }
            if (categoryClass != null) {
                Object category = null;
                try {
                    category = categoryClass.getField("MISC").get(null);
                } catch (Throwable ignored) {
                }
                if (category != null) {
                    java.lang.reflect.Constructor<KeyMapping> ctor = KeyMapping.class.getConstructor(
                        String.class,
                        InputConstants.Type.class,
                        int.class,
                        categoryClass
                    );
                    return ctor.newInstance("key.accuratefpsplus.toggleHUD", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), category);
                }
            }
        } catch (Throwable ignored) {
        }

        try {
            java.lang.reflect.Constructor<KeyMapping> ctor = KeyMapping.class.getConstructor(
                String.class,
                InputConstants.Type.class,
                int.class,
                String.class
            );
            return ctor.newInstance("key.accuratefpsplus.toggleHUD", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "category.accuratefpsplus.title");
        } catch (Throwable t) {
            return null;
        }
    }

    public static int detectDisplayRefreshRate() {
        try {
            Minecraft mc = Minecraft.getInstance();
            long windowHandle = 0L;
            if (mc != null && mc.getWindow() != null) {
                try {
                    java.lang.reflect.Method m = mc.getWindow().getClass().getMethod("handle");
                    windowHandle = (Long) m.invoke(mc.getWindow());
                } catch (Throwable t1) {
                    try {
                        java.lang.reflect.Method m = mc.getWindow().getClass().getMethod("getWindow");
                        windowHandle = (Long) m.invoke(mc.getWindow());
                    } catch (Throwable ignored) {
                    }
                }
            }
            if (windowHandle == 0L) {
                windowHandle = GLFW.glfwGetCurrentContext();
            }
            if (windowHandle != 0L) {
                long monitor = GLFW.glfwGetWindowMonitor(windowHandle);
                if (monitor == 0L) {
                    monitor = GLFW.glfwGetPrimaryMonitor();
                }
                if (monitor != 0L) {
                    GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
                    if (vidMode != null && vidMode.refreshRate() > 0) {
                        return vidMode.refreshRate();
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return 60;
    }

    static {
        lastConfigRevision = -1L;
        positionDirty = true;
        cachedScreenWidth = -1;
        cachedScreenHeight = -1;
        toggleHUD = true;
        hideWithF3 = false;
        xPos = 5;
        yPos = 5;
        shadow = true;
        pollingRate = 100;
        calculationMode = 1;
        historyWindowSeconds = 5;
        anchorCorner = 0;
        darkModeOverlay = false;
        overlayColor = 0;
        overlayTransparency = 50;
        cachedOverlayColorWithAlpha = Integer.MIN_VALUE;
        currentPreset = 1;
        showFps = true;
        fpsPosition = 1;
        fpsPrecision = 0;
        beforeFps = "FPS: ";
        afterFps = "";
        mainUpdateInterval = 250;
        fpsColor = 0xFFFFFF;
        fpsAutoColor = true;
        showFrametime = false;
        frametimePosition = 3;
        frametimePrecision = 1;
        beforeFrametime = "FT: ";
        afterFrametime = "ms";
        frametimeColor = 0xFFFFFF;
        frametimeAutoColor = false;
        showAvr = false;
        avrPosition = 2;
        avrPrecision = 0;
        beforeAvr = "AVG: ";
        afterAvr = "";
        avrUpdateInterval = 2000;
        avrColor = 0xFFFFFF;
        avrAutoColor = false;
        showMax = false;
        maxPosition = 3;
        maxPrecision = 0;
        beforeMax = "MAX: ";
        afterMax = "";
        maxUpdateInterval = 2000;
        maxColor = 0xFFFFFF;
        maxAutoColor = false;
        showMin = false;
        minPosition = 4;
        minPrecision = 0;
        beforeMin = "MIN: ";
        afterMin = "";
        minUpdateInterval = 2000;
        minColor = 0xFFFFFF;
        minAutoColor = false;
        showOnePercentLow = false;
        onePercentLowPosition = 1;
        onePercentLowPrecision = 0;
        beforeOnePercentLow = "1% LOW: ";
        afterOnePercentLow = "";
        onePercentLowUpdateInterval = 2000;
        onePercentLowColor = 0xFFFFFF;
        onePercentLowAutoColor = false;
        showZeroOnePercentLow = false;
        zeroOnePercentLowPosition = 2;
        zeroOnePercentLowPrecision = 0;
        beforeZeroOnePercentLow = "0.1% LOW: ";
        afterZeroOnePercentLow = "";
        zeroOnePercentLowUpdateInterval = 2000;
        zeroOnePercentLowColor = 0xFFFFFF;
        zeroOnePercentLowAutoColor = false;
        colorThresholdMode = 0;
        gradientColorLow = 0xFF5555;
        gradientColorMed = 0xFFFF55;
        gradientColorHigh = 0x55FF55;
        manualThresholdLow = 30;
        manualThresholdMed = 60;
        manualThresholdHigh = 120;

        java.lang.reflect.Method getter = null;
        java.lang.reflect.Method push = null;
        java.lang.reflect.Method pop = null;
        java.lang.reflect.Method scale = null;
        boolean scale3d = false;
        try {
            getter = net.minecraft.client.gui.GuiGraphics.class.getMethod("pose");
            Class<?> poseClass = getter.getReturnType();
            try {
                push = poseClass.getMethod("pushMatrix");
                pop = poseClass.getMethod("popMatrix");
                scale = poseClass.getMethod("scale", float.class, float.class);
                scale3d = false;
            } catch (NoSuchMethodException e) {
                push = poseClass.getMethod("pushPose");
                pop = poseClass.getMethod("popPose");
                scale = poseClass.getMethod("scale", float.class, float.class, float.class);
                scale3d = true;
            }
        } catch (Throwable ignored) {
        }
        POSE_GETTER = getter;
        POSE_PUSH = push;
        POSE_POP = pop;
        POSE_SCALE = scale;
        POSE_SCALE_IS_3D = scale3d;
    }

    private static void drawScaledText(net.minecraft.client.gui.GuiGraphics context, net.minecraft.client.gui.Font font, FormattedCharSequence text, float x, float y, float scale, int color, boolean shadow) {
        if (scale == 1.0f) {
            context.drawString(font, text, (int) x, (int) y, color, shadow);
            return;
        }
        Object pose = null;
        try {
            if (POSE_GETTER != null) {
                pose = POSE_GETTER.invoke(context);
            }
        } catch (Throwable ignored) {
        }
        if (pose == null || POSE_PUSH == null || POSE_POP == null || POSE_SCALE == null) {
            context.drawString(font, text, (int) x, (int) y, color, shadow);
            return;
        }
        try {
            POSE_PUSH.invoke(pose);
            try {
                if (POSE_SCALE_IS_3D) {
                    POSE_SCALE.invoke(pose, scale, scale, 1.0f);
                } else {
                    POSE_SCALE.invoke(pose, scale, scale);
                }
                float invScale = 1.0f / scale;
                context.drawString(font, text, Math.round(x * invScale), Math.round(y * invScale), color, shadow);
            } finally {
                POSE_POP.invoke(pose);
            }
        } catch (Throwable t) {
            context.drawString(font, text, (int) x, (int) y, color, shadow);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record HudSnapshot(Component mainText, FormattedCharSequence mainOrderedText, String mainRawText, Component lowText, FormattedCharSequence lowOrderedText, String lowRawText, int mainTextWidth, int lowTextWidth, int fontHeight) {
    }
}
