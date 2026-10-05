package com.accuratefpsplus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum ThresholdMode {
    AUTOMATIC,
    MANUAL
}
