package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.setup.config.ConfigUtil;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

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
        private ModConfigSpec.ConfigValue<List<? extends String>> configValue;

        /**
         * Create a new AugmentLimits from the given ConfigValue
         */
        private AugmentLimits(ModConfigSpec.ConfigValue<List<? extends String>> configValue) {
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
                            m -> ResourceLocation.tryParse(m.group(1)),
                            m -> Integer.valueOf(m.group(2))
                    ));
        }
    }

    public static class ComboLimits {
        private final ModConfigSpec.ConfigValue<List<? extends String>> configValue;

        /**
         * Create a new AugmentLimits from the given ConfigValue
         */
        public ComboLimits(ModConfigSpec.ConfigValue<List<? extends String>> configValue) {
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
     * Builds a "augment_limits" configuration item using the provided {@link ModConfigSpec.Builder} and returns an
     * {@link AugmentLimits} instance to encapsulate it.
     */
    public static AugmentLimits buildAugmentLimitsConfig(ModConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        ModConfigSpec.ConfigValue<List<? extends String>> configValue = builder
                .comment("Limits the number of times a given augment may be applied to a given effect", "Example entry: \"" + GlyphLib.AugmentAmplifyID + "=5\"")
                .defineList("augment_limits", writeAugmentConfig(defaults), SpellPartConfigUtil::validateAugmentLimits);

        return new AugmentLimits(configValue);
    }

    public static ComboLimits buildInvalidCombosConfig(ModConfigSpec.Builder builder, Set<ResourceLocation> defaults) {
        ModConfigSpec.ConfigValue<List<? extends String>> configValue = builder
                .comment("Prevents the given glyph from being used in the same spell as the given glyph", "Example entry: \"" + GlyphLib.EffectBurstID + "\"")
                .defineList("invalid_combos", writeComboConfig(defaults), (o) -> o instanceof String s && ResourceLocation.read(s).isSuccess());

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
     * Builds a "augment_limits" configuration item using the provided {@link ModConfigSpec.Builder} and returns an
     * {@link AugmentLimits} instance to encapsulate it.
     */
    public static AugmentCosts buildAugmentCosts(ModConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        ModConfigSpec.ConfigValue<List<? extends String>> configValue = builder
                .comment("How much an augment should cost when used on this effect or form. This overrides the default cost in the augment config.", "Example entry: \"" + GlyphLib.AugmentAmplifyID + "=50\"")
                .defineList("augment_cost_overrides", ConfigUtil.writeResConfig(defaults), SpellPartConfigUtil::validateAugmentLimits);

        return new AugmentCosts(configValue);
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

    /**
     * Class used to encapsulate the logic around parsing and printing the augment limit configuration.
     * <p>
     * Used by {@link com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart} to handle configuring augmentation
     * limitations.
     */
    public static class AugmentCosts {
        private Map<ResourceLocation, Integer> costs = null;
        private ModConfigSpec.ConfigValue<List<? extends String>> configValue;

        /**
         * Create a new AugmentLimits from the given ConfigValue
         */
        private AugmentCosts(ModConfigSpec.ConfigValue<List<? extends String>> configValue) {
            this.configValue = configValue;
        }

        /**
         * Retrieves the cost of the augment given an effect or form.
         */
        public int getAugmentCost(ResourceLocation effectTag, int fallback) {
            // No caching so /reload works
            costs = parseAugmentCosts();
            return costs.getOrDefault(effectTag, fallback);
        }

        /**
         * Parse glyph_limits into a Map from augment glyph tags to limits.
         */
        private Map<ResourceLocation, Integer> parseAugmentCosts() {
            return configValue.get().stream()
                    .map(AUGMENT_LIMITS_PATTERN::matcher)
                    .filter(Matcher::matches)
                    .collect(Collectors.toMap(
                            m -> ResourceLocation.tryParse(m.group(1)),
                            m -> Integer.valueOf(m.group(2))
                    ));
        }
    }
}
