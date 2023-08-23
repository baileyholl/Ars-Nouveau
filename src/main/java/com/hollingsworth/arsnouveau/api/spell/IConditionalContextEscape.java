package com.hollingsworth.arsnouveau.api.spell;

/**
 * An interface for conditional context escapes, that have two inner contexts.
 */
public interface IConditionalContextEscape extends IContextEscape {
     /**
     * the spell for the second inner condition. Should return
      * @param context the spell context calling this
      * @param posInRecipe the position of this glyph in the recipe
     * @return
     */
    Spell getSecondConditionSpell(SpellContext context, int posInRecipe);

    /**
     * allows the context escape to modify the second context
     * @param context
     */
    default void modifySecondContext(SpellContext context){

    }
}
