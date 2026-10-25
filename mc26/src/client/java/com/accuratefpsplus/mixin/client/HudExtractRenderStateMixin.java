package com.accuratefpsplus.mixin.client;

import com.accuratefpsplus.AccurateFpsPlusClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class HudExtractRenderStateMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void accuratefpsplus$renderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        AccurateFpsPlusClient.renderHud(graphics);
    }
}
