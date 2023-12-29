package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;

import java.util.List;

/**
 * Spell validation that requires at most one cast method in a spell.
 */
public class MaxOneCastMethodSpellValidator extends ScanningSpellValidator<MaxOneCastMethodSpellValidator.OneCastContext> {
    @Override
    protected OneCastContext initContext() {
        return new OneCastContext();
    }

    @Override
    protected void digestSpellPart(OneCastContext context, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors) {
        if (spellPart instanceof AbstractCastMethod) {
            context.count++;
            if (context.count > 1) {
                validationErrors.add(new OneCastMethodSpellValidationError(position, (AbstractCastMethod) spellPart));
            }
        }
    }

    /**
     * Context for this validator is a simple counter on AbstractCastMethod parts
     */
    public static class OneCastContext {
        int count = 0;
    }

    private static class OneCastMethodSpellValidationError extends BaseSpellValidationError {
        public OneCastMethodSpellValidationError(int position, AbstractCastMethod method) {
            super(position, method, "max_one_cast_method", method);
        }
    }
}
