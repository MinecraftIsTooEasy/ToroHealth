package com.moddedmite.mitemod.toro_health.compat;

import com.moddedmite.mitemod.toro_health.ToroHealthConfigs;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ToroHealthConfigs.getInstance().getConfigScreen(parent);
    }
}
