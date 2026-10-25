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
            new ConfigBoolean("toro_health.show_entity_model", true, "config.comment.toro_health.show_entity_model");
    public static final ConfigBoolean showDamageParticles =
            new ConfigBoolean("toro_health.show_damage_particles", true, "config.comment.toro_health.show_damage_particles");
    public static final ConfigEnum<EntityStatusDisplay> entityStatusDisplay =
            new ConfigEnum<>("toro_health.entity_status_display", EntityStatusDisplay.HEARTS, "config.comment.toro_health.entity_status_display");
    public static final ConfigEnum<StatusDisplayPosition> statusDisplayPosition =
            new ConfigEnum<>("toro_health.status_display_position", StatusDisplayPosition.TOP_LEFT, "config.comment.toro_health.status_display_position");
    public static final ConfigEnum<Skin> skin =
            new ConfigEnum<>("toro_health.skin", Skin.BASIC, "config.comment.toro_health.skin");
    public static final ConfigInteger statusDisplayX =
            new ConfigInteger("toro_health.status_display_x", 0, -200, 200, true, "config.comment.toro_health.status_display_x");
    public static final ConfigInteger statusDisplayY =
            new ConfigInteger("toro_health.status_display_y", 0, -200, 200, true, "config.comment.toro_health.status_display_y");
    public static final ConfigInteger statusDisplayScale =
            new ConfigInteger("toro_health.status_display_scale", 100, 50, 200, true, "config.comment.toro_health.status_display_scale");
    public static final ConfigInteger hideDelay =
            new ConfigInteger("toro_health.hide_delay", 400, 0, 10000, true, "config.comment.toro_health.hide_delay");
    public static final ConfigColor damageColor =
            new ConfigColor("toro_health.damage_color", "#FFFF0000", "config.comment.toro_health.damage_color");
    public static final ConfigColor healColor =
            new ConfigColor("toro_health.heal_color", "#FF00FF00", "config.comment.toro_health.heal_color");

    private static final ToroHealthConfigs INSTANCE = new ToroHealthConfigs();

    public ToroHealthConfigs() {
        super(
                "toro_health",
                null,
                List.of(
                        showEntityModel,
                        showDamageParticles,
                        entityStatusDisplay,
                        statusDisplayPosition,
                        skin,
                        statusDisplayX,
                        statusDisplayY,
                        statusDisplayScale,
                        hideDelay,
                        damageColor,
                        healColor
                )
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

    public static int statusDisplayScale() {
        return statusDisplayScale.getIntegerValue();
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
