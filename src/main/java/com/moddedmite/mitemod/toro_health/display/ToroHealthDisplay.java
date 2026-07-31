package com.moddedmite.mitemod.toro_health.display;

import net.minecraft.EntityLivingBase;

public interface ToroHealthDisplay {
    void setEntity(EntityLivingBase entity);
    void setPosition(int x, int y);
    void draw();
}
