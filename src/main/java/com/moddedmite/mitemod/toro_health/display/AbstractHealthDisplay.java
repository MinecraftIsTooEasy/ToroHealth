package com.moddedmite.mitemod.toro_health.display;

import net.minecraft.EntityAnimal;
import net.minecraft.EntityLivingBase;
import net.minecraft.EntityMob;
import net.minecraft.EntitySlime;
import net.minecraft.EntitySquid;
import net.minecraft.EntityBat;
import net.minecraft.EntityGhast;

public abstract class AbstractHealthDisplay implements ToroHealthDisplay {

    protected EntityLivingBase entity;

    public enum Relation {
        FRIEND, FOE, UNKNOWN
    }

    protected Relation determineRelation() {
        if (entity instanceof EntityMob) {
            return Relation.FOE;
        } else if (entity instanceof EntitySlime) {
            return Relation.FOE;
        } else if (entity instanceof EntityGhast) {
            return Relation.FOE;
        } else if (entity instanceof EntityAnimal) {
            return Relation.FRIEND;
        } else if (entity instanceof EntitySquid) {
            return Relation.FRIEND;
        } else if (entity instanceof EntityBat) {
            return Relation.FRIEND;
        } else {
            return Relation.UNKNOWN;
        }
    }

    @Override
    public void setEntity(EntityLivingBase entity) {
        this.entity = entity;
    }

    public String getEntityName() {
        if (entity == null) {
            return "";
        }
        try {
            return entity.getEntityName();
        } catch (Throwable e) {
            return "";
        }
    }
}
