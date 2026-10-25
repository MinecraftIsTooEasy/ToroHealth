package com.moddedmite.mitemod.toro_health.gui;

import net.minecraft.EntityLivingBase;
import net.minecraft.Gui;
import net.minecraft.MathHelper;
import net.minecraft.Minecraft;
import net.minecraft.OpenGlHelper;
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
    private static final int PANEL_WIDTH = 160;
    private static final int PANEL_HEIGHT = 60;
    private static final int PANEL_ANCHOR_OFFSET = 10;
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
    private float panelScale = 1.0F;
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

        if (entity == null || entity.isDead) {
            hideHealthBar();
            return;
        }

        if (ToroHealthConfigs.entityStatusDisplay() == ToroHealthConfigs.EntityStatusDisplay.OFF) return;

        updateGuiAge();
        if (!showHealthBar) return;

        updatePositions();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(screenX, screenY, 0.0F);
            GL11.glScalef(panelScale, panelScale, 1.0F);
            GL11.glTranslatef(-screenX, -screenY, 0.0F);
            prepareGuiRenderState();
            drawSkin();
            draw();
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private void drawSkin() {
        if (ToroHealthConfigs.skin() == ToroHealthConfigs.Skin.NONE) return;

        if (ToroHealthConfigs.skin() == ToroHealthConfigs.Skin.HEAVY) {
            mc.getTextureManager().bindTexture(SKIN_HEAVY);
        } else {
            mc.getTextureManager().bindTexture(SKIN_BASIC);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawModalRectWithCustomSizedTexture(screenX - PANEL_ANCHOR_OFFSET, screenY - PANEL_ANCHOR_OFFSET,
                0.0f, 0.0f, PANEL_WIDTH, PANEL_HEIGHT, PANEL_WIDTH, PANEL_HEIGHT);
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

        if (ToroHealthConfigs.statusDisplayPosition().isBottom()) {
            y += 6;
        }

        numericDisplay.setPosition(x, y);
        barDisplay.setPosition(x, y);
        heartsDisplay.setPosition(x, y);
    }

    private void draw() {
        if (ToroHealthConfigs.showEntityModel()) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            entityDisplay.draw();
            prepareGuiRenderState();
        }

        if (ToroHealthConfigs.entityStatusDisplay() == ToroHealthConfigs.EntityStatusDisplay.NUMERIC) {
            numericDisplay.draw();
        } else if (ToroHealthConfigs.entityStatusDisplay() == ToroHealthConfigs.EntityStatusDisplay.BAR) {
            barDisplay.draw();
        } else if (ToroHealthConfigs.entityStatusDisplay() == ToroHealthConfigs.EntityStatusDisplay.HEARTS) {
            heartsDisplay.draw();
        }
    }

    private void prepareGuiRenderState() {
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        if (OpenGlHelper.lightmapTexUnit != OpenGlHelper.defaultTexUnit) {
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void adjustForDisplayPositionSetting() {
        ScaledResolution viewport = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        ToroHealthConfigs.StatusDisplayPosition displayPosition = ToroHealthConfigs.statusDisplayPosition();

        int sh = viewport.getScaledHeight();
        int sw = viewport.getScaledWidth();
        panelScale = ToroHealthConfigs.statusDisplayScale() / 100.0F;

        int leftInset = MathHelper.ceiling_float_int(PANEL_ANCHOR_OFFSET * panelScale);
        int rightExtent = MathHelper.ceiling_float_int((PANEL_WIDTH - PANEL_ANCHOR_OFFSET) * panelScale);
        int topInset = MathHelper.ceiling_float_int(PANEL_ANCHOR_OFFSET * panelScale);
        int bottomExtent = MathHelper.ceiling_float_int((PANEL_HEIGHT - PANEL_ANCHOR_OFFSET) * panelScale);

        screenX = PADDING_FROM_EDGE + leftInset;
        screenY = PADDING_FROM_EDGE + topInset;
        if (displayPosition.isRight()) {
            screenX = sw - PADDING_FROM_EDGE - rightExtent;
        } else if (displayPosition.isCenter()) {
            screenX = Math.round((sw - (PANEL_WIDTH - PANEL_ANCHOR_OFFSET * 2) * panelScale) / 2.0F);
        }
        if (displayPosition.isBottom()) {
            screenY = sh - PADDING_FROM_EDGE - bottomExtent;
        }

        screenX += ToroHealthConfigs.statusDisplayX();
        screenY += ToroHealthConfigs.statusDisplayY();
        screenX = clampPanelPosition(screenX, sw, leftInset, rightExtent);
        screenY = clampPanelPosition(screenY, sh, topInset, bottomExtent);
    }

    private int clampPanelPosition(int position, int viewportSize, int leadingExtent, int trailingExtent) {
        int min = PADDING_FROM_EDGE + leadingExtent;
        int max = viewportSize - PADDING_FROM_EDGE - trailingExtent;
        if (max < min) return min;
        return Math.max(min, Math.min(position, max));
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
        if (entity == entityToTrack) {
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
