package com.buuz135.dysoncubeproject.client;

/**
 * Client-side holder for Dyson Sphere construction progress.
 * Range: 0.0 (no panels) .. 1.0 (complete sphere).
 */
public class DysonSphereProgress {
    private static float progress = 0.0f;

    public static float get() {
        return progress;
    }

    /**
     * Sets the Dyson Sphere construction progress.
     *
     * @param value 0..1 recommended, values are clamped.
     */
    public static void set(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) return;
        progress = Math.max(0.0f, Math.min(1.0f, value));
    }
}
