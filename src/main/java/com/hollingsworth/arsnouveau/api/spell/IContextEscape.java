package com.hollingsworth.arsnouveau.api.spell;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * an interface for glyphs that escape or end an inner context, usually upon its creation.
 */
public interface IContextEscape {

    /**
     * only one context escape may provide the spell for a context manipulator
     * this method allows a context escape to chose to be passed over, thus allowing the next context escape to control the spell
     * @param context
     * @param posInRecipe
     * @return
     */
    default boolean shouldProvideSpell(@Nullable SpellContext context, int posInRecipe){
        return pop(1,context,posInRecipe) < 1;//if popping doesn't reduce push count, don't affect the spell
    }

    /**
     * used bu other context escapes when calculating in context and post context spells
     * allows a context escape to pop the context by a custom amount.
     * should not modify the context in any way
     * @param posInRecipe the position of this glyph in the recipe
     * @return the new push count in the traversal after this escape context
     */
    default int pop(int currentPushCount, @Nullable SpellContext context, int posInRecipe){
        return currentPushCount - 1;
    }

    /**
     * used by the spell validator
     * @param stack the current stack to modify
     * @param part this as an abstract spell part
     * @param posInRecipe the position of the spell part in the recipe
     * @param validationErrors a list of validation errors that can optionally be appended to
     */
    default void pop(Stack<AbstractSpellPart> stack, AbstractSpellPart part, int posInRecipe, List<SpellValidationError> validationErrors){
        stack.pop();
    }

    /**
     * Allows the context escape to modify the inner context created by SpellContext#getInnerContext before it returns
     * @param context the context to modify
     */
    default void modifyInnerContext(SpellContext context){
        //intentionally left blank
    }

    default void modifyPostContext(SpellContext context){
        //intentionally left blank
    }


    default Spell getInContextSpell(SpellContext context, int posInRecipe){
        return context.getSpell().clone().setRecipe(new ArrayList<>(context.getSpell().recipe.subList(context.getCurrentIndex(), posInRecipe)));
    }

    default Spell getPostContextSpell(SpellContext outerContext, int posInRecipe){
        return outerContext.getSpell().clone().setRecipe(new ArrayList<>(outerContext.getSpell().recipe.subList(posInRecipe,outerContext.getSpell().recipe.size())));
    }
}
