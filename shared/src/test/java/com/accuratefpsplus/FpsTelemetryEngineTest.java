package com.accuratefpsplus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class FpsTelemetryEngineTest {
    private static final long NANOS_PER_MILLI = 1_000_000L;

    @Test
    void emptyEngineStartsWithoutTelemetryData() {
        FpsTelemetryEngine engine = new FpsTelemetryEngine(0L);

        assertFalse(engine.hasData());
        assertEquals(0.0, engine.getDisplayedMainFps(), 1e-9);
        assertEquals(0.0, engine.getDisplayedAverageFps(), 1e-9);
        assertEquals(0.0, engine.getDisplayedOnePercentLow(), 1e-9);
        assertEquals(0.0, engine.getDisplayedZeroOnePercentLow(), 1e-9);
    }

    @Test
    void realTimeAndAveragedModesUseTheirIntendedCalculations() {
        FpsTelemetryEngine realTime = new FpsTelemetryEngine(0L);
        realTime.setCalculationMode(0);
        tick(realTime, 10L, 30, 1, 1_000L);
        tick(realTime, 30L, 30, 1, 1_000L);

        FpsTelemetryEngine averaged = new FpsTelemetryEngine(0L);
        averaged.setCalculationMode(1);
        tick(averaged, 10L, 30, 1, 1_000L);
        tick(averaged, 30L, 30, 1, 1_000L);

        assertEquals(50.0, realTime.getDisplayedMainFps(), 1e-9);
        assertEquals(2.0 * 1_000.0 / 30.0, averaged.getDisplayedMainFps(), 1e-9);
    }

    @Test
    void exponentialModeSmoothsTheSampledFrameRate() {
        FpsTelemetryEngine engine = new FpsTelemetryEngine(0L);
        engine.setCalculationMode(2);
        tick(engine, 10L, 1, 1, 1_000L);
        tick(engine, 30L, 1, 1, 1_000L);

        assertEquals(90.91, engine.getDisplayedMainFps(), 1e-9);
    }

    @Test
    void historyWindowRetainsMoreThanOneThousandSamples() {
        FpsTelemetryEngine engine = new FpsTelemetryEngine(0L);

        for (int frame = 1; frame <= 1_500; frame++) {
            tick(engine, frame, 1, 1_500, 5_000L);
        }

        assertEquals(1_000.0, engine.getDisplayedAverageFps(), 1e-9);
        assertEquals(1_000.0, engine.getDisplayedMaxFps(), 1e-9);
        assertEquals(1_000.0, engine.getDisplayedMinFps(), 1e-9);
    }

    @Test
    void rangeStatisticsKeepTheirIndependentRefreshIntervals() {
        FpsTelemetryEngine engine = new FpsTelemetryEngine(0L);
        engine.setCalculationMode(0);
        engine.onFrameAt(10L * NANOS_PER_MILLI, 1, 1, 10, 20, 40, 10, 10, 1_000L);
        engine.onFrameAt(30L * NANOS_PER_MILLI, 1, 1, 10, 20, 40, 10, 10, 1_000L);

        assertEquals(75.0, engine.getDisplayedAverageFps(), 1e-9);
        assertEquals(100.0, engine.getDisplayedMaxFps(), 1e-9);
        assertEquals(0.0, engine.getDisplayedMinFps(), 1e-9);

        engine.onFrameAt(40L * NANOS_PER_MILLI, 1, 1, 10, 20, 40, 10, 10, 1_000L);

        assertEquals((100.0 + 50.0 + 100.0) / 3.0, engine.getDisplayedAverageFps(), 1e-9);
        assertEquals(100.0, engine.getDisplayedMaxFps(), 1e-9);
        assertEquals(50.0, engine.getDisplayedMinFps(), 1e-9);
    }

    @Test
    void percentilesUseNearestRankAndHandleSmallWindows() {
        FpsTelemetryEngine oneSample = new FpsTelemetryEngine(0L);
        oneSample.setComputePercentiles(true);
        tick(oneSample, 10L, 1, 1, 1_000L);

        assertEquals(100.0, oneSample.getDisplayedOnePercentLow(), 1e-9);
        assertEquals(100.0, oneSample.getDisplayedZeroOnePercentLow(), 1e-9);

        FpsTelemetryEngine twoSamples = new FpsTelemetryEngine(0L);
        twoSamples.setComputePercentiles(true);
        tick(twoSamples, 10L, 1, 1, 1_000L);
        tick(twoSamples, 30L, 1, 1, 1_000L);

        assertEquals(50.0, twoSamples.getDisplayedOnePercentLow(), 1e-9);
        assertEquals(50.0, twoSamples.getDisplayedZeroOnePercentLow(), 1e-9);
    }

    @Test
    void percentileQuickselectMatchesNearestRankForLargeSampleSets() {
        FpsTelemetryEngine engine = new FpsTelemetryEngine(0L);
        double[] expectedValues = new double[1_001];
        long nowNanos = 0L;

        for (int fps = 1; fps <= 1_000; fps++) {
            long frameDurationNanos = Math.round(1_000_000_000.0 / fps);
            nowNanos += frameDurationNanos;
            expectedValues[fps - 1] = 1_000_000_000.0 / frameDurationNanos;
            engine.onFrameAt(nowNanos, 1, 100_000, 100_000, 100_000, 100_000, 60_000, 60_000, 60_000L);
        }

        nowNanos += NANOS_PER_MILLI;
        expectedValues[1_000] = 1_000.0;
        engine.setComputePercentiles(true);
        engine.onFrameAt(nowNanos, 1, 100_000, 100_000, 100_000, 100_000, 1, 1, 60_000L);
        Arrays.sort(expectedValues);

        assertEquals(nearestRank(expectedValues, 0.01), engine.getDisplayedOnePercentLow(), 1e-9);
        assertEquals(nearestRank(expectedValues, 0.001), engine.getDisplayedZeroOnePercentLow(), 1e-9);
    }

    @Test
    void nonMonotonicTimestampsAreIgnoredWithoutLosingSubsequentSamples() {
        FpsTelemetryEngine engine = new FpsTelemetryEngine(0L);
        engine.setCalculationMode(0);
        tick(engine, 10L, 1, 1, 1_000L);
        engine.onFrameAt(5L * NANOS_PER_MILLI, 1, 1, 1, 1, 1, 1, 1, 1_000L);
        tick(engine, 20L, 1, 1, 1_000L);

        assertTrue(engine.hasData());
        assertEquals(100.0, engine.getDisplayedMainFps(), 1e-9);
        assertEquals(10.0, engine.getLastFrametimeMs(), 1e-9);
        assertFalse(Double.isNaN(engine.getDisplayedMainFps()));
    }

    private static void tick(FpsTelemetryEngine engine, long timeMillis, int pollingMillis, int updateIntervalMillis, long historyMillis) {
        engine.onFrameAt(
            timeMillis * NANOS_PER_MILLI,
            pollingMillis,
            updateIntervalMillis,
            updateIntervalMillis,
            updateIntervalMillis,
            updateIntervalMillis,
            updateIntervalMillis,
            updateIntervalMillis,
            historyMillis
        );
    }

    private static double nearestRank(double[] sortedValues, double percentile) {
        int index = Math.max(0, (int) Math.ceil(sortedValues.length * percentile) - 1);
        return sortedValues[index];
    }
}
