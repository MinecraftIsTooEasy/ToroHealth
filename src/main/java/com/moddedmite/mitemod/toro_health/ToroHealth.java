package com.moddedmite.mitemod.toro_health;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import moddedmite.rustedironcore.api.event.Handlers;
import net.fabricmc.api.ModInitializer;
import net.xiaoyu233.fml.ModResourceManager;

public class ToroHealth implements ModInitializer {
    public static final String MOD_ID = "toro_health";

    private static ToroHealthClient clientInstance;

    @Override
    public void onInitialize() {
        ModResourceManager.addResourcePackDomain(MOD_ID);
        Handlers.Tick.register(new ToroHealthTickListener());

        InitializationHandler.getInstance().registerInitializationHandler(new IInitializationHandler() {
            @Override
            public void registerModHandlers() {
                ConfigManager.getInstance().registerConfig(ToroHealthConfigs.getInstance());
            }
        });
    }

    public static ToroHealthClient getClientInstance() {
        if (clientInstance == null) {
            clientInstance = new ToroHealthClient();
        }
        return clientInstance;
    }
}
