package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Map;
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
        private Map<ResourceLocation, Integer> limits = null;
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
            limits = parseAugmentLimits();
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

    /**
     * Builds a "augment_limits" configuration item using the provided {@link ForgeConfigSpec.Builder} and returns an
     * {@link AugmentLimits} instance to encapsulate it.
     */
    public static AugmentLimits buildAugmentLimitsConfig(ForgeConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        ForgeConfigSpec.ConfigValue<List<? extends String>> configValue = builder
                .comment("Limits the number of times a given augment may be applied to a given effect", "Example entry: \"" + GlyphLib.AugmentAmplifyID + "=5\"")
                .defineList("augment_limits", writeConfig(defaults), SpellPartConfigUtil::validateAugmentLimits);

        return new AugmentLimits(configValue);
    }

    /**
     * Builds a "augment_limits" configuration item using the provided {@link ForgeConfigSpec.Builder} and returns an
     * {@link AugmentLimits} instance to encapsulate it.
     */
    public static AugmentCosts buildAugmentCosts(ForgeConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        ForgeConfigSpec.ConfigValue<List<? extends String>> configValue = builder
                .comment("How much an augment should cost when used on this effect or form. This overrides the default cost in the augment config.", "Example entry: \"" + GlyphLib.AugmentAmplifyID + "=50\"")
                .defineList("augment_cost_overrides", writeConfig(defaults), SpellPartConfigUtil::validateAugmentLimits);

        return new AugmentCosts(configValue);
    }

    /**
     * Produces a list of tag=limit strings suitable for saving to the configuration.
     */
    private static List<String> writeConfig(Map<ResourceLocation, Integer> augmentLimits) {
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
        private ForgeConfigSpec.ConfigValue<List<? extends String>> configValue;

        /**
         * Create a new AugmentLimits from the given ConfigValue
         */
        private AugmentCosts(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue) {
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
                            m -> new ResourceLocation(m.group(1)),
                            m -> Integer.valueOf(m.group(2))
                    ));
        }
    }
}
