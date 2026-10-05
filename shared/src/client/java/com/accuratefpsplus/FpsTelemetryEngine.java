package com.accuratefpsplus;

import java.util.ArrayDeque;
import java.util.Deque;

public class FpsTelemetryEngine {
    private static final int NANOS_PER_MILLI = 1_000_000;
    private long pollTotalNanos;
    private int pollSampleCount;
    private final Deque<SamplePoint> sampledFps = new ArrayDeque<>();
    private long lastFrameNanos;
    private long lastPollNanos;
    private long lastMainUpdateNanos;
    private long lastAverageUpdateNanos;
    private long lastMaxUpdateNanos;
    private long lastMinUpdateNanos;
    private long lastOnePercentLowUpdateNanos;
    private long lastZeroOnePercentLowUpdateNanos;
    private int calculationMode = 1;
    private double latestSampledFps;
    private double displayedMainFps;
    private double displayedAverageFps;
    private double displayedMaxFps;
    private double displayedMinFps;
    private double displayedOnePercentLow;
    private double displayedZeroOnePercentLow;
    private double lastFrametimeMs;
    private double displayedFrametimeMs;
    private long displayRevision;
    private static final double EMA_ALPHA = 0.1818;
    private double emaFps = -1.0;
    private double sessionEmaFps = -1.0;
    private double dynamicLowThreshold = 30.0;
    private double dynamicMedThreshold = 60.0;
    private double dynamicHighThreshold = 120.0;
    private boolean computePercentiles = false;
    private boolean externalPercentilesRequested = false;

    public FpsTelemetryEngine() {
        this(System.nanoTime());
    }

    FpsTelemetryEngine(long initialNanos) {
        this.lastFrameNanos = initialNanos;
        this.lastPollNanos = initialNanos;
        this.lastMainUpdateNanos = initialNanos;
        this.lastAverageUpdateNanos = initialNanos;
        this.lastMaxUpdateNanos = initialNanos;
        this.lastMinUpdateNanos = initialNanos;
        this.lastOnePercentLowUpdateNanos = initialNanos;
        this.lastZeroOnePercentLowUpdateNanos = initialNanos;
    }

    public void setCalculationMode(int mode) {
        this.calculationMode = Math.max(0, Math.min(2, mode));
    }

    public void setComputePercentiles(boolean enabled) {
        this.computePercentiles = enabled;
    }

    public void setExternalPercentilesRequested(boolean requested) {
        this.externalPercentilesRequested = requested;
    }

    public int getCalculationMode() {
        return this.calculationMode;
    }

    public void onFrame(int pollingRateMillis, int mainIntervalMillis, int averageIntervalMillis, int maxIntervalMillis, int minIntervalMillis, int onePercentLowIntervalMillis, int zeroOnePercentLowIntervalMillis, long historyWindowMillis) {
        this.onFrameAt(System.nanoTime(), pollingRateMillis, mainIntervalMillis, averageIntervalMillis, maxIntervalMillis, minIntervalMillis, onePercentLowIntervalMillis, zeroOnePercentLowIntervalMillis, historyWindowMillis);
    }

    void onFrameAt(long nowNanos, int pollingRateMillis, int mainIntervalMillis, int averageIntervalMillis, int maxIntervalMillis, int minIntervalMillis, int onePercentLowIntervalMillis, int zeroOnePercentLowIntervalMillis, long historyWindowMillis) {
        long cutoffNanos;
        pollingRateMillis = Math.max(1, pollingRateMillis);
        mainIntervalMillis = Math.max(1, mainIntervalMillis);
        averageIntervalMillis = Math.max(1, averageIntervalMillis);
        maxIntervalMillis = Math.max(1, maxIntervalMillis);
        minIntervalMillis = Math.max(1, minIntervalMillis);
        onePercentLowIntervalMillis = Math.max(1, onePercentLowIntervalMillis);
        zeroOnePercentLowIntervalMillis = Math.max(1, zeroOnePercentLowIntervalMillis);
        historyWindowMillis = Math.max(1L, historyWindowMillis);
        long deltaNanos = nowNanos - this.lastFrameNanos;
        if (deltaNanos <= 0L) {
            return;
        }
        this.lastFrameNanos = nowNanos;
        this.lastFrametimeMs = (double)deltaNanos / 1000000.0;
        this.pollTotalNanos += deltaNanos;
        ++this.pollSampleCount;
        long elapsedSincePollMillis = (nowNanos - this.lastPollNanos) / 1000000L;
        if (elapsedSincePollMillis < (long)pollingRateMillis) {
            return;
        }
        this.lastPollNanos = nowNanos;
        double instantFps = 1.0E9 / (double)deltaNanos;
        this.latestSampledFps = switch (this.calculationMode) {
            case 0 -> instantFps;
            case 2 -> {
                if (this.emaFps < 0.0) {
                    this.emaFps = instantFps;
                }
                yield this.emaFps = EMA_ALPHA * instantFps + (1.0 - EMA_ALPHA) * this.emaFps;
            }
            default -> this.aggregatePollToFps();
        };
        this.pollTotalNanos = 0L;
        this.pollSampleCount = 0;
        this.sampledFps.addLast(new SamplePoint(nowNanos, this.latestSampledFps));

        // Session FPS tracking and dynamic gradient thresholds
        if (this.sessionEmaFps < 0.0) {
            this.sessionEmaFps = this.latestSampledFps;
            this.updateDynamicThresholdsInstant(this.latestSampledFps);
        } else {
            this.sessionEmaFps = 0.02 * this.latestSampledFps + 0.98 * this.sessionEmaFps;
            this.updateDynamicThresholdsSmooth();
        }

        long retainMillis = Math.max(historyWindowMillis, (long)Math.max(Math.max(mainIntervalMillis, averageIntervalMillis), Math.max(maxIntervalMillis, minIntervalMillis)));
        this.pruneOldSamples(nowNanos - retainMillis * NANOS_PER_MILLI - 1000L * NANOS_PER_MILLI);
        if (nowNanos - this.lastMainUpdateNanos >= (long)mainIntervalMillis * NANOS_PER_MILLI) {
            this.displayedMainFps = this.latestSampledFps;
            // Use actual measured frame duration, not FPS-derived approximation
            this.displayedFrametimeMs = this.lastFrametimeMs > 0.0 ? this.lastFrametimeMs : (this.displayedMainFps > 0.0 ? 1000.0 / this.displayedMainFps : 0.0);
            ++this.displayRevision;
            this.lastMainUpdateNanos = nowNanos;
        }
        boolean updateAverage = nowNanos - this.lastAverageUpdateNanos >= (long)averageIntervalMillis * NANOS_PER_MILLI;
        boolean updateMax = nowNanos - this.lastMaxUpdateNanos >= (long)maxIntervalMillis * NANOS_PER_MILLI;
        boolean updateMin = nowNanos - this.lastMinUpdateNanos >= (long)minIntervalMillis * NANOS_PER_MILLI;
        if (updateAverage || updateMax || updateMin) {
            double sum = 0.0;
            double max = Double.NEGATIVE_INFINITY;
            double min = Double.POSITIVE_INFINITY;
            int count = 0;
            cutoffNanos = nowNanos - historyWindowMillis * NANOS_PER_MILLI;
            for (SamplePoint point : this.sampledFps) {
                if (point.timestampNanos < cutoffNanos) {
                    continue;
                }
                sum += point.fps;
                max = Math.max(max, point.fps);
                min = Math.min(min, point.fps);
                ++count;
            }
            if (count == 0) {
                sum = this.latestSampledFps;
                max = this.latestSampledFps;
                min = this.latestSampledFps;
                count = 1;
            }
            if (updateAverage) {
                this.displayedAverageFps = sum / count;
                this.lastAverageUpdateNanos = nowNanos;
                ++this.displayRevision;
            }
            if (updateMax) {
                this.displayedMaxFps = max;
                this.lastMaxUpdateNanos = nowNanos;
                ++this.displayRevision;
            }
            if (updateMin) {
                this.displayedMinFps = min;
                this.lastMinUpdateNanos = nowNanos;
                ++this.displayRevision;
            }
        }
        boolean activePercentiles = this.computePercentiles || this.externalPercentilesRequested;
        if (activePercentiles && nowNanos - this.lastOnePercentLowUpdateNanos >= (long)onePercentLowIntervalMillis * NANOS_PER_MILLI) {
            cutoffNanos = nowNanos - historyWindowMillis * NANOS_PER_MILLI;
            this.displayedOnePercentLow = this.computePercentileLow(cutoffNanos, 0.01);
            ++this.displayRevision;
            this.lastOnePercentLowUpdateNanos = nowNanos;
        }
        if (activePercentiles && nowNanos - this.lastZeroOnePercentLowUpdateNanos >= (long)zeroOnePercentLowIntervalMillis * NANOS_PER_MILLI) {
            cutoffNanos = nowNanos - historyWindowMillis * NANOS_PER_MILLI;
            this.displayedZeroOnePercentLow = this.computePercentileLow(cutoffNanos, 0.001);
            ++this.displayRevision;
            this.lastZeroOnePercentLowUpdateNanos = nowNanos;
        }
    }

    public boolean hasData() {
        return !this.sampledFps.isEmpty() && this.latestSampledFps > 0.0;
    }

    public double getDisplayedMainFps() {
        return this.displayedMainFps;
    }

    public double getDisplayedFrametimeMs() {
        return this.displayedFrametimeMs > 0.0 ? this.displayedFrametimeMs : (this.displayedMainFps > 0.0 ? 1000.0 / this.displayedMainFps : 0.0);
    }

    public double getLastFrametimeMs() {
        return this.lastFrametimeMs > 0.0 ? this.lastFrametimeMs : (this.displayedMainFps > 0.0 ? 1000.0 / this.displayedMainFps : 0.0);
    }

    public double getDisplayedAverageFps() {
        return this.displayedAverageFps;
    }

    public double getDisplayedMaxFps() {
        return this.displayedMaxFps;
    }

    public double getDisplayedMinFps() {
        return this.displayedMinFps;
    }

    public double getDisplayedOnePercentLow() {
        return this.displayedOnePercentLow;
    }

    public double getDisplayedZeroOnePercentLow() {
        return this.displayedZeroOnePercentLow;
    }

    public long getDisplayRevision() {
        return this.displayRevision;
    }

    private double aggregatePollToFps() {
        if (this.pollSampleCount == 0) {
            return this.latestSampledFps;
        }
        if (this.pollTotalNanos <= 0L) {
            return this.latestSampledFps;
        }
        return (double)this.pollSampleCount * 1.0E9 / (double)this.pollTotalNanos;
    }

    private void pruneOldSamples(long cutoffNanos) {
        while (!this.sampledFps.isEmpty() && this.sampledFps.peekFirst().timestampNanos < cutoffNanos) {
            this.sampledFps.removeFirst();
        }
    }

    private double computePercentileLow(long cutoffNanos, double percentile) {
        int count = 0;
        for (SamplePoint point : this.sampledFps) {
            if (point.timestampNanos < cutoffNanos) continue;
            ++count;
        }
        if (count == 0) {
            return this.latestSampledFps;
        }
        double[] fpsValues = new double[count];
        int i = 0;
        for (SamplePoint point : this.sampledFps) {
            if (point.timestampNanos < cutoffNanos) continue;
            fpsValues[i++] = point.fps;
        }
        int k = (int)Math.ceil((double)count * percentile) - 1;
        k = Math.max(0, Math.min(k, count - 1));
        return this.quickSelect(fpsValues, 0, count - 1, k);
    }

    public double getDynamicLowThreshold() {
        return this.dynamicLowThreshold;
    }

    public double getDynamicMedThreshold() {
        return this.dynamicMedThreshold;
    }

    public double getDynamicHighThreshold() {
        return this.dynamicHighThreshold;
    }

    public double getSessionAverageFps() {
        return this.sessionEmaFps > 0.0 ? this.sessionEmaFps : (this.displayedAverageFps > 0.0 ? this.displayedAverageFps : 60.0);
    }

    private void updateDynamicThresholdsInstant(double fps) {
        double baseline = Math.max(15.0, fps);
        double targetHigh = Math.max(30.0, baseline);
        double targetMed = Math.max(20.0, targetHigh * 0.75);
        double targetLow = Math.max(10.0, targetHigh * 0.50);
        if (targetMed <= targetLow + 5.0) {
            targetMed = targetLow + 5.0;
        }
        if (targetHigh <= targetMed + 5.0) {
            targetHigh = targetMed + 5.0;
        }
        this.dynamicLowThreshold = targetLow;
        this.dynamicMedThreshold = targetMed;
        this.dynamicHighThreshold = targetHigh;
    }

    private void updateDynamicThresholdsSmooth() {
        double baseline = Math.max(15.0, this.sessionEmaFps);
        double targetHigh = Math.max(30.0, baseline);
        double targetMed = Math.max(20.0, targetHigh * 0.75);
        double targetLow = Math.max(10.0, targetHigh * 0.50);
        if (targetMed <= targetLow + 5.0) {
            targetMed = targetLow + 5.0;
        }
        if (targetHigh <= targetMed + 5.0) {
            targetHigh = targetMed + 5.0;
        }
        this.dynamicLowThreshold += 0.05 * (targetLow - this.dynamicLowThreshold);
        this.dynamicMedThreshold += 0.05 * (targetMed - this.dynamicMedThreshold);
        this.dynamicHighThreshold += 0.05 * (targetHigh - this.dynamicHighThreshold);
    }

    private double quickSelect(double[] arr, int left, int right, int k) {
        while (left < right) {
            if (right - left <= 4) {
                // Insertion sort for small partitions
                for (int i = left + 1; i <= right; ++i) {
                    double val = arr[i];
                    int j = i - 1;
                    while (j >= left && arr[j] > val) {
                        arr[j + 1] = arr[j];
                        --j;
                    }
                    arr[j + 1] = val;
                }
                return arr[k];
            }
            int pivotIndex = this.partition(arr, left, right);
            if (pivotIndex == k) {
                return arr[k];
            }
            if (pivotIndex < k) {
                left = pivotIndex + 1;
            } else {
                right = pivotIndex - 1;
            }
        }
        return arr[left];
    }

    private int partition(double[] arr, int left, int right) {
        int mid = left + (right - left) / 2;
        if (arr[mid] < arr[left]) {
            this.swap(arr, left, mid);
        }
        if (arr[right] < arr[left]) {
            this.swap(arr, left, right);
        }
        if (arr[right] < arr[mid]) {
            this.swap(arr, mid, right);
        }
        this.swap(arr, mid, right - 1);
        double pivot = arr[right - 1];
        int i = left;
        int j = right - 1;
        while (true) {
            while (arr[++i] < pivot) {}
            while (arr[--j] > pivot && j > left) {}
            if (i >= j) {
                break;
            }
            this.swap(arr, i, j);
        }
        this.swap(arr, i, right - 1);
        return i;
    }

    private void swap(double[] arr, int i, int j) {
        double temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private record SamplePoint(long timestampNanos, double fps) {
    }
}
