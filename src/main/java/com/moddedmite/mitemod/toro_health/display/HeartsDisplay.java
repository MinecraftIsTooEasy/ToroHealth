package com.moddedmite.mitemod.toro_health.display;

import net.minecraft.DamageSource;
import net.minecraft.EntityLivingBase;
import net.minecraft.Gui;
import net.minecraft.Minecraft;
import net.minecraft.MathHelper;
import com.moddedmite.mitemod.toro_health.ToroHealthConfigs;
import com.moddedmite.mitemod.toro_health.gui.GuiEntityStatus;

public class HeartsDisplay extends AbstractHealthDisplay implements ToroHealthDisplay {
    private static final int MAX_DISPLAYED_HEARTS = 10;
    private static final int MAX_ARMOR_ICONS = 10;
    private static final int MAX_ARMOR_DISPLAY_WIDTH = 92;

    private final Minecraft mc;
    private final Gui gui;
    private int x, originX = 100;
    private int y, originY = 100;

    public HeartsDisplay(Minecraft mc, Gui gui) {
        this.mc = mc;
        this.gui = gui;
    }

    @Override
    public void setPosition(int x, int y) {
        originX = x;
        originY = y;
        resetToOrigin();
    }

    @Override
    public void draw() {
        if (entity == null) return;

        resetToOrigin();
        x += 4;
        y += 2;

        drawName();
        drawHearts();
        drawArmor();
    }

    private void resetToOrigin() {
        x = originX;
        y = originY;
    }

    private void drawName() {
        int maxNameWidth = ToroHealthConfigs.showEntityModel() ? 100 : 140;
        String name = ToroHealthFormatting.fitText(mc.fontRenderer, getEntityName(), maxNameWidth);
        gui.drawString(mc.fontRenderer, name, x, y, 0xFFFFFF);
        y += 10;
    }

    private void drawHearts() {
        mc.getTextureManager().bindTexture(GuiEntityStatus.ICONS);
        float maxHealth = entity.getMaxHealth();
        if (!(maxHealth > 0.0F)) return;

        float maxHearts = maxHealth / 2.0F;
        int totalHearts = maxHearts >= MAX_DISPLAYED_HEARTS
                ? MAX_DISPLAYED_HEARTS
                : MathHelper.ceiling_float_int(maxHearts);
        if (totalHearts <= 0) return;

        int numRowsOfHearts = MathHelper.ceiling_float_int(totalHearts / 10.0F);
        int rowSpacing = Math.max(10 - (numRowsOfHearts - 2), 3);
        boolean showHealthValue = !Float.isFinite(maxHealth) || maxHearts > MAX_DISPLAYED_HEARTS;
        float displayedHealth = showHealthValue
                ? ToroHealthFormatting.healthRatio(entity.getHealth(), maxHealth) * totalHearts
                : entity.getHealth() / 2.0F;
        int foeOffset = determineRelation() == Relation.FOE ? 54
                : determineRelation() == Relation.UNKNOWN ? 18 : 0;
        int hardcoreModeOffset = entity.worldObj.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0;

        for (int heartIndex = totalHearts - 1; heartIndex >= 0; --heartIndex) {
            int row = MathHelper.ceiling_float_int((float) (heartIndex + 1) / 10.0F) - 1;
            int heartX = x + heartIndex % 10 * 8;
            int heartY = y + row * rowSpacing;

            gui.drawTexturedModalRect(heartX, heartY, 16, 9 * hardcoreModeOffset, 9, 9);

            if (displayedHealth >= heartIndex + 1) {
                gui.drawTexturedModalRect(heartX, heartY, 16 + foeOffset + 36, 9 * hardcoreModeOffset, 9, 9);
            } else if (displayedHealth > heartIndex) {
                gui.drawTexturedModalRect(heartX, heartY, 16 + foeOffset + 45, 9 * hardcoreModeOffset, 9, 9);
            }
        }

        y += (numRowsOfHearts - 1) * rowSpacing + 10;
        if (showHealthValue) {
            mc.fontRenderer.drawStringWithShadow(
                    ToroHealthFormatting.formatHealthPair(entity.getHealth(), maxHealth), x, y, 0xFFFFFF);
            y += 10;
        }
    }

    private void drawArmor() {
        mc.getTextureManager().bindTexture(GuiEntityStatus.ICONS);
        float armorValue = getArmorValue(entity);
        if (!(armorValue > 0.0F)) return;

        float armor = armorValue / 2.0F;
        int visibleIcons = armor >= MAX_ARMOR_ICONS
                ? MAX_ARMOR_ICONS
                : MathHelper.ceiling_float_int(armor);
        String armorLabel = armor > MAX_ARMOR_ICONS ? ToroHealthFormatting.formatAmount(armorValue) : null;
        if (armorLabel != null) {
            int labelWidth = mc.fontRenderer.getStringWidth(armorLabel);
            visibleIcons = Math.min(visibleIcons,
                    Math.max(0, (MAX_ARMOR_DISPLAY_WIDTH - labelWidth - 2) / 8));
        }

        for (int i = 0; i < visibleIcons; ++i) {
            int armorIconX = x + i * 8;
            float upper = i + 1;
            if (armor >= upper) {
                gui.drawTexturedModalRect(armorIconX, y, 34, 9, 9, 9);
            } else {
                gui.drawTexturedModalRect(armorIconX, y, 25, 9, 9, 9);
            }
        }

        if (armorLabel != null) {
            int labelX = x + visibleIcons * 8 + 2;
            mc.fontRenderer.drawStringWithShadow(armorLabel, labelX, y, 0xFFFFFF);
        }
        y += 10;
    }

    private float getArmorValue(EntityLivingBase entity) {
        return entity.getTotalProtection(DamageSource.causeMobDamage((EntityLivingBase) null));
    }
}
