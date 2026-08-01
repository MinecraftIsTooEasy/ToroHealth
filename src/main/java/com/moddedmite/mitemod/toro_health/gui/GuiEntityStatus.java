package com.moddedmite.mitemod.toro_health.gui;

import net.minecraft.EntityLivingBase;
import net.minecraft.Gui;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;
import net.minecraft.ScaledResolution;
import net.minecraft.Tessellator;
import org.lwjgl.opengl.GL11;
import com.moddedmite.mitemod.toro_health.ToroHealth;
import com.moddedmite.mitemod.toro_health.ToroHealthConfigs;
import com.moddedmite.mitemod.toro_health.display.BarDisplay;
import com.moddedmite.mitemod.toro_health.display.EntityDisplay;
import com.moddedmite.mitemod.toro_health.display.HeartsDisplay;
import com.moddedmite.mitemod.toro_health.display.NumericDisplay;
import com.moddedmite.mitemod.toro_health.display.ToroHealthDisplay;

public class GuiEntityStatus extends Gui {

    public static final ResourceLocation ICONS = new ResourceLocation("textures/gui/icons.png");

    private static final int PADDING_FROM_EDGE = 3;
    private static final ResourceLocation SKIN_BASIC = new ResourceLocation(ToroHealth.MOD_ID, "textures/gui/default_skin_basic.png");
    private static final ResourceLocation SKIN_HEAVY = new ResourceLocation(ToroHealth.MOD_ID, "textures/gui/default_skin_heavy.png");

    private final Minecraft mc;
    private final ToroHealthDisplay entityDisplay;
    private final ToroHealthDisplay heartsDisplay;
    private final ToroHealthDisplay numericDisplay;
    private final ToroHealthDisplay barDisplay;

    private EntityLivingBase entity;
    private int age = 0;
    private boolean showHealthBar = false;

    int screenX = PADDING_FROM_EDGE;
    int screenY = PADDING_FROM_EDGE;
    int displayHeight;
    int displayWidth;
    int x, y;

    public GuiEntityStatus() {
        this(Minecraft.getMinecraft());
    }

    public GuiEntityStatus(Minecraft mc) {
        this.mc = mc;
        entityDisplay = new EntityDisplay(mc);
        heartsDisplay = new HeartsDisplay(mc, this);
        numericDisplay = new NumericDisplay(mc, this);
        barDisplay = new BarDisplay(mc, this);

        entityDisplay.setPosition(50, 50);
        heartsDisplay.setPosition(25, 150);
        numericDisplay.setPosition(130, 150);
        barDisplay.setPosition(25, 200);
    }

    public void drawHealthBar() {
        if (!showHealthBar) return;

        String entityStatusDisplay = ToroHealthConfigs.entityStatusDisplay();
        if (entityStatusDisplay.equals("OFF")) return;

        updateGuiAge();
        if (!showHealthBar) return;

        updatePositions();
        drawSkin();
        draw();
    }

    private void drawSkin() {
        if (ToroHealthConfigs.skin().equals("NONE")) return;

        if (ToroHealthConfigs.skin().equals("HEAVY")) {
            mc.getTextureManager().bindTexture(SKIN_HEAVY);
        } else {
            mc.getTextureManager().bindTexture(SKIN_BASIC);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawModalRectWithCustomSizedTexture(screenX - 10, screenY - 10, 0.0f, 0.0f, 160, 60, 160, 60);
    }

    private void updateGuiAge() {
        age = age + 15;
        if (age > ToroHealthConfigs.hideDelay()) {
            hideHealthBar();
        }
    }

    private void updatePositions() {
        adjustForDisplayPositionSetting();

        x = screenX;
        y = screenY;

        if (ToroHealthConfigs.showEntityModel()) {
            entityDisplay.setPosition(x, y);
            x += 40;
        }

        if (ToroHealthConfigs.statusDisplayPosition().contains("BOTTOM")) {
            y += 6;
        }

        numericDisplay.setPosition(x, y);
        barDisplay.setPosition(x, y);
        heartsDisplay.setPosition(x, y);
    }

    private void draw() {
        if (ToroHealthConfigs.showEntityModel()) {
            entityDisplay.draw();
        }

        if ("NUMERIC".equals(ToroHealthConfigs.entityStatusDisplay())) {
            numericDisplay.draw();
        } else if ("BAR".equals(ToroHealthConfigs.entityStatusDisplay())) {
            barDisplay.draw();
        } else if ("HEARTS".equals(ToroHealthConfigs.entityStatusDisplay())) {
            heartsDisplay.draw();
        }
    }

    private void adjustForDisplayPositionSetting() {
        if (ToroHealthConfigs.showEntityModel()) {
            displayHeight = 40;
            displayWidth = 140;
        } else {
            displayHeight = 32;
            displayWidth = 100;
        }

        ScaledResolution viewport = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        String displayPosition = ToroHealthConfigs.statusDisplayPosition();

        int sh = viewport.getScaledHeight();
        int sw = viewport.getScaledWidth();

        if (displayPosition.contains("TOP") || displayPosition.equals("CUSTOM")) {
            screenY = PADDING_FROM_EDGE;
        }
        if (displayPosition.contains("BOTTOM")) {
            screenY = sh - displayHeight - PADDING_FROM_EDGE;
        }
        if (displayPosition.contains("LEFT") || displayPosition.equals("CUSTOM")) {
            screenX = PADDING_FROM_EDGE;
        }
        if (displayPosition.contains("RIGHT")) {
            screenX = sw - displayWidth - PADDING_FROM_EDGE;
        }
        if (displayPosition.contains("CENTER")) {
            screenX = (sw - displayWidth) / 2;
        }

        screenX += ToroHealthConfigs.statusDisplayX();
        screenY += ToroHealthConfigs.statusDisplayY();
    }

    private void showHealthBar() {
        showHealthBar = true;
    }

    private void hideHealthBar() {
        showHealthBar = false;
    }

    public void setEntity(EntityLivingBase entityToTrack) {
        showHealthBar();
        age = 0;
        if (entity != null && entity.entityId == entityToTrack.entityId) {
            return;
        }
        entity = entityToTrack;
        entityDisplay.setEntity(entity);
        heartsDisplay.setEntity(entity);
        numericDisplay.setEntity(entity);
        barDisplay.setEntity(entity);
    }

    private void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        float uMin = u / textureWidth;
        float uMax = (u + width) / textureWidth;
        float vMin = v / textureHeight;
        float vMax = (v + height) / textureHeight;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, this.zLevel, uMin, vMax);
        tessellator.addVertexWithUV(x + width, y + height, this.zLevel, uMax, vMax);
        tessellator.addVertexWithUV(x + width, y, this.zLevel, uMax, vMin);
        tessellator.addVertexWithUV(x, y, this.zLevel, uMin, vMin);
        tessellator.draw();
    }
}
