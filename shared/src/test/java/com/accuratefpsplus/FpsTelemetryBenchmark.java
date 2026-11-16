package com.accuratefpsplus;

import java.util.ArrayDeque;
import java.util.Deque;

public final class FpsTelemetryBenchmark {
    private static final long NANOS_PER_MILLI = 1_000_000L;
    private static final long HISTORY_WINDOW_MILLIS = 499L;
    private static final long[] PERCENTILE_FRAME_DURATIONS_NANOS = {
        500_000L, 750_000L, 1_250_000L, 1_500_000L
    };
    private static volatile double blackhole;

    private FpsTelemetryBenchmark() {
    }

    public static void main(String[] args) {
        int iterations = args.length == 0 ? 50_000 : Integer.parseInt(args[0]);
        if (iterations < 1) {
            throw new IllegalArgumentException("Iteration count must be positive");
        }

        for (int i = 0; i < 3; i++) {
            runAggregation(iterations / 5 + 1, false);
            runAggregation(iterations / 5 + 1, true);
            runPercentileEngine(Math.max(1_000, iterations / 10), false);
            runPercentileEngine(Math.max(1_000, iterations / 10), true);
        }

        long[] fullScanTimes = new long[5];
        long[] rollingTimes = new long[5];
        for (int i = 0; i < fullScanTimes.length; i++) {
            AggregationResult fullScan = runAggregation(iterations, false);
            AggregationResult rolling = runAggregation(iterations, true);
            assertSameStatistics(fullScan.statistics(), rolling.statistics());
            fullScanTimes[i] = fullScan.elapsedNanos();
            rollingTimes[i] = rolling.elapsedNanos();
        }
        long fullScanMedian = median(fullScanTimes);
        long rollingMedian = median(rollingTimes);
        System.out.printf(
            "Aggregation (%d iterations, 1 ms cadence, 500 retained samples): full scan %.2f ms; rolling %.2f ms; %.2fx speedup%n",
            iterations,
            fullScanMedian / 1_000_000.0,
            rollingMedian / 1_000_000.0,
            (double) fullScanMedian / rollingMedian
        );

        int engineIterations = Math.max(1_000, iterations / 10);
        long[] percentilesDisabledTimes = new long[5];
        long[] percentilesEnabledTimes = new long[5];
        for (int i = 0; i < percentilesDisabledTimes.length; i++) {
            percentilesDisabledTimes[i] = runPercentileEngine(engineIterations, false);
            percentilesEnabledTimes[i] = runPercentileEngine(engineIterations, true);
        }
        System.out.printf(
            "Current engine (%d frames, 1 ms average cadence): percentiles off %.2f ms; on %.2f ms%n",
            engineIterations,
            median(percentilesDisabledTimes) / 1_000_000.0,
            median(percentilesEnabledTimes) / 1_000_000.0
        );
    }

    private static AggregationResult runAggregation(int iterations, boolean rolling) {
        Deque<FpsTelemetryEngine.SamplePoint> samples = new ArrayDeque<>();
        for (int frame = 1; frame <= 500; frame++) {
            samples.addLast(new FpsTelemetryEngine.SamplePoint(frame * NANOS_PER_MILLI, fpsFor(frame)));
        }
        if (samples.size() != 500) {
            throw new IllegalStateException("Benchmark setup must retain exactly 500 samples");
        }
        long nowNanos = 500L * NANOS_PER_MILLI;
        FpsTelemetryEngine.RollingStatistics statistics = new FpsTelemetryEngine.RollingStatistics();
        statistics.update(nowNanos, HISTORY_WINDOW_MILLIS, samples);
        double checksum = 0.0;
        long startedAt = System.nanoTime();

        for (int frame = 1; frame <= iterations; frame++) {
            nowNanos += NANOS_PER_MILLI;
            samples.addLast(new FpsTelemetryEngine.SamplePoint(nowNanos, fpsFor(frame + 500)));
            long cutoffNanos = nowNanos - HISTORY_WINDOW_MILLIS * NANOS_PER_MILLI;
            int expiredSamples = 0;
            while (samples.peekFirst().timestampNanos() < cutoffNanos) {
                samples.removeFirst();
                expiredSamples++;
            }
            if (expiredSamples != 1 || samples.size() != 500) {
                throw new IllegalStateException("Benchmark must expire one sample per 1 ms update");
            }

            double average;
            double maximum;
            double minimum;
            if (rolling) {
                statistics.update(nowNanos, HISTORY_WINDOW_MILLIS, samples);
                average = statistics.average(0.0);
                maximum = statistics.maximum(0.0);
                minimum = statistics.minimum(0.0);
            } else {
                double total = 0.0;
                maximum = Double.NEGATIVE_INFINITY;
                minimum = Double.POSITIVE_INFINITY;
                int count = 0;
                for (FpsTelemetryEngine.SamplePoint sample : samples) {
                    if (sample.timestampNanos() < cutoffNanos) {
                        continue;
                    }
                    total += sample.fps();
                    maximum = Math.max(maximum, sample.fps());
                    minimum = Math.min(minimum, sample.fps());
                    count++;
                }
                average = total / count;
            }
            checksum += average + maximum + minimum;
        }
        long elapsedNanos = System.nanoTime() - startedAt;
        blackhole = checksum;
        Statistics finalStatistics = rolling
            ? new Statistics(
                statistics.average(0.0),
                statistics.maximum(0.0),
                statistics.minimum(0.0)
            )
            : fullScan(samples, nowNanos);
        return new AggregationResult(elapsedNanos, finalStatistics);
    }

    private static Statistics fullScan(
        Deque<FpsTelemetryEngine.SamplePoint> samples,
        long nowNanos
    ) {
        long cutoffNanos = nowNanos - HISTORY_WINDOW_MILLIS * NANOS_PER_MILLI;
        double total = 0.0;
        double maximum = Double.NEGATIVE_INFINITY;
        double minimum = Double.POSITIVE_INFINITY;
        int count = 0;
        for (FpsTelemetryEngine.SamplePoint sample : samples) {
            if (sample.timestampNanos() < cutoffNanos) {
                continue;
            }
            total += sample.fps();
            maximum = Math.max(maximum, sample.fps());
            minimum = Math.min(minimum, sample.fps());
            count++;
        }
        return new Statistics(total / count, maximum, minimum);
    }

    private static long runPercentileEngine(int frames, boolean percentilesEnabled) {
        FpsTelemetryEngine engine = new FpsTelemetryEngine(0L);
        engine.setCalculationMode(0);
        engine.setComputePercentiles(percentilesEnabled);
        long nowNanos = 0L;
        long startedAt = System.nanoTime();
        for (int frame = 0; frame < frames; frame++) {
            nowNanos += PERCENTILE_FRAME_DURATIONS_NANOS[frame % PERCENTILE_FRAME_DURATIONS_NANOS.length];
            engine.onFrameAt(nowNanos, 1, 1, 1, 1, 1, 1, 1, HISTORY_WINDOW_MILLIS);
        }
        long elapsedNanos = System.nanoTime() - startedAt;
        if (!engine.hasData()) {
            throw new IllegalStateException("Telemetry engine did not collect benchmark samples");
        }
        blackhole = engine.getDisplayedOnePercentLow() + engine.getDisplayedZeroOnePercentLow();
        return elapsedNanos;
    }

    private static double fpsFor(int frame) {
        return 30.0 + (frame * 73L % 901L);
    }

    private static long median(long[] values) {
        long[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }

    private static void assertSameStatistics(Statistics expected, Statistics actual) {
        if (Math.abs(expected.average() - actual.average()) > 1e-8
            || Math.abs(expected.maximum() - actual.maximum()) > 1e-8
            || Math.abs(expected.minimum() - actual.minimum()) > 1e-8) {
            throw new IllegalStateException("Rolling aggregation does not match the full-scan reference");
        }
    }

    private record Statistics(double average, double maximum, double minimum) {
    }

    private record AggregationResult(long elapsedNanos, Statistics statistics) {
    }
}
