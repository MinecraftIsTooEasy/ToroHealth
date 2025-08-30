package net.torocraft.torohealth.bars;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.torocraft.torohealth.ToroHealth;
import net.torocraft.torohealth.config.Config.InWorld;
import net.torocraft.torohealth.config.Config.Mode;
import net.torocraft.torohealth.util.EntityUtil;

public class HealthBarRenderer {

  private static final ResourceLocation GUI_BARS_TEXTURES = 
      ResourceLocation.fromNamespaceAndPath(ToroHealth.MODID, "textures/gui/bars.png");
  private static final int DARK_GRAY = 0x808080;
  private static final float FULL_SIZE = 40;

  private static InWorld getConfig() {
    return ToroHealth.CONFIG.inWorld;
  }

  private static final List<LivingEntity> renderedEntities = new ArrayList<>();

  public static void prepareRenderInWorld(LivingEntity entity) {
    Minecraft client = Minecraft.getInstance();

    if (!EntityUtil.showHealthBar(entity, client)) {
      return;
    }

    if (client.getCameraEntity() == null) {
      return;
    }
    Entity cameraEntity = client.getCameraEntity();
    if (entity.distanceTo(cameraEntity) > ToroHealth.CONFIG.inWorld.distance) {
      return;
    }

    BarStates.getState(entity);

    if (Mode.WHEN_HOLDING_WEAPON.equals(getConfig().mode) && !ToroHealth.IS_HOLDING_WEAPON) {
      return;
    }

    if (Mode.NONE.equals(getConfig().mode)) {
      return;
    }

    if (ToroHealth.CONFIG.inWorld.onlyWhenLookingAt && ToroHealth.HUD.getEntity() != entity) {
      return;
    }

    if (ToroHealth.CONFIG.inWorld.onlyWhenHurt && entity.getHealth() >= entity.getMaxHealth()) {
      return;
    }

    renderedEntities.add(entity);

  }

  public static void renderInWorld(float partialTick, PoseStack poseStack, Camera camera) {
    if (renderedEntities.isEmpty()) {
      return;
    }

    Minecraft minecraft = Minecraft.getInstance();
    MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
    
    Vec3 cameraPos = camera.getPosition();
    
    for (LivingEntity entity : renderedEntities) {
      if (entity == null || !entity.isAlive()) {
        continue;
      }
      
      Vec3 entityPos = entity.position().add(0, entity.getBbHeight() + 0.5, 0);
      Vec3 offset = entityPos.subtract(cameraPos);
      
      poseStack.pushPose();
      poseStack.translate(offset.x, offset.y, offset.z);
      
      // Make the health bar face the camera
      poseStack.mulPose(camera.rotation());
      
      // Render the health bar
      renderHealthBar(poseStack, vertexConsumer, entity);
      
      poseStack.popPose();
    }
    
    bufferSource.endBatch(RenderType.lines());
    renderedEntities.clear();
  }

  private static void renderHealthBar(PoseStack poseStack, VertexConsumer vertexConsumer, LivingEntity entity) {
    float health = entity.getHealth();
    float maxHealth = entity.getMaxHealth();
    float healthPercent = Mth.clamp(health / maxHealth, 0.0f, 1.0f);
    
    float barWidth = 1.0f;
    float barHeight = 0.1f;
    
    Matrix4f matrix = poseStack.last().pose();
    
    // Background bar (dark gray)
    drawQuad(matrix, vertexConsumer, -barWidth/2, -barHeight/2, barWidth, barHeight, 0.2f, 0.2f, 0.2f, 1.0f);
    
    // Health bar (colored based on health percentage)
    if (healthPercent > 0) {
      float healthWidth = barWidth * healthPercent;
      float red = healthPercent < 0.5f ? 1.0f : 2.0f * (1.0f - healthPercent);
      float green = healthPercent < 0.5f ? 2.0f * healthPercent : 1.0f;
      
      drawQuad(matrix, vertexConsumer, -barWidth/2, -barHeight/2, healthWidth, barHeight, red, green, 0.0f, 1.0f);
    }
  }

  private static void drawQuad(Matrix4f matrix, VertexConsumer vertexConsumer, float x, float y, float width, float height, 
                               float red, float green, float blue, float alpha) {
    float x2 = x + width;
    float y2 = y + height;
    
    // Bottom line
    vertexConsumer.addVertex(matrix, x, y, 0).setColor(red, green, blue, alpha);
    vertexConsumer.addVertex(matrix, x2, y, 0).setColor(red, green, blue, alpha);
    
    // Right line  
    vertexConsumer.addVertex(matrix, x2, y, 0).setColor(red, green, blue, alpha);
    vertexConsumer.addVertex(matrix, x2, y2, 0).setColor(red, green, blue, alpha);
    
    // Top line
    vertexConsumer.addVertex(matrix, x2, y2, 0).setColor(red, green, blue, alpha);
    vertexConsumer.addVertex(matrix, x, y2, 0).setColor(red, green, blue, alpha);
    
    // Left line
    vertexConsumer.addVertex(matrix, x, y2, 0).setColor(red, green, blue, alpha);
    vertexConsumer.addVertex(matrix, x, y, 0).setColor(red, green, blue, alpha);
  }

  public static void render(PoseStack poseStack, LivingEntity entity, double x, double y,
      float width, boolean inWorld) {
    
    if (!inWorld) {
      // For non-world rendering, we'd need GUI rendering which is handled elsewhere
      return;
    }

    Minecraft minecraft = Minecraft.getInstance();
    MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
    
    poseStack.pushPose();
    poseStack.translate(x, y, 0);
    renderHealthBar(poseStack, vertexConsumer, entity);
    poseStack.popPose();
    
    bufferSource.endBatch(RenderType.lines());
  }

  // public static void drawDamageNumber(PoseStack poseStack, int dmg, double x, double y,
  //     float width) {    
  // }
}
