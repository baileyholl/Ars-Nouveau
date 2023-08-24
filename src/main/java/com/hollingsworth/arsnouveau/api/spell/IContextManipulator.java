package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.common.spell.validation.BaseSpellValidationError;
import com.hollingsworth.arsnouveau.common.spell.validation.InvalidNestingValidator;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Stack;

public interface IContextManipulator {

    /**
     * return true if the context manipulator has an inner context, I.E. if it is escapable with escape context
     * most context manipulators, such as linger, should return true
     */

    boolean isEscapable();

    /**
     * used by context escapes when calculating the inner and post context spell.
     * allows a context escape to pop the context by a custom amount.
     * should not modify the context in any way
     * @return the new push count in the traversal after this escape context
     */
    default int push(int currentPushCount, @Nullable SpellContext context, int posInRecipe){
        return isEscapable() ? currentPushCount + 1 : currentPushCount;
    }

    /**
     * used by the spell validator
     * @param stack the current stack to modify
     * @param part this as an abstract spell part
     * @param posInRecipe the position of the spell part in the recipe
     * @param validationErrors a list of validation errors that can optionally be appended to
     */
    default void push(InvalidNestingValidator.NestingContextStack stack, AbstractSpellPart part, int posInRecipe, List<SpellValidationError> validationErrors) {
        if(isEscapable()) {
            stack.push(part);
        }
    }

    /**
     * called when this context manipulator is popped of the manipulator stack during spell validation
     * @param stack the context manipulator stack this was popped from
     */
    default void onPopped(InvalidNestingValidator.NestingContextStack stack){

    }
}
