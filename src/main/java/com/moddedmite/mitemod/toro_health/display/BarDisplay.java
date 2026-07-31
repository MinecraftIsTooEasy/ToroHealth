package com.moddedmite.mitemod.toro_health.display;

import net.minecraft.Gui;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;
import net.minecraft.Tessellator;
import org.lwjgl.opengl.GL11;
import com.moddedmite.mitemod.toro_health.ToroHealth;

public class BarDisplay extends AbstractHealthDisplay implements ToroHealthDisplay {

    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation(ToroHealth.MOD_ID, "textures/gui/bars.png");
    private static final int BAR_WIDTH = 92;

    private final Minecraft mc;
    private final Gui gui;
    private int y;
    private int barX;
    private int barY;

    public BarDisplay(Minecraft mc, Gui gui) {
        this.mc = mc;
        this.gui = gui;
    }

    @Override
    public void setPosition(int x, int y) {
        this.y = y;
        barX = x + 4;
        barY = y + 12;
    }

    @Override
    public void draw() {
        if (entity == null) return;
        renderBossHealth();
    }

    public void renderBossHealth() {
        String name = getEntityName();
        String health = (int) Math.ceil(entity.getHealth()) + "/" + (int) entity.getMaxHealth();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
        renderHealthBar();
        mc.fontRenderer.drawStringWithShadow(name, barX, y + 2, 0xFFFFFF);
        mc.fontRenderer.drawStringWithShadow(health, barX, y + 20, 0xFFFFFF);
    }

    public enum Color {
        PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE
    }

    private void renderHealthBar() {
        Color color = determineColor();
        float percent = entity.getHealth() / entity.getMaxHealth();
        drawTexturedModalRectCustom(barX, barY, 0, color.ordinal() * 5 * 2, BAR_WIDTH, 5, 256, 256);
        int healthWidth = (int) (percent * BAR_WIDTH);
        if (healthWidth > 0) {
            drawTexturedModalRectCustom(barX, barY, 0, color.ordinal() * 5 * 2 + 5, healthWidth, 5, 256, 256);
        }
    }

    private Color determineColor() {
        switch (determineRelation()) {
            case FOE: return Color.RED;
            case FRIEND: return Color.GREEN;
            default: return Color.WHITE;
        }
    }

    private void drawTexturedModalRectCustom(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0.0, u * f, (v + height) * f1);
        tessellator.addVertexWithUV(x + width, y + height, 0.0, (u + width) * f, (v + height) * f1);
        tessellator.addVertexWithUV(x + width, y, 0.0, (u + width) * f, v * f1);
        tessellator.addVertexWithUV(x, y, 0.0, u * f, v * f1);
        tessellator.draw();
    }
}
