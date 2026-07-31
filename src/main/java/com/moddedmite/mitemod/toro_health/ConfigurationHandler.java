package com.moddedmite.mitemod.toro_health;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationHandler {

    public static boolean showEntityModel;
    public static boolean showDamageParticles;
    public static String entityStatusDisplay;
    public static String statusDisplayPosition;
    public static String skin;
    public static int statusDisplayX;
    public static int statusDisplayY;
    public static int damageColor;
    public static int healColor;
    public static int hideDelay;

    private static final String[] ACCEPTED_DISPLAY = {"HEARTS", "NUMERIC", "BAR", "OFF"};
    private static final String[] ACCEPTED_POSITIONS = {"TOP LEFT", "TOP CENTER", "TOP RIGHT", "BOTTOM LEFT", "BOTTOM RIGHT", "CUSTOM"};
    private static final String[] ACCEPTED_SKINS = {"NONE", "BASIC", "HEAVY"};
    private static final String[] ACCEPTED_COLORS = {"RED", "GREEN", "BLUE", "YELLOW", "ORANGE", "WHITE", "BLACK", "PURPLE"};

    private static File configFile;
    private static final Properties properties = new Properties();

    public static void init() {
        try {
            File configDir = new File("config");
            if (!configDir.exists()) {
                configDir.mkdir();
            }
            configFile = new File(configDir, "toro_health.properties");
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            loadConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfiguration() {
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
            skin = getValidValue(properties.getProperty("skin", "BASIC"), ACCEPTED_SKINS, "BASIC");
            showEntityModel = Boolean.parseBoolean(properties.getProperty("show_entity_model", "true"));
            entityStatusDisplay = getValidValue(properties.getProperty("entity_status_display", "HEARTS"), ACCEPTED_DISPLAY, "HEARTS");
            statusDisplayPosition = getValidValue(properties.getProperty("status_display_position", "TOP LEFT"), ACCEPTED_POSITIONS, "TOP LEFT");
            statusDisplayX = Integer.parseInt(properties.getProperty("status_display_x", "0"));
            statusDisplayY = Integer.parseInt(properties.getProperty("status_display_y", "0"));
            hideDelay = Integer.parseInt(properties.getProperty("hide_delay", "400"));
            showDamageParticles = Boolean.parseBoolean(properties.getProperty("show_damage_particles", "true"));
            healColor = mapColor(properties.getProperty("heal_color", "GREEN"));
            damageColor = mapColor(properties.getProperty("damage_color", "RED"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            saveConfiguration();
        }
    }

    public static void saveConfiguration() {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.setProperty("skin", skin);
            properties.setProperty("show_entity_model", String.valueOf(showEntityModel));
            properties.setProperty("entity_status_display", entityStatusDisplay);
            properties.setProperty("status_display_position", statusDisplayPosition);
            properties.setProperty("status_display_x", String.valueOf(statusDisplayX));
            properties.setProperty("status_display_y", String.valueOf(statusDisplayY));
            properties.setProperty("hide_delay", String.valueOf(hideDelay));
            properties.setProperty("show_damage_particles", String.valueOf(showDamageParticles));
            properties.setProperty("heal_color", getColorName(healColor));
            properties.setProperty("damage_color", getColorName(damageColor));
            properties.store(fos, "ToroHealth Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getValidValue(String value, String[] accepted, String defaultValue) {
        if (value == null) return defaultValue;
        for (String s : accepted) {
            if (s.equals(value)) return value;
        }
        return defaultValue;
    }

    private static int mapColor(String color) {
        if (color == null) return 0xffffff;
        switch (color) {
            case "RED": return 0xff0000;
            case "GREEN": return 0x00ff00;
            case "BLUE": return 0x0000ff;
            case "YELLOW": return 0xffff00;
            case "ORANGE": return 0xffa500;
            case "BLACK": return 0x000000;
            case "PURPLE": return 0x960096;
            default: return 0xffffff;
        }
    }

    private static String getColorName(int color) {
        switch (color) {
            case 0xff0000: return "RED";
            case 0x00ff00: return "GREEN";
            case 0x0000ff: return "BLUE";
            case 0xffff00: return "YELLOW";
            case 0xffa500: return "ORANGE";
            case 0x000000: return "BLACK";
            case 0x960096: return "PURPLE";
            default: return "WHITE";
        }
    }
}
