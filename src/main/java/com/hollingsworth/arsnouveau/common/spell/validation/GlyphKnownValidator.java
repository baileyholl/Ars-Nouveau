package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlyphKnownValidator extends ScanningSpellValidator<Unit> {

    @Nullable IPlayerCap playerDataCap;

    public GlyphKnownValidator(@Nullable IPlayerCap player) {
        this.playerDataCap = player;
    }


    @Override
    protected Unit initContext() {
        return Unit.INSTANCE;
    }

    @Override
    protected void digestSpellPart(Unit context, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors) {
        if (playerDataCap != null) {
            if (!playerDataCap.knowsGlyph(spellPart) && !GlyphRegistry.getDefaultStartingSpells().contains(spellPart)) {
                validationErrors.add(new GlyphUnknownValidationError(position, spellPart, "glyph_not_known"));
            }
        }
    }


    static class GlyphUnknownValidationError extends BaseSpellValidationError {
        public GlyphUnknownValidationError(int position, AbstractSpellPart spellPart, String localizationCode) {
            super(position, spellPart, localizationCode, spellPart);
        }
    }

}
