package com.moddedmite.mitemod.toro_health.display;

import net.minecraft.DamageSource;
import net.minecraft.EntityLivingBase;
import net.minecraft.Gui;
import net.minecraft.Minecraft;
import net.minecraft.MathHelper;
import com.moddedmite.mitemod.toro_health.gui.GuiEntityStatus;

public class HeartsDisplay extends AbstractHealthDisplay implements ToroHealthDisplay {
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
        gui.drawString(mc.fontRenderer, getEntityName(), x, y, 0xFFFFFF);
        y += 10;
    }

    private void drawHearts() {
        mc.getTextureManager().bindTexture(GuiEntityStatus.ICONS);
        int currentHealth = MathHelper.ceiling_float_int(entity.getHealth());

        float maxHealth = entity.getMaxHealth();
        int numRowsOfHearts = MathHelper.ceiling_float_int(maxHealth / 2.0F / 10.0F);
        int j2 = Math.max(10 - (numRowsOfHearts - 2), 3);

        for (int currentHeartBeingDrawn = MathHelper.ceiling_float_int(maxHealth / 2.0F) - 1; currentHeartBeingDrawn >= 0; --currentHeartBeingDrawn) {
            int texturePosX = 16;
            int flashingHeartOffset = 0;
            int foeOffset = 0;

            if (determineRelation().equals(Relation.FOE)) {
                foeOffset = 54;
            } else if (determineRelation().equals(Relation.UNKNOWN)) {
                foeOffset = 18;
            }

            int rowsOfHearts = MathHelper.ceiling_float_int((float) (currentHeartBeingDrawn + 1) / 10.0F) - 1;
            int heartToDrawX = x + currentHeartBeingDrawn % 10 * 8;
            int heartToDrawY = y + rowsOfHearts * j2;

            int hardcoreModeOffset = 0;
            if (entity.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
                hardcoreModeOffset = 5;
            }

            gui.drawTexturedModalRect(heartToDrawX, heartToDrawY, 16 + flashingHeartOffset * 9, 9 * hardcoreModeOffset, 9, 9);

            if (currentHeartBeingDrawn * 2 + 1 < currentHealth) {
                gui.drawTexturedModalRect(heartToDrawX, heartToDrawY, texturePosX + foeOffset + 36, 9 * hardcoreModeOffset, 9, 9);
            }

            if (currentHeartBeingDrawn * 2 + 1 == currentHealth) {
                gui.drawTexturedModalRect(heartToDrawX, heartToDrawY, texturePosX + foeOffset + 45, 9 * hardcoreModeOffset, 9, 9);
            }
        }

        y += (numRowsOfHearts - 1) * j2 + 10;
    }

    private void drawArmor() {
        mc.getTextureManager().bindTexture(GuiEntityStatus.ICONS);
        float armor = getArmorValue(entity);
        if (armor <= 0) return;

        int numIcons = MathHelper.ceiling_float_int(armor);
        for (int i = 0; i < numIcons; ++i) {
            int armorIconX = x + i * 8;
            float upper = i + 1;
            if (armor >= upper) {
                gui.drawTexturedModalRect(armorIconX, y, 34, 9, 9, 9);
            } else {
                gui.drawTexturedModalRect(armorIconX, y, 25, 9, 9, 9);
            }
        }
        y += 10;
    }

    private float getArmorValue(EntityLivingBase entity) {
        return entity.getTotalProtection(DamageSource.causeMobDamage((EntityLivingBase) null));
    }
}
