package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for code around handling spell part configuration.
 */
public class SpellPartConfigUtil {
    /**
     * Pattern describing an entry in the augment limits configuration.
     * <p>
     * Expected format is "glyphtag=limit"
     * Example: "fortune=3"
     */
    private static final Pattern AUGMENT_LIMITS_PATTERN = Pattern.compile("([^/=]+)=(\\d+)");

    /**
     * Class used to encapsulate the logic around parsing and printing the augment limit configuration.
     * <p>
     * Used by {@link com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart} to handle configuring augmentation
     * limitations.
     */
    public static class AugmentLimits {
        private ForgeConfigSpec.ConfigValue<List<? extends String>> configValue;

        /**
         * Create a new AugmentLimits from the given ConfigValue
         */
        private AugmentLimits(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue) {
            this.configValue = configValue;
        }

        /**
         * Retrieves the maximum number of times the given augment may be applied, given the configuration.
         */
        public int getAugmentLimit(ResourceLocation augmentTag) {
            // No caching so /reload works
            Map<ResourceLocation, Integer> limits = parseAugmentLimits();
            return limits.getOrDefault(augmentTag, Integer.MAX_VALUE);
        }


        /**
         * Parse glyph_limits into a Map from augment glyph tags to limits.
         */
        private Map<ResourceLocation, Integer> parseAugmentLimits() {
            return configValue.get().stream()
                    .map(AUGMENT_LIMITS_PATTERN::matcher)
                    .filter(Matcher::matches)
                    .collect(Collectors.toMap(
                            m -> new ResourceLocation(m.group(1)),
                            m -> Integer.valueOf(m.group(2))
                    ));
        }
    }

    public static class ComboLimits {
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> configValue;

        /**
         * Create a new AugmentLimits from the given ConfigValue
         */
        public ComboLimits(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue) {
            this.configValue = configValue;
        }

        public boolean contains(ResourceLocation glyphTag) {
            return parseComboLimits().contains(glyphTag);
        }


        /**
         * Parse glyph_limits into a Map from augment glyph tags to limits.
         */
        public Set<ResourceLocation> parseComboLimits() {
            if (configValue == null) {
                return new HashSet<>();
            }
            return configValue.get().stream()
                    .map(ResourceLocation::tryParse)
                    .collect(Collectors.toSet());
        }
    }


    /**
     * Builds a "augment_limits" configuration item using the provided {@link ForgeConfigSpec.Builder} and returns an
     * {@link AugmentLimits} instance to encapsulate it.
     */
    public static AugmentLimits buildAugmentLimitsConfig(ForgeConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        ForgeConfigSpec.ConfigValue<List<? extends String>> configValue = builder
                .comment("Limits the number of times a given augment may be applied to a given effect", "Example entry: \"" + GlyphLib.AugmentAmplifyID + "=5\"")
                .defineList("augment_limits", writeAugmentConfig(defaults), SpellPartConfigUtil::validateAugmentLimits);

        return new AugmentLimits(configValue);
    }

    public static ComboLimits buildInvalidCombosConfig(ForgeConfigSpec.Builder builder, Set<ResourceLocation> defaults) {
        ForgeConfigSpec.ConfigValue<List<? extends String>> configValue = builder
                .comment("Prevents the given glyph from being used in the same spell as the given glyph", "Example entry: \"" + GlyphLib.EffectBurstID + "\"")
                .defineList("invalid_combos", writeComboConfig(defaults), (o) -> o instanceof String s && ResourceLocation.isValidResourceLocation(s));

        return new ComboLimits(configValue);
    }

    /**
     * Produces a list of resourcelocation strings suitable for saving to the configuration.
     */
    private static List<String> writeComboConfig(Set<ResourceLocation> augmentLimits) {
        return augmentLimits.stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
    }

    /**
     * Produces a list of tag=limit strings suitable for saving to the configuration.
     */
    private static List<String> writeAugmentConfig(Map<ResourceLocation, Integer> augmentLimits) {
        return augmentLimits.entrySet().stream()
                .map(e -> e.getKey().toString() + "=" + e.getValue().toString())
                .collect(Collectors.toList());
    }

    /**
     * Ensure glyph_limits matches the expected regex pattern.
     */
    private static boolean validateAugmentLimits(Object rawConfig) {
        if (rawConfig instanceof CharSequence) {
            return AUGMENT_LIMITS_PATTERN.matcher((CharSequence) rawConfig).matches();
        }
        return false;
    }
}
