package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import net.minecraft.util.Unit;

import java.util.List;

/**
 * Spell validation that enforces that glyphs be equal to or of a lesser tier than the provided one.
 * <p>
 * This validator is pulled in specially in {@link com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook}.
 */
public class GlyphMaxTierValidator extends ScanningSpellValidator<Unit> {
    private final int maxTier;

    /**
     * Creates a glyph tier validator that will only accept glyphs of <code><maxTier/code> or <em>lower</em>.
     */
    public GlyphMaxTierValidator(int maxTier) {
        this.maxTier = maxTier;
    }

    @Override
    protected Unit initContext() {
        return Unit.INSTANCE;
    }

    @Override
    protected void digestSpellPart(Unit context, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors) {
        if (spellPart.getConfigTier().value > maxTier) {
            validationErrors.add(new GlyphTierValidationError(position, spellPart, "glyph_tier"));
        }
    }

    private static class GlyphTierValidationError extends BaseSpellValidationError {
        public GlyphTierValidationError(int position, AbstractSpellPart spellPart, String localizationCode) {
            super(position, spellPart, localizationCode, spellPart);
        }
    }
}
