package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spell validation that enforces the glyph limits policy set out in the configuration.
 */
public class GlyphOccurrencesPolicyValidator extends ScanningSpellValidator<Map<String, Integer>> {
    @Override
    protected Map<String, Integer> initContext() {
        return new HashMap<>();
    }

    @Override
    protected void digestSpellPart(Map<String, Integer> partCounts, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors) {
        // Count the glyph
        if (partCounts.containsKey(spellPart.tag)) {
            partCounts.put(spellPart.tag, partCounts.get(spellPart.tag) + 1);
        } else {
            partCounts.put(spellPart.tag, 1);
        }

        // Check if over the limit
        int limit = spellPart.PER_SPELL_LIMIT.get();
        if (partCounts.getOrDefault(spellPart.tag, 0) > limit) {
            validationErrors.add(new GlyphOccurrencesPolicySpellValidationError(position, spellPart, limit));
        }
    }

    @Override
    protected void finish(Map<String, Integer> context, List<SpellValidationError> validationErrors) {}

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
