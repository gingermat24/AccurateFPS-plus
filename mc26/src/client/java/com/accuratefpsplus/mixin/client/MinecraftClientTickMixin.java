package com.accuratefpsplus.mixin.client;

import com.accuratefpsplus.AccurateFpsPlusClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientTickMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void accuratefpsplus$onClientTick(CallbackInfo ci) {
        Minecraft client = (Minecraft)(Object)this;
        AccurateFpsPlusClient.registerKeyMappings(client);
        AccurateFpsPlusClient.onClientTick(client);
    }
}
