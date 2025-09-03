package net.torocraft.torohealth;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.torocraft.torohealth.client.ConfigScreen;
import net.torocraft.torohealth.display.Hud;
import net.torocraft.torohealth.util.RayTrace;

public class ToroHealthClient {

  public static Hud HUD = new Hud();
  public static RayTrace RAYTRACE = new RayTrace();
  public static boolean IS_HOLDING_WEAPON = false;

  public static void init(IEventBus modEventBus) {
    ClientEventHandler.init(modEventBus);
    
    // Register config screen
    ModLoadingContext.get().registerExtensionPoint(
        IConfigScreenFactory.class,
        () -> (client, parent) -> new ConfigScreen(parent)
    );
  }

}
