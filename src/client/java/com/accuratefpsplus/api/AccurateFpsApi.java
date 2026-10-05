package com.accuratefpsplus.api;

import com.accuratefpsplus.AccurateFpsPlusClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Lightweight client-side access to the live telemetry values displayed by the HUD.
 */
@Environment(EnvType.CLIENT)
public final class AccurateFpsApi {
    private AccurateFpsApi() {}

    /**
     * @return true if AccurateFPS+ client telemetry is active.
     */
    public static boolean isAvailable() {
        return AccurateFpsPlusClient.getTelemetryEngine() != null;
    }

    /**
     * @return true if telemetry has collected at least one valid frame sample.
     */
    public static boolean hasData() {
        return isAvailable() && AccurateFpsPlusClient.getTelemetryEngine().hasData();
    }

    /**
     * @return live displayed FPS matching the HUD.
     */
    public static double getFps() {
        return isAvailable() ? AccurateFpsPlusClient.getTelemetryEngine().getDisplayedMainFps() : 0.0;
    }

    /**
     * @return live displayed average FPS matching the HUD.
     */
    public static double getAverageFps() {
        return isAvailable() ? AccurateFpsPlusClient.getTelemetryEngine().getDisplayedAverageFps() : 0.0;
    }

    /**
     * @return live displayed maximum FPS matching the HUD.
     */
    public static double getMaxFps() {
        return isAvailable() ? AccurateFpsPlusClient.getTelemetryEngine().getDisplayedMaxFps() : 0.0;
    }

    /**
     * @return live displayed minimum FPS matching the HUD.
     */
    public static double getMinFps() {
        return isAvailable() ? AccurateFpsPlusClient.getTelemetryEngine().getDisplayedMinFps() : 0.0;
    }

    /**
     * @return live displayed 1% Low FPS matching the HUD.
     */
    public static double getOnePercentLowFps() {
        return isAvailable() ? AccurateFpsPlusClient.getTelemetryEngine().getDisplayedOnePercentLow() : 0.0;
    }

    /**
     * @return live displayed 0.1% Low FPS matching the HUD.
     */
    public static double getZeroOnePercentLowFps() {
        return isAvailable() ? AccurateFpsPlusClient.getTelemetryEngine().getDisplayedZeroOnePercentLow() : 0.0;
    }

    /**
     * @return measured duration of the most recent frame in milliseconds.
     */
    public static double getLastFrametimeMs() {
        return isAvailable() ? AccurateFpsPlusClient.getTelemetryEngine().getLastFrametimeMs() : 0.0;
    }

    /**
     * @return live displayed frametime in milliseconds matching the HUD.
     */
    public static double getDisplayedFrametimeMs() {
        return isAvailable() ? AccurateFpsPlusClient.getTelemetryEngine().getDisplayedFrametimeMs() : 0.0;
    }

    /**
     * Allows companion mods or consumers to ensure 1% and 0.1% low percentiles are computed,
     * even if the player has toggled them off in the HUD UI.
     */
    public static void requirePercentiles(boolean require) {
        if (isAvailable()) {
            AccurateFpsPlusClient.getTelemetryEngine().setExternalPercentilesRequested(require);
        }
    }
}
