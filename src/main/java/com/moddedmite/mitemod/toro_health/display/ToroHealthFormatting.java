package com.moddedmite.mitemod.toro_health.display;

import net.minecraft.FontRenderer;

import java.util.Locale;

public final class ToroHealthFormatting {

    private static final String[] SUFFIXES = {"K", "M", "B", "T"};

    public static float healthRatio(float health, float maxHealth) {
        if (Float.isNaN(health) || Float.isNaN(maxHealth) || maxHealth <= 0.0F || health <= 0.0F) {
            return 0.0F;
        }
        if (!Float.isFinite(maxHealth)) return 0.0F;
        if (health >= maxHealth) return 1.0F;
        return health / maxHealth;
    }

    public static String formatHealthPair(float health, float maxHealth) {
        return formatHealthValue(health) + "/" + formatHealthValue(maxHealth);
    }

    public static String fitText(FontRenderer fontRenderer, String text, int maxWidth) {
        if (text == null || maxWidth <= 0) return "";
        if (fontRenderer.getStringWidth(text) <= maxWidth) return text;

        String ellipsis = "...";
        int prefixWidth = Math.max(0, maxWidth - fontRenderer.getStringWidth(ellipsis));
        return fontRenderer.trimStringToWidth(text, prefixWidth) + ellipsis;
    }

    public static String formatHealthValue(float value) {
        if (Float.isNaN(value) || value <= 0.0F) return "0";
        if (value == Float.POSITIVE_INFINITY) return "MAX";
        return formatAmount(Math.ceil(value));
    }

    public static String formatAmount(double value) {
        if (Double.isNaN(value) || value <= 0.0D) return "0";
        if (Double.isInfinite(value)) return "MAX";

        double rounded = Math.ceil(value);
        if (rounded < 1000.0D) return Long.toString((long) rounded);

        double compactValue = rounded;
        int suffixIndex = -1;
        while (compactValue >= 1000.0D && suffixIndex < SUFFIXES.length - 1) {
            compactValue /= 1000.0D;
            suffixIndex++;
        }
        if (compactValue >= 1000.0D) {
            String scientific = String.format(Locale.ROOT, "%.1e", rounded);
            return scientific.replace(".0e", "e");
        }

        double roundedCompactValue = compactValue >= 100.0D
                ? Math.round(compactValue)
                : Math.round(compactValue * 10.0D) / 10.0D;
        if (roundedCompactValue >= 1000.0D && suffixIndex < SUFFIXES.length - 1) {
            roundedCompactValue /= 1000.0D;
            suffixIndex++;
        }

        String amount = roundedCompactValue == Math.rint(roundedCompactValue)
                ? Long.toString((long) roundedCompactValue)
                : String.format(Locale.ROOT, "%.1f", roundedCompactValue);
        return amount + SUFFIXES[suffixIndex];
    }
}
