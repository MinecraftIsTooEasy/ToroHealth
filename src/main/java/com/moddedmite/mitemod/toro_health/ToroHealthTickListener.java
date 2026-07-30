package com.moddedmite.mitemod.toro_health;

import moddedmite.rustedironcore.api.event.listener.ITickListener;
import net.minecraft.Entity;
import net.minecraft.EntityLivingBase;
import net.minecraft.EntityPlayer;
import net.minecraft.Minecraft;
import net.minecraft.server.MinecraftServer;

public class ToroHealthTickListener implements ITickListener {

    @Override
    public void onEntityPlayerTick(EntityPlayer player) {
    }

    @Override
    public void onClientTick(Minecraft client) {
    }

    @Override
    public void onServerTick(MinecraftServer server) {
    }

    @Override
    public void onRenderTick(float partialTick) {
        if (Minecraft.getMinecraft() == null) return;
        if (Minecraft.getMinecraft().theWorld == null) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;

        ToroHealthClient client = ToroHealth.getClientInstance();
        client.setEntityInCrosshairs();
        client.drawHealthBar();
    }

    @Override
    public void onEntityTick(Entity entity) {
        if (entity == null || entity.worldObj == null) return;
        if (!entity.worldObj.isRemote) return;
        if (entity instanceof EntityLivingBase) {
            ToroHealth.getClientInstance().displayDamageDealt((EntityLivingBase) entity);
        }
    }
}
