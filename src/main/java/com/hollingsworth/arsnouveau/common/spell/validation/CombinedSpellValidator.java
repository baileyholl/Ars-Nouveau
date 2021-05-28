package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellValidator;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Combined spell validator that runs the validations of all its children.
 */
public class CombinedSpellValidator implements ISpellValidator {
    private final List<ISpellValidator> validators;

    /** Create a combined spell validator from the given list of validators. */
    public CombinedSpellValidator(List<ISpellValidator> validators) {
        this.validators = validators;
    }

    /** Create a combined spell validator from the given list of validators. */
    public CombinedSpellValidator(ISpellValidator... validators) {
        this.validators = Arrays.asList(validators);
    }

    @Override
    public List<SpellValidationError> validate(List<AbstractSpellPart> spellRecipe) {
        return validators.stream()
                .flatMap(v -> v.validate(spellRecipe).stream())
                .collect(Collectors.toList());
    }
}
