package com.hollingsworth.arsnouveau.setup.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigUtil {
    /**
     * Pattern describing an entry in a map
     * <p>
     * Expected format is "string=int"
     * Example: "fortune=3"
     */
    public static final Pattern STRING_INT_MAP = Pattern.compile("(.+?)=(\\d+)");

    /**
     * Parse glyph_limits into a Map from augment glyph tags to limits.
     */
    public static Map<String, Integer> parseMapConfig(ModConfigSpec.ConfigValue<List<? extends String>> configValue) {
        return configValue.get().stream()
                .map(STRING_INT_MAP::matcher)
                .filter(Matcher::matches)
                .collect(Collectors.toMap(
                        m -> m.group(1),
                        m -> Integer.valueOf(m.group(2))
                ));
    }

    /**
     * Produces a list of tag=limit strings suitable for saving to the configuration.
     */
    public static List<String> writeConfig(Map<String, Integer> map) {
        return map.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue().toString())
                .collect(Collectors.toList());
    }

    /**
     * Produces a list of tag=limit strings suitable for saving to the configuration.
     */
    public static List<String> writeResConfig(Map<ResourceLocation, Integer> map) {
        return map.entrySet().stream()
                .map(e -> e.getKey().toString() + "=" + e.getValue().toString())
                .collect(Collectors.toList());
    }

    /**
     * Ensure glyph_limits matches the expected regex pattern.
     */
    public static boolean validateMap(Object rawConfig) {
        if (rawConfig instanceof CharSequence raw) {
            return STRING_INT_MAP.matcher(raw).matches();
        }
        return false;
    }

}
