
package net.torocraft.torohealth;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.torocraft.torohealth.bars.BarStates;
import net.torocraft.torohealth.bars.HealthBarRenderer;
import net.torocraft.torohealth.bars.ParticleRenderer;
import net.torocraft.torohealth.util.HoldingWeaponUpdater;

public class ClientEventHandler {

  public static void init(IEventBus modEventBus) {
    NeoForge.EVENT_BUS.addListener(ClientEventHandler::playerTick);
    NeoForge.EVENT_BUS.addListener(ClientEventHandler::entityRender);
    NeoForge.EVENT_BUS.addListener(ClientEventHandler::renderParticles);
    modEventBus.addListener(ClientEventHandler::registerLayers);
  }

  @SubscribeEvent
  private static void registerLayers(final RegisterGuiLayersEvent event) {
    event.registerAbove(VanillaGuiLayers.EFFECTS, ToroHealth.id("torohealth_hud"), 
        (guiGraphics, deltaTracker) -> {
          if (Minecraft.getInstance().player != null) {
            int width = guiGraphics.guiWidth();
            int height = guiGraphics.guiHeight();
            ToroHealthClient.HUD.draw(guiGraphics, deltaTracker.getRealtimeDeltaTicks(), width, height);
          }
        });
  }

  private static void entityRender(RenderLivingEvent.Post<?, ?, ?> event) {
    try {
      if (event.getRenderer() != null) {
      }
    } catch (Exception e) {
    }
  }

  private static void renderParticles(RenderLevelStageEvent.AfterParticles event) {
    Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
    ParticleRenderer.renderParticles(event.getPoseStack(), camera);
    HealthBarRenderer.renderInWorld(event.getPartialTick().getRealtimeDeltaTicks(), event.getPoseStack(), camera);
  }

  private static void playerTick(PlayerTickEvent.Pre event) {
    if (!event.getEntity().level().isClientSide) {
      return;
    }
    
    if (event.getEntity().level() != null) {
      var nearbyEntities = event.getEntity().level().getEntitiesOfClass(
          net.minecraft.world.entity.LivingEntity.class,
          event.getEntity().getBoundingBox().inflate(ToroHealth.CONFIG.particle.distance)
      );
      
      for (var entity : nearbyEntities) {
        if (entity != event.getEntity()) { 
          BarStates.getState(entity); 
        }
      }
    }
    
    ToroHealthClient.HUD.setEntity(
        ToroHealthClient.RAYTRACE.getEntityInCrosshair(0, ToroHealth.CONFIG.hud.distance));
    BarStates.tick();
    HoldingWeaponUpdater.update();
    ToroHealthClient.HUD.tick();
  }
}
