package com.accuratefpsplus;

record ManualFpsThresholds(int low, int medium, int high) {
    static ManualFpsThresholds normalize(int low, int medium, int high) {
        int normalizedLow = Math.max(1, Math.min(998, low));
        int normalizedMedium = Math.max(normalizedLow + 1, Math.min(999, medium));
        int normalizedHigh = Math.max(normalizedMedium + 1, Math.min(1000, high));
        return new ManualFpsThresholds(normalizedLow, normalizedMedium, normalizedHigh);
    }
}
