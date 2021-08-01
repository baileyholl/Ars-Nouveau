package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.util.text.IFormattableTextComponent;

import javax.annotation.Nullable;
import java.util.List;

/**
 * An indication of some sort of validation error when confirming if a spell is correctly constructed.
 *
 * Instances of this are obtained from {@link ISpellValidator#validate(List)}.
 *
 * This interface is not intended to be implemented by clients of the API.
 */
public interface SpellValidationError {
    /**
     * Returns the position this validation error refers to, or <code>-1</code> if the error indicates a problem with
     * the entire spell.
     */
    int getPosition();

    /**
     * Returns the spell part this validation error refers to, or <code>null</code> if the error indicates a problem
     * with the entire spell.
     */
    @Nullable
    AbstractSpellPart getSpellPart();

    /**
     * Creates and returns a Text Component with a full error message, where the use case is reporting an issue with
     * an existing glyph.
     */
    IFormattableTextComponent makeTextComponentExisting();
    /**
     * Creates and returns a Text Component with a full error message, where the use case is reporting an issue that
     * will occur if adding a glyph to a spell.
     */
    IFormattableTextComponent makeTextComponentAdding();
}
