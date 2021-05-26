package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;

import java.util.List;

/**
 * Spell validation that requires a spell start with a cast method.
 */
public class StartingCastMethodSpellValidator extends AbstractSpellValidator {
    @Override
    protected void validateImpl(List<AbstractSpellPart> spellRecipe, List<SpellValidationError> errors) {
        if (!(spellRecipe.isEmpty() || spellRecipe.get(0) instanceof AbstractCastMethod)) {
            errors.add(new StartingCastMethodSpellValidationError());
        }
    }

    private static class StartingCastMethodSpellValidationError extends BaseSpellValidationError {
        public StartingCastMethodSpellValidationError() {
            super(-1, null, "starting_cast_method");
        }
    }
}
