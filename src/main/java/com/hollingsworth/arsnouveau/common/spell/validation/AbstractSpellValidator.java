package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellValidator;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract base class for ISpellValidators that handles some common boilerplate.
 */
public abstract class AbstractSpellValidator implements ISpellValidator {
    @Override
    public List<SpellValidationError> validate(List<AbstractSpellPart> spellRecipe) {
        List<SpellValidationError> errors = new LinkedList<>();
        validateImpl(spellRecipe, errors);
        return errors;
    }

    /**
     * Implementations of this method validate the given spell recipe, accumulating errors into <code>validationErrors</code>.
     *
     * @param spellRecipe the recipe of the spell to validate. The list may contain <code>null</code> elements, which
     *                    indicates that a glyph slot is currently blank.
     * @param validationErrors a list the validator may add new errors to. Like <code>context</code>, this list is
     *                         private to the specific validator, allowing steps to remove or change existing errors if
     *                         desired.
     */
    protected abstract void validateImpl(List<AbstractSpellPart> spellRecipe, List<SpellValidationError> validationErrors);
}
