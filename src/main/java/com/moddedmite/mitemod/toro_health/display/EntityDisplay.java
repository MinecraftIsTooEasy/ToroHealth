package com.moddedmite.mitemod.toro_health.display;

import net.minecraft.Entity;
import net.minecraft.EntityGhast;
import net.minecraft.EntityLiving;
import net.minecraft.EntityLivingBase;
import net.minecraft.MathHelper;
import net.minecraft.Minecraft;
import net.minecraft.RenderHelper;
import net.minecraft.RenderManager;
import org.lwjgl.opengl.GL11;

public class EntityDisplay implements ToroHealthDisplay {
    private static final int RENDER_HEIGHT = 30;
    private static final int RENDER_WIDTH = 18;
    private static final int PADDING = 2;
    private static final int WIDTH = 40;
    private static final int HEIGHT = WIDTH;

    private int y;
    private float glX;
    private float glY;

    private EntityLivingBase entity;
    private Entity leashedToEntity;
    private float prevYawOffset;
    private float prevYaw;
    private float prevPitch;
    private float prevYawHead;
    private float prevPrevYawHead;
    private int scale = 1;

    public EntityDisplay(Minecraft mc) {
    }

    @Override
    public void setPosition(int x, int y) {
        this.y = y;
        glX = (float) x + WIDTH / 2;
        updateScale();
    }

    @Override
    public void setEntity(EntityLivingBase entity) {
        this.entity = entity;
        updateScale();
    }

    @Override
    public void draw() {
        if (entity == null) return;
        try {
            pushEntityLeashedTo();
            pushEntityRotations();
            glDraw();
            popEntityRotations();
            popEntityLeashedTo();
        } catch (Throwable ignore) {
        }
    }

    private void updateScale() {
        if (entity == null) {
            glY = (float) y + HEIGHT - PADDING;
            return;
        }
        int scaleY = MathHelper.ceiling_float_int(RENDER_HEIGHT / entity.height);
        int scaleX = MathHelper.ceiling_float_int(RENDER_WIDTH / entity.width);
        scale = Math.min(scaleX, scaleY);
        glY = (float) y + (HEIGHT / 2 + RENDER_HEIGHT / 2);
        if (entity instanceof EntityGhast) {
            glY -= 10;
        }
    }

    private void glDraw() {
        GL11.glEnable(2903); // GL_COLOR_MATERIAL
        GL11.glColorMaterial(1032, 5634); // GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE
        GL11.glPushMatrix();

        GL11.glTranslatef(glX, glY, 50.0F);
        GL11.glScalef((float) (-scale), (float) scale, (float) scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-100.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(0.0f, 1.0F, 0.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        GL11.glTranslatef(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = RenderManager.instance;
        rendermanager.playerViewY = 180.0F;
        rendermanager.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);

        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(32826); // GL_RESCALE_NORMAL
        GL11.glDisable(2903);  // GL_COLOR_MATERIAL
        GL11.glDisable(2896);  // GL_LIGHTING
        GL11.glDisable(3042);  // GL_BLEND
        GL11.glEnable(3008);   // GL_ALPHA_TEST
        GL11.glBlendFunc(770, 771); // GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    }

    private void pushEntityLeashedTo() {
        if (entity instanceof EntityLiving) {
            if (((EntityLiving) entity).getLeashed()) {
                leashedToEntity = ((EntityLiving) entity).getLeashedToEntity();
                ((EntityLiving) entity).setLeashedToEntity(null, false);
            }
        }
    }

    private void popEntityLeashedTo() {
        if (entity instanceof EntityLiving && leashedToEntity != null) {
            ((EntityLiving) entity).setLeashedToEntity(leashedToEntity, false);
            leashedToEntity = null;
        }
    }

    private void pushEntityRotations() {
        prevYawOffset = entity.renderYawOffset;
        prevYaw = entity.rotationYaw;
        prevPitch = entity.rotationPitch;
        prevYawHead = entity.rotationYawHead;
        prevPrevYawHead = entity.prevRotationYawHead;
        entity.renderYawOffset = 0.0f;
        entity.rotationYaw = 0.0f;
        entity.rotationPitch = 0.0f;
        entity.rotationYawHead = 0.0f;
        entity.prevRotationYawHead = 0.0f;
    }

    private void popEntityRotations() {
        entity.renderYawOffset = prevYawOffset;
        entity.rotationYaw = prevYaw;
        entity.rotationPitch = prevPitch;
        entity.rotationYawHead = prevYawHead;
        entity.prevRotationYawHead = prevPrevYawHead;
    }
}
