package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.util.*;

import java.util.*;

/**
 * Spell validation that enforces that glyphs be unlocked.
 *
 * This validator is pulled in specially in {@link com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook}.
 */
public class GlyphUnlockedValidator extends ScanningSpellValidator<Unit> {
    private final List<AbstractSpellPart> unlockedSpells;

    /** Creates a glyph validator that will only accept glyphs that are present in this list. */
    public GlyphUnlockedValidator(List<AbstractSpellPart> unlockedSpells) {
        this.unlockedSpells = unlockedSpells;
    }

    @Override
    protected Unit initContext() { return Unit.INSTANCE; }

    @Override
    protected void digestSpellPart(Unit context, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors) {
        if(!unlockedSpells.contains(spellPart)) {
            validationErrors.add(new GlyphUnlockedValidationError(position, spellPart, "glyph_unlock"));
        }
    }

    @Override
    protected void finish(Unit context, List<SpellValidationError> validationErrors) {}

    public static class GlyphUnlockedValidationError extends BaseSpellValidationError {

        @Override
        public float getAlpha(){
            return 0.05F;
        }

        public GlyphUnlockedValidationError(int position, AbstractSpellPart spellPart, String localizationCode) {
            super(position, spellPart, localizationCode, spellPart);
        }
    }
}
