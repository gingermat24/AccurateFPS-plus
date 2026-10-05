package com.accuratefpsplus.mixin.sodium;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Environment(EnvType.CLIENT)
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.SodiumConfigBuilder", remap = false)
public class SodiumConfigBuilderMixin {
    @ModifyConstant(method={"buildGeneralPage"}, constant={@Constant(intValue=260)})
    private int accuratefpsplus$changeMaxFps(int original) {
        return 1000;
    }

    @ModifyConstant(method={"buildGeneralPage"}, constant={@Constant(intValue=10, ordinal=0)})
    private int accuratefpsplus$changeMinFps(int original) {
        return 1;
    }

    @ModifyConstant(method={"buildGeneralPage"}, constant={@Constant(intValue=10, ordinal=1)})
    private int accuratefpsplus$changeStepFps(int original) {
        return 1;
    }
}
