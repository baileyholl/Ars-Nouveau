package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellValidator;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;

import java.util.LinkedList;
import java.util.List;

/**
 * A scanning spell validator operates by scanning over the spell glyph by glyph from left to right, collecting any
 * errors found during the process.
 * <p>
 * The design of this class ensures that the actual <code>ScanningSpellValidator</code> object is immutable, while still
 * allowing mutable state during spell validation.
 */
public abstract class ScanningSpellValidator<T> implements ISpellValidator {
    /**
     * Called to initialize a new context for a validation job.
     */
    protected abstract T initContext();

    /**
     * Called to validate the given spell part, accumulating any state in <code>context</code> and adding
     * any discovered validation errors to <code>errorAccumulator</code>.
     *
     * @param context          the context the stateful validator stores its state in
     * @param position         the position within the spell recipe this glyph appears in
     * @param spellPart        the current glyph being validated
     * @param validationErrors a list the validator may add new errors to. Like <code>context</code>, this list is
     *                         private to the specific validator, allowing steps to remove or change existing errors if
     *                         desired.
     */
    protected abstract void digestSpellPart(T context, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors);

    /**
     * Called to complete validation after all glyphs have been consumed for validation.
     *
     * @param context          the final context at the end of validation
     * @param validationErrors a list the finisher may add new errors to. Like <code>context</code>, this list is
     *                         private to the specific job, allowing steps to remove or change existing errors if desired.
     */
    protected void finish(T context, List<SpellValidationError> validationErrors){

    }

    @Override
    public List<SpellValidationError> validate(List<AbstractSpellPart> spellRecipe) {
        T t = initContext();
        List<SpellValidationError> errors = new LinkedList<>();

        for (int pos = 0; pos < spellRecipe.size(); pos++) {
            AbstractSpellPart part = spellRecipe.get(pos);
            if (part != null) {
                digestSpellPart(t, pos, part, errors);
            }
        }
        finish(t, errors);
        return errors;
    }
}
