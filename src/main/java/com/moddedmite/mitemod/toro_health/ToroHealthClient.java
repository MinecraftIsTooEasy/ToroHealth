package com.moddedmite.mitemod.toro_health;

import net.minecraft.Entity;
import net.minecraft.EntityLivingBase;
import net.minecraft.Minecraft;
import com.moddedmite.mitemod.toro_health.gui.GuiEntityStatus;

import java.util.WeakHashMap;

public class ToroHealthClient {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final GuiEntityStatus entityStatusGUI;
    private final WeakHashMap<EntityLivingBase, Double> previousHealthMap = new WeakHashMap<>();

    public ToroHealthClient() {
        this.entityStatusGUI = new GuiEntityStatus();
    }

    public void displayDamageDealt(EntityLivingBase entity) {
        if (entity == null || entity.worldObj == null) return;
        if (!entity.worldObj.isRemote) return;
        if (!ToroHealthConfigs.showDamageParticles()) return;

        double currentHealth = Math.ceil(Math.max(0.0F, entity.getHealth()));
        if (Double.isNaN(currentHealth)) currentHealth = 0.0D;
        Double previousHealth = previousHealthMap.get(entity);
        if (previousHealth != null && previousHealth != currentHealth) {
            displayParticle(entity, previousHealth - currentHealth);
        }
        previousHealthMap.put(entity, currentHealth);
    }

    private void displayParticle(Entity entity, double damage) {
        if (damage == 0) return;
        double motionX = entity.worldObj.rand.nextGaussian() * 0.02;
        double motionY = 0.5f;
        double motionZ = entity.worldObj.rand.nextGaussian() * 0.02;
        DamageParticle damageIndicator = new DamageParticle(damage, entity.worldObj,
                entity.posX, entity.posY + entity.height, entity.posZ,
                motionX, motionY, motionZ);
        mc.effectRenderer.addEffect(damageIndicator);
    }

    public void setEntityInCrosshairs() {
        EntityLivingBase pointed = mc.pointedEntityLiving;
        if (pointed != null) {
            entityStatusGUI.setEntity(pointed);
        }
    }

    public void drawHealthBar() {
        entityStatusGUI.drawHealthBar();
    }
}
