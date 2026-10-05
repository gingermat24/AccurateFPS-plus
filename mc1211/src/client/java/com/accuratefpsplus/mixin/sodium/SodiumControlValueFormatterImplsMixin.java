package com.accuratefpsplus.mixin.sodium;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Environment(EnvType.CLIENT)
@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls", remap = false)
public class SodiumControlValueFormatterImplsMixin {
    @ModifyConstant(method={"lambda$fpsLimit$2"}, constant={@Constant(intValue=260)})
    private static int accuratefpsplus$changeUnlimitedThreshold(int original) {
        return 1000;
    }
}
