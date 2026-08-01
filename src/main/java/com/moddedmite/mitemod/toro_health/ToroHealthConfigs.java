package com.moddedmite.mitemod.toro_health;

import fi.dy.masa.malilib.config.SimpleConfigs;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigEnum;
import fi.dy.masa.malilib.config.options.ConfigInteger;

import java.util.List;

public class ToroHealthConfigs extends SimpleConfigs {

    public enum EntityStatusDisplay {
        HEARTS, NUMERIC, BAR, OFF
    }

    public enum StatusDisplayPosition {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CUSTOM;

        public boolean isTop() { return this == TOP_LEFT || this == TOP_CENTER || this == TOP_RIGHT; }
        public boolean isBottom() { return this == BOTTOM_LEFT || this == BOTTOM_RIGHT; }
        public boolean isLeft() { return this == TOP_LEFT || this == BOTTOM_LEFT || this == CUSTOM; }
        public boolean isRight() { return this == TOP_RIGHT || this == BOTTOM_RIGHT; }
        public boolean isCenter() { return this == TOP_CENTER; }
        public boolean isCustom() { return this == CUSTOM; }
    }

    public enum Skin {
        NONE, BASIC, HEAVY
    }

    public static final ConfigBoolean showEntityModel =
            new ConfigBoolean("toro_health.show_entity_model", true, "显示实体3D模型");
    public static final ConfigBoolean showDamageParticles =
            new ConfigBoolean("toro_health.show_damage_particles", true, "显示伤害数字粒子");
    public static final ConfigEnum<EntityStatusDisplay> entityStatusDisplay =
            new ConfigEnum<>("toro_health.entity_status_display", EntityStatusDisplay.HEARTS, "血量样式");
    public static final ConfigEnum<StatusDisplayPosition> statusDisplayPosition =
            new ConfigEnum<>("toro_health.status_display_position", StatusDisplayPosition.TOP_LEFT, "显示位置");
    public static final ConfigEnum<Skin> skin =
            new ConfigEnum<>("toro_health.skin", Skin.BASIC, "皮肤");
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

    public static EntityStatusDisplay entityStatusDisplay() {
        return entityStatusDisplay.getEnumValue();
    }

    public static StatusDisplayPosition statusDisplayPosition() {
        return statusDisplayPosition.getEnumValue();
    }

    public static Skin skin() {
        return skin.getEnumValue();
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
