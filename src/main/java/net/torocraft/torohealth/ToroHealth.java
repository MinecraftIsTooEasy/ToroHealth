package net.torocraft.torohealth;

import java.util.Random;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.torocraft.torohealth.config.Config;
import net.torocraft.torohealth.config.loader.ConfigLoader;
import net.torocraft.torohealth.display.Hud;
import net.torocraft.torohealth.util.RayTrace;

@Mod(ToroHealth.MODID)
public class ToroHealth {

  public static final String MODID = "torohealth";
  public static final Logger LOGGER = LogUtils.getLogger();

  public static Config CONFIG = new Config();
  public static Hud HUD = new Hud();
  public static RayTrace RAYTRACE = new RayTrace();
  public static boolean IS_HOLDING_WEAPON = false;
  public static Random RAND = new Random();

  private static ConfigLoader<Config> CONFIG_LOADER = new ConfigLoader<>(new Config(),
      ToroHealth.MODID + ".json", config -> ToroHealth.CONFIG = config);

  public ToroHealth(IEventBus modEventBus, ModContainer modContainer) {
    modEventBus.addListener(this::commonSetup);
    
    if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
      ToroHealthClient.init(modEventBus);
    }
  }

  public static net.minecraft.resources.ResourceLocation id(String path) {
    return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MODID, path);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    CONFIG_LOADER.load();
  }
  
  public static void saveConfig() {
    CONFIG_LOADER.save(CONFIG);
  }
}
