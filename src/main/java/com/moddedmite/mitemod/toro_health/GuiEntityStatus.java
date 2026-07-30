package com.moddedmite.mitemod.toro_health;

import net.minecraft.Entity;
import net.minecraft.EntityLiving;
import net.minecraft.EntityLivingBase;
import net.minecraft.Gui;
import net.minecraft.ItemArmor;
import net.minecraft.ItemStack;
import net.minecraft.MathHelper;
import net.minecraft.Minecraft;
import net.minecraft.RenderHelper;
import net.minecraft.RenderManager;
import net.minecraft.ResourceLocation;
import net.minecraft.ScaledResolution;
import net.minecraft.Tessellator;
import org.lwjgl.opengl.GL11;

public class GuiEntityStatus extends Gui {

    private static final ResourceLocation ENTITY_STATUS_TEXTURE = new ResourceLocation(ToroHealth.MOD_ID, "textures/gui/entityStatus.png");

    private final Minecraft mc;
    private EntityLivingBase entity;
    private int age = 0;
    private boolean showHealthBar = false;

    private ScaledResolution viewport;
    private static final int PADDING_FROM_EDGE = 2;

    private String displayPosition;
    private int screenX = PADDING_FROM_EDGE;
    private int screenY = PADDING_FROM_EDGE;
    private int displayHeight;
    private int displayWidth;

    private int entityRenderWidth;
    private static final int ENTITY_RENDER_HEIGHT_UNIT = 20;
    private static final int ENTITY_RENDER_X = 20;
    private Entity leashedToEntity;

    private int entityHealth = 0;
    private int lastEntityHealth = 0;
    private long lastSystemTime = 0L;
    private int updateCounter;
    private long healthUpdateCounter = 0L;

    public GuiEntityStatus() {
        this.mc = Minecraft.getMinecraft();
    }

    public void setEntity(EntityLivingBase entityToTrack) {
        showHealthBar = true;
        age = 0;
        if (entity != null && entity.entityId == entityToTrack.entityId) {
            return;
        }
        entity = entityToTrack;
    }

    public void drawHealthBar() {
        if (!showHealthBar || entity == null || entity.isDead) {
            return;
        }

        String entityStatusDisplay = ConfigurationHandler.entityStatusDisplay;
        age++;
        if (age > ConfigurationHandler.hideDelay || entityStatusDisplay.equals("OFF")) {
            showHealthBar = false;
            return;
        }

        if (mc.theWorld == null || mc.fontRenderer == null) {
            return;
        }

        boolean showEntityModel = ConfigurationHandler.showEntityModel;
        if (showEntityModel) {
            entityRenderWidth = 40;
        } else {
            entityRenderWidth = 0;
        }

        viewport = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        displayPosition = ConfigurationHandler.statusDisplayPosition;

        if (isUnsupportedDisplayType(entityStatusDisplay)) {
            entityStatusDisplay = "HEARTS";
        }

        if (entityStatusDisplay.equals("NUMERIC")) {
            drawNumericDisplayStyle();
        } else if (entityStatusDisplay.equals("HEARTS")) {
            drawHeartsDisplay();
        }

        if (showEntityModel) {
            try {
                drawEntityOnScreen();
            } catch (Throwable ignore) {
            }
        }
    }

    private void drawEntityOnScreen() {
        int sw = viewport.getScaledWidth();
        int sh = viewport.getScaledHeight();

        int entityRenderHeight = 20;

        double h = entityRenderHeight / (double) entity.height;

        if (displayPosition.contains("TOP")) {
            screenY = entityRenderHeight + 5;
        }
        if (displayPosition.contains("BOTTOM")) {
            screenY = sh - displayHeight + entityRenderHeight;
        }
        if (displayPosition.contains("LEFT")) {
            screenX = ENTITY_RENDER_X;
        }
        if (displayPosition.contains("RIGHT")) {
            screenX = sw - entityRenderWidth + 10;
        }
        if (displayPosition.contains("CENTER")) {
            screenX = (sw - entityRenderWidth - displayWidth) / 2;
        }
        if (displayPosition.equals("CUSTOM")) {
            screenX = ConfigurationHandler.statusDisplayX + (entityRenderWidth / 2);
            screenY = ConfigurationHandler.statusDisplayY + entityRenderHeight + 10;
        }

        int scale = MathHelper.ceiling_double_int(h);

        if (entity instanceof EntityLiving) {
            if (((EntityLiving) entity).getLeashed()) {
                leashedToEntity = ((EntityLiving) entity).getLeashedToEntity();
                ((EntityLiving) entity).setLeashedToEntity(null, false);
            }
        }

        float prevYawOffset = entity.renderYawOffset;
        float prevYaw = entity.rotationYaw;
        float prevPitch = entity.rotationPitch;
        float prevYawHead = entity.rotationYawHead;
        float prevPrevYawHead = entity.prevRotationYawHead;

        GL11.glEnable(2903); // GL_COLOR_MATERIAL
        GL11.glColorMaterial(1032, 5634); // GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE
        GL11.glPushMatrix();
        GL11.glTranslatef((float) screenX, (float) screenY, 50.0F);
        GL11.glScalef((float) (-scale), (float) scale, (float) scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-100.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(0.0f, 1.0F, 0.0F, 0.0F);
        entity.renderYawOffset = 0.0f;
        entity.rotationYaw = 0.0f;
        entity.rotationPitch = 0.0f;
        entity.rotationYawHead = 0.0f;
        entity.prevRotationYawHead = 0.0f;
        GL11.glTranslatef(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = RenderManager.instance;
        rendermanager.playerViewY = 180.0F;
        rendermanager.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        entity.renderYawOffset = prevYawOffset;
        entity.rotationYaw = prevYaw;
        entity.rotationPitch = prevPitch;
        entity.rotationYawHead = prevYawHead;
        entity.prevRotationYawHead = prevPrevYawHead;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(32826); // GL_RESCALE_NORMAL

        if (entity instanceof EntityLiving && leashedToEntity != null) {
            ((EntityLiving) entity).setLeashedToEntity(leashedToEntity, false);
            leashedToEntity = null;
        }
    }

    private boolean isUnsupportedDisplayType(String entityStatusDisplay) {
        return !entityStatusDisplay.equals("HEARTS") && !entityStatusDisplay.equals("NUMERIC");
    }

    private void drawNumericDisplayStyle() {
        mc.getTextureManager().bindTexture(ENTITY_STATUS_TEXTURE);

        int bgX = 0, bgY = 0, healthBarX = 2, healthBarY = 16, nameX = 50, nameY = 4, healthX = 50, healthY = 20;

        displayWidth = 100;
        displayHeight = 34;

        adjustForDisplayPositionSetting();

        drawModalRectWithCustomSizedTexture(screenX + bgX, screenY + bgY, 0.0f, 0.0f, displayWidth, displayHeight, 200.0f, 200.0f);
        drawModalRectWithCustomSizedTexture(screenX + healthBarX, screenY + healthBarY, 0.0f, 150.0f, 96, 16, 200.0f, 200.0f);

        int currentHealthWidth = (int) Math.ceil(96 * (entity.getHealth() / entity.getMaxHealth()));
        drawModalRectWithCustomSizedTexture(screenX + healthBarX, screenY + healthBarY, 0.0f, 100.0f, currentHealthWidth, 16, 200.0f, 200.0f);

        String name = getDisplayName();

        drawCenteredString(mc.fontRenderer, name, screenX + nameX, screenY + nameY, 0xFFFFFF);
        drawCenteredString(mc.fontRenderer, (int) Math.ceil(entity.getHealth()) + "/" + (int) entity.getMaxHealth(), screenX + healthX, screenY + healthY, 0xFFFFFF);
    }

    private void drawHeartsDisplay() {
        screenX = PADDING_FROM_EDGE;
        screenY = PADDING_FROM_EDGE;
        displayHeight = 74;
        displayWidth = 84;

        adjustForDisplayPositionSetting();

        drawName();
        drawHearts();
        drawArmor();
    }

    private void drawName() {
        String name = getDisplayName();
        drawString(mc.fontRenderer, name, screenX, screenY, 0xFFFFFF);
        screenY += 10;
    }

    private int drawHearts() {
        mc.getTextureManager().bindTexture(Gui.icons);
        int currentHealth = MathHelper.ceiling_float_int(entity.getHealth());
        entityHealth = currentHealth;
        int absorptionAmount = 0; // MITE doesn't have absorption in the same way

        float maxHealth = entity.getMaxHealth();

        int numRowsOfHearts = MathHelper.ceiling_float_int(maxHealth / 2.0F / 10.0F);
        int j2 = Math.max(10 - (numRowsOfHearts - 2), 3);

        int hardcoreModeOffset = 0;
        if (entity.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
            hardcoreModeOffset = 5;
        }

        int flashingHeartOffset = 0;

        for (int currentHeartBeingDrawn = MathHelper.ceiling_float_int(maxHealth / 2.0F) - 1; currentHeartBeingDrawn >= 0; --currentHeartBeingDrawn) {
            int texturePosX = 16;

            int rowsOfHearts = MathHelper.ceiling_float_int((float) (currentHeartBeingDrawn + 1) / 10.0F) - 1;
            int heartToDrawX = screenX + currentHeartBeingDrawn % 10 * 8;
            int heartToDrawY = screenY + rowsOfHearts * j2;

            this.drawTexturedModalRect(heartToDrawX, heartToDrawY, 16 + flashingHeartOffset * 9, 9 * hardcoreModeOffset, 9, 9);

            if (currentHeartBeingDrawn * 2 + 1 < currentHealth) {
                this.drawTexturedModalRect(heartToDrawX, heartToDrawY, texturePosX + 36, 9 * hardcoreModeOffset, 9, 9);
            }

            if (currentHeartBeingDrawn * 2 + 1 == currentHealth) {
                this.drawTexturedModalRect(heartToDrawX, heartToDrawY, texturePosX + 45, 9 * hardcoreModeOffset, 9, 9);
            }
        }

        screenY += (numRowsOfHearts - 1) * j2 + 10;

        return absorptionAmount;
    }

    private void drawArmor() {
        mc.getTextureManager().bindTexture(Gui.icons);

        int armor = getArmorValue(entity);

        for (int i = 0; i < 10; ++i) {
            if (armor > 0) {
                int armorIconX = screenX + i * 8;

                if (i * 2 + 1 < armor) {
                    this.drawTexturedModalRect(armorIconX, screenY, 34, 9, 9, 9);
                }

                if (i * 2 + 1 == armor) {
                    this.drawTexturedModalRect(armorIconX, screenY, 25, 9, 9, 9);
                }

                if (i * 2 + 1 > armor) {
                    this.drawTexturedModalRect(armorIconX, screenY, 16, 9, 9, 9);
                }
            }
        }

        screenY += 10;
    }

    private int getArmorValue(EntityLivingBase entity) {
        int totalArmor = 0;
        ItemStack[] wornItems = entity.getWornItems();
        if (wornItems != null) {
            for (ItemStack stack : wornItems) {
                if (stack != null && stack.getItem() instanceof ItemArmor) {
                    totalArmor += 2;
                }
            }
        }
        return totalArmor;
    }

    private void adjustForDisplayPositionSetting() {
        int sh = viewport.getScaledHeight();
        int sw = viewport.getScaledWidth();

        if (displayPosition.equals("CUSTOM")) {
            screenX = ConfigurationHandler.statusDisplayX + entityRenderWidth;
            screenY = ConfigurationHandler.statusDisplayY;
            return;
        }

        if (displayPosition.contains("TOP")) {
            screenY = PADDING_FROM_EDGE;
        }
        if (displayPosition.contains("BOTTOM")) {
            screenY = sh - displayHeight - PADDING_FROM_EDGE;
        }
        if (displayPosition.contains("LEFT")) {
            screenX = entityRenderWidth + PADDING_FROM_EDGE;
        }
        if (displayPosition.contains("RIGHT")) {
            screenX = sw - displayWidth - PADDING_FROM_EDGE - entityRenderWidth - 10;
        }
        if (displayPosition.contains("CENTER")) {
            screenX = (sw - displayWidth) / 2;
        }
    }

    private String getDisplayName() {
        return entity.getEntityName();
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
