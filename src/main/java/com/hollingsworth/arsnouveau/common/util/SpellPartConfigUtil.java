package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.GlyphLib;
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
     *
     * Expected format is "glyphtag=limit"
     * Example: "fortune=3"
     */
    private static final Pattern AUGMENT_LIMITS_PATTERN = Pattern.compile("([^/=]+)=(\\p{Digit}+)");

    /**
     * Class used to encapsulate the logic around parsing and printing the augment limit configuration.
     *
     * Used by {@link com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart} to handle configuring augmentation
     * limitations.
     */
    public static class AugmentLimits {
        private Map<String, Integer> limits = null;
        private ForgeConfigSpec.ConfigValue<List<? extends String>> configValue;

        /** Create a new AugmentLimits from the given ConfigValue */
        private AugmentLimits(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue) {
            this.configValue = configValue;
        }

        /** Retrieves the maximum number of times the given augment may be applied, given the configuration. */
        public int getAugmentLimit(String augmentTag) {
            if (limits == null) {
                limits = parseAugmentLimits();
            }
            return limits.getOrDefault(augmentTag, Integer.MAX_VALUE);
        }

        /** Sets the maximum number of times the given augment may be applied and updates the configuration. */
        public void setAugmentLimit(String augmentTag, int limit) {
            if (limits == null) {
                limits = parseAugmentLimits();
            }
            limits.put(augmentTag, limit);
            configValue.set(writeConfig(limits));
        }

        /** Parse glyph_limits into a Map from augment glyph tags to limits. */
        private Map<String, Integer> parseAugmentLimits() {
            return configValue.get().stream()
                    .map(AUGMENT_LIMITS_PATTERN::matcher)
                    .filter(Matcher::matches)
                    .collect(Collectors.toMap(
                            m -> m.group(1),
                            m -> Integer.valueOf(m.group(2))
                    ));
        }
    }

    /**
     * Builds a "augment_limits" configuration item using the provided {@link ForgeConfigSpec.Builder} and returns an
     * {@link AugmentLimits} instance to encapsulate it.
     */
    public static AugmentLimits buildAugmentLimitsConfig(ForgeConfigSpec.Builder builder, Map<String, Integer> defaults) {
        ForgeConfigSpec.ConfigValue<List<? extends String>> configValue = builder
                .comment("Limits the number of times a given augment may be applied to a given effect", "Example entry: \"" + GlyphLib.AugmentAmplifyID + "=5\"")
                .defineList("augment_limits", writeConfig(defaults), SpellPartConfigUtil::validateAugmentLimits);

        return new AugmentLimits(configValue);
    }

    /** Produces a list of tag=limit strings suitable for saving to the configuration. */
    private static List<String> writeConfig(Map<String, Integer> augmentLimits) {
        return augmentLimits.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue().toString())
                .collect(Collectors.toList());
    }

    /** Ensure glyph_limits matches the expected regex pattern. */
    private static boolean validateAugmentLimits(Object rawConfig) {
        if (rawConfig instanceof CharSequence) {
            return AUGMENT_LIMITS_PATTERN.matcher((CharSequence) rawConfig).matches();
        }
        return false;
    }
}
