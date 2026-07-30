package com.moddedmite.mitemod.toro_health;

import moddedmite.rustedironcore.api.event.Handlers;
import net.fabricmc.api.ModInitializer;
import net.xiaoyu233.fml.ModResourceManager;

public class ToroHealth implements ModInitializer {
    public static final String MOD_ID = "toro_health";
    public static final String MOD_NAME = "ToroHealth";
    public static final String VERSION = "1.0.0";

    private static ToroHealthClient clientInstance;

    @Override
    public void onInitialize() {
        ModResourceManager.addResourcePackDomain(MOD_ID);

        ConfigurationHandler.init();

        Handlers.Tick.register(new ToroHealthTickListener());
    }

    public static ToroHealthClient getClientInstance() {
        if (clientInstance == null) {
            clientInstance = new ToroHealthClient();
        }
        return clientInstance;
    }
}
