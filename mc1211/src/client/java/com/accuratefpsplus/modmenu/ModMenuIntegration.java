package com.accuratefpsplus.modmenu;

import com.accuratefpsplus.ConfigScreenRouter;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreenRouter.hasAvailableScreen()
            ? ConfigScreenRouter::createPreferred
            : ModMenuApi.super.getModConfigScreenFactory();
    }
}
