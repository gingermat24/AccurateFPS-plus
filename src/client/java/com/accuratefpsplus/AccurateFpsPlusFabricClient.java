package com.accuratefpsplus;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;

public final class AccurateFpsPlusFabricClient implements ClientModInitializer {
    @Override
    @SuppressWarnings("deprecation")
    public void onInitializeClient() {
        AccurateFpsPlusClient.registerKeyCategory();
        AccurateFpsPlusClient.initialize(FabricLoader.getInstance().getConfigDir());

        KeyMapping toggleKey = AccurateFpsPlusClient.createToggleKeyMapping();
        KeyMapping presetCycleKey = AccurateFpsPlusClient.createKeyMapping("key.accuratefpsplus.cyclePreset");
        KeyMapping openConfigKey = ConfigScreenRouter.hasAvailableScreen()
            ? AccurateFpsPlusClient.createKeyMapping("key.accuratefpsplus.openConfig")
            : null;
        KeyBindingHelper.registerKeyBinding(toggleKey);
        KeyBindingHelper.registerKeyBinding(presetCycleKey);
        if (openConfigKey != null) {
            KeyBindingHelper.registerKeyBinding(openConfigKey);
        }
        AccurateFpsPlusClient.setKeyMappings(toggleKey, presetCycleKey, openConfigKey);

        ClientTickEvents.END_CLIENT_TICK.register(AccurateFpsPlusClient::onClientTick);
        HudRenderCallback.EVENT.register((graphics, tickCounter) -> AccurateFpsPlusClient.renderHud(graphics));
    }
}
