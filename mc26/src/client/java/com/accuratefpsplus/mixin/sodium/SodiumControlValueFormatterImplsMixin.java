package com.accuratefpsplus.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatterImpls", remap = false)
public class SodiumControlValueFormatterImplsMixin {
    @ModifyConstant(method={"lambda$fpsLimit$0"}, constant={@Constant(intValue=260)})
    private static int accuratefpsplus$changeUnlimitedThreshold(int original) {
        return 1001;
    }
}
