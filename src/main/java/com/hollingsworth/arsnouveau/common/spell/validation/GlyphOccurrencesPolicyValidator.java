package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spell validation that enforces the glyph limits policy set out in the configuration.
 */
public class GlyphOccurrencesPolicyValidator extends ScanningSpellValidator<Map<ResourceLocation, Integer>> {
    @Override
    protected Map<ResourceLocation, Integer> initContext() {
        return new HashMap<>();
    }

    @Override
    protected void digestSpellPart(Map<ResourceLocation, Integer> partCounts, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors) {
        // Count the glyph
        if (partCounts.containsKey(spellPart.getRegistryName())) {
            partCounts.put(spellPart.getRegistryName(), partCounts.get(spellPart.getRegistryName()) + 1);
        } else {
            partCounts.put(spellPart.getRegistryName(), 1);
        }

        // Check if over the limit
        int limit = spellPart.PER_SPELL_LIMIT == null ? Integer.MAX_VALUE : spellPart.PER_SPELL_LIMIT.get();
        if (partCounts.getOrDefault(spellPart.getRegistryName(), 0) > limit) {
            validationErrors.add(new GlyphOccurrencesPolicySpellValidationError(position, spellPart, limit));
        }
    }

    @Override
    protected void finish(Map<ResourceLocation, Integer> context, List<SpellValidationError> validationErrors) {
    }

    private static class GlyphOccurrencesPolicySpellValidationError extends BaseSpellValidationError {
        public GlyphOccurrencesPolicySpellValidationError(int position, AbstractSpellPart part, int limit) {
            super(
                    position,
                    part,
                    "glyph_occurrences_policy",
                    part
            );
        }
    }
}
