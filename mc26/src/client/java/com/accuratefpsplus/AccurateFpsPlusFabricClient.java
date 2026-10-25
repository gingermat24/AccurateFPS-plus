package com.accuratefpsplus;

import java.util.Arrays;
import com.accuratefpsplus.mixin.client.OptionsAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.resources.Identifier;

public final class AccurateFpsPlusFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AccurateFpsPlusClient.initialize(FabricLoader.getInstance().getConfigDir());
        AccurateFpsPlusClient.setKeyCategory(KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("accuratefpsplus", "general")
        ));

        KeyMapping toggleKey = AccurateFpsPlusClient.createToggleKeyMapping();
        KeyMapping presetCycleKey = AccurateFpsPlusClient.createKeyMapping("key.accuratefpsplus.cyclePreset");
        KeyMapping openConfigKey = ConfigScreenRouter.hasAvailableScreen()
            ? AccurateFpsPlusClient.createKeyMapping("key.accuratefpsplus.openConfig")
            : null;
        AccurateFpsPlusClient.setKeyMappings(toggleKey, presetCycleKey, openConfigKey);
        AccurateFpsPlusClient.setKeyMappingRegistrar(AccurateFpsPlusFabricClient::registerKeyMappings);
    }

    private static void registerKeyMappings(Minecraft client) {
        Options options = client.options;
        KeyMapping[] mappings = {
            AccurateFpsPlusClient.getToggleHudKeyMapping(),
            AccurateFpsPlusClient.getPresetCycleKeyMapping(),
            AccurateFpsPlusClient.getOpenConfigKeyMapping()
        };
        KeyMapping[] registered = options.keyMappings;
        int count = 0;
        for (KeyMapping mapping : mappings) {
            if (mapping != null) {
                count++;
            }
        }
        KeyMapping[] updated = Arrays.copyOf(registered, registered.length + count);
        int nextIndex = registered.length;
        for (KeyMapping mapping : mappings) {
            if (mapping != null) {
                updated[nextIndex++] = mapping;
            }
        }
        ((OptionsAccessor)(Object)options).accuratefpsplus$setKeyMappings(updated);
    }
}
