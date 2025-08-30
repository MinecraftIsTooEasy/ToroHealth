package net.torocraft.torohealth.util;

import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

// NeoForge 1.21.8: Simplified RayTrace utility without BlockGetter interface
public class RayTrace {
  private static Predicate<Entity> isVisible =
      entity -> !entity.isSpectator() && entity.isPickable();

  public LivingEntity getEntityInCrosshair(float partialTicks, double reachDistance) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.player == null) return null;
    Minecraft client = Minecraft.getInstance();
    Entity viewer = client.getCameraEntity();

    if (viewer == null) {
      return null;
    }

    Vec3 position = viewer.getEyePosition(partialTicks);
    Vec3 look = viewer.getViewVector(1.0F);
    Vec3 max = position.add(look.x * reachDistance, look.y * reachDistance, look.z * reachDistance);
    AABB searchBox =
        viewer.getBoundingBox().expandTowards(look.scale(reachDistance)).inflate(1.0D, 1.0D, 1.0D);

    EntityHitResult result = ProjectileUtil.getEntityHitResult(viewer, position, max, searchBox,
        isVisible, reachDistance * reachDistance);

    if (result == null || result.getEntity() == null) {
      return null;
    }

    if (result.getEntity() instanceof LivingEntity) {
      LivingEntity target = (LivingEntity) result.getEntity();

      if (client.player != null) {
        Player player = client.player;
        HitResult blockHit =
            clip(setupRayTraceContext(player, reachDistance, ClipContext.Fluid.NONE));

        if (!blockHit.getType().equals(BlockHitResult.Type.MISS)) {
          double blockDistance = blockHit.getLocation().distanceTo(position);
          if (blockDistance > target.distanceTo(player)) {
            return target;
          }
        } else {
          return target;
        }
      } else {
        return target;
      }
    }

    return null;
  }

  private ClipContext setupRayTraceContext(Player player, double distance,
      ClipContext.Fluid fluidHandling) {
    float pitch = player.getXRot();
    float yaw = player.getYRot();
    Vec3 fromPos = player.getEyePosition(1.0F);
    float float_3 = Mth.cos(-yaw * 0.017453292F - 3.1415927F);
    float float_4 = Mth.sin(-yaw * 0.017453292F - 3.1415927F);
    float float_5 = -Mth.cos(-pitch * 0.017453292F);
    float xComponent = float_4 * float_5;
    float yComponent = Mth.sin(-pitch * 0.017453292F);
    float zComponent = float_3 * float_5;
    Vec3 toPos = fromPos.add((double) xComponent * distance, (double) yComponent * distance,
        (double) zComponent * distance);
    return new ClipContext(fromPos, toPos, ClipContext.Block.OUTLINE, fluidHandling, player);
  }

  public BlockHitResult clip(ClipContext context) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.level != null) {
      return minecraft.level.clip(context);
    }
    //Fallback if level is null
    Vec3 to = context.getTo();
    return BlockHitResult.miss(to, Direction.DOWN, BlockPos.containing(to));
  }

}
