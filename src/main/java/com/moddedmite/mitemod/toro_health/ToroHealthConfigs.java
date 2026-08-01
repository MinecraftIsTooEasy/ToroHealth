package com.moddedmite.mitemod.toro_health;

import fi.dy.masa.malilib.config.SimpleConfigs;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigString;

import java.util.ArrayList;
import java.util.List;

public class ToroHealthConfigs extends SimpleConfigs {

    public static final ConfigBoolean showEntityModel =
            new ConfigBoolean("toro_health.show_entity_model", true, "显示实体3D模型");
    public static final ConfigBoolean showDamageParticles =
            new ConfigBoolean("toro_health.show_damage_particles", true, "显示伤害数字粒子");
    public static final ConfigString entityStatusDisplay =
            new ConfigString("toro_health.entity_status_display", "HEARTS", "血量样式: HEARTS / NUMERIC / BAR / OFF");
    public static final ConfigString statusDisplayPosition =
            new ConfigString("toro_health.status_display_position", "TOP LEFT", "显示位置: TOP LEFT / TOP CENTER / TOP RIGHT / BOTTOM LEFT / BOTTOM RIGHT / CUSTOM");
    public static final ConfigString skin =
            new ConfigString("toro_health.skin", "BASIC", "皮肤: NONE / BASIC / HEAVY");
    public static final ConfigInteger statusDisplayX =
            new ConfigInteger("toro_health.status_display_x", 0, -200, 200, true, "X偏移");
    public static final ConfigInteger statusDisplayY =
            new ConfigInteger("toro_health.status_display_y", 0, -200, 200, true, "Y偏移");
    public static final ConfigInteger hideDelay =
            new ConfigInteger("toro_health.hide_delay", 400, 0, 10000, true, "隐藏延迟(ms)");
    public static final ConfigColor damageColor =
            new ConfigColor("toro_health.damage_color", "#FFFF0000", "伤害数字颜色");
    public static final ConfigColor healColor =
            new ConfigColor("toro_health.heal_color", "#FF00FF00", "治疗数字颜色");

    private static final ToroHealthConfigs INSTANCE = new ToroHealthConfigs();

    public ToroHealthConfigs() {
        super(
                "toro_health",
                List.of(),
                List.of(
                        showEntityModel,
                        showDamageParticles,
                        entityStatusDisplay,
                        statusDisplayPosition,
                        skin,
                        statusDisplayX,
                        statusDisplayY,
                        hideDelay,
                        damageColor,
                        healColor
                ),
                "ToroHealth 配置"
        );
    }

    public static ToroHealthConfigs getInstance() {
        return INSTANCE;
    }

    public static boolean showEntityModel() {
        return showEntityModel.getBooleanValue();
    }

    public static boolean showDamageParticles() {
        return showDamageParticles.getBooleanValue();
    }

    public static String entityStatusDisplay() {
        return entityStatusDisplay.getStringValue();
    }

    public static String statusDisplayPosition() {
        return statusDisplayPosition.getStringValue();
    }

    public static String skin() {
        return skin.getStringValue();
    }

    public static int statusDisplayX() {
        return statusDisplayX.getIntegerValue();
    }

    public static int statusDisplayY() {
        return statusDisplayY.getIntegerValue();
    }

    public static int hideDelay() {
        return hideDelay.getIntegerValue();
    }

    public static int damageColor() {
        return damageColor.getColorInteger();
    }

    public static int healColor() {
        return healColor.getColorInteger();
    }
}
