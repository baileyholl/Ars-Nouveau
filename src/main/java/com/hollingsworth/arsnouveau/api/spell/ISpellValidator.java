package com.hollingsworth.arsnouveau.api.spell;

import java.util.List;

/**
 * Interface describing a spell validator.  Validators are functions that analyze a spell and indicate if the spell
 * violates the rules defined by the specific implementation of this interface.
 *
 * Clients should obtain an instance of this interface from {@link com.hollingsworth.arsnouveau.api.ArsNouveauAPI}.
 *
 * This interface is not intended to be implemented by clients of the API.
 */
public interface ISpellValidator {
    /**
     * Validates the given spell recipe.
     *
     * @param spellRecipe the recipe of the spell to validate. The list may contain <code>null</code> elements, which
     *                    indicates that a glyph slot is currently blank.
     * @return a list of any {@link SpellValidationError}s found.
     */
    List<SpellValidationError> validate(List<AbstractSpellPart> spellRecipe);
}
