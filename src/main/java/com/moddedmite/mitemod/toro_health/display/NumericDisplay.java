package com.moddedmite.mitemod.toro_health.display;

import net.minecraft.EntityLivingBase;
import net.minecraft.Gui;
import net.minecraft.Minecraft;
import net.minecraft.MathHelper;
import net.minecraft.ResourceLocation;
import net.minecraft.Tessellator;
import com.moddedmite.mitemod.toro_health.ToroHealth;
import org.lwjgl.opengl.GL11;

public class NumericDisplay implements ToroHealthDisplay {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ToroHealth.MOD_ID, "textures/gui/entityStatus.png");
    private static final int WIDTH = 100;
    private static final int HEIGHT = 34;

    private final Minecraft mc;
    private final Gui gui;
    private int x = 220;
    private int y = 100;
    private EntityLivingBase entity;

    public NumericDisplay(Minecraft mc, Gui gui) {
        this.mc = mc;
        this.gui = gui;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw() {
        if (entity == null) return;

        mc.getTextureManager().bindTexture(TEXTURE);

        int bgX = 0, bgY = 0, healthBarX = 2, healthBarY = 16, nameX = 50, nameY = 4, healthX = 50, healthY = 20;

        drawModalRectWithCustomSizedTexture(x + bgX, y + bgY, 0.0f, 0.0f, WIDTH, HEIGHT, 200.0f, 200.0f);
        drawModalRectWithCustomSizedTexture(x + healthBarX, y + healthBarY, 0.0f, 150.0f, 96, 16, 200.0f, 200.0f);

        int currentHealthWidth = (int) Math.ceil(96 * (entity.getHealth() / entity.getMaxHealth()));
        drawModalRectWithCustomSizedTexture(x + healthBarX, y + healthBarY, 0.0f, 100.0f, currentHealthWidth, 16, 200.0f, 200.0f);

        String name = getEntityName();
        gui.drawCenteredString(mc.fontRenderer, name, x + nameX, y + nameY, 0xFFFFFF);
        gui.drawCenteredString(mc.fontRenderer, (int) Math.ceil(entity.getHealth()) + "/" + (int) entity.getMaxHealth(), x + healthX, y + healthY, 0xFFFFFF);
    }

    @Override
    public void setEntity(EntityLivingBase entity) {
        this.entity = entity;
    }

    public String getEntityName() {
        if (entity == null) return "";
        try {
            return entity.getEntityName();
        } catch (Throwable e) {
            return "";
        }
    }

    private void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        float uMin = u / textureWidth;
        float uMax = (u + width) / textureWidth;
        float vMin = v / textureHeight;
        float vMax = (v + height) / textureHeight;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0.0, uMin, vMax);
        tessellator.addVertexWithUV(x + width, y + height, 0.0, uMax, vMax);
        tessellator.addVertexWithUV(x + width, y, 0.0, uMax, vMin);
        tessellator.addVertexWithUV(x, y, 0.0, uMin, vMin);
        tessellator.draw();
    }
}
