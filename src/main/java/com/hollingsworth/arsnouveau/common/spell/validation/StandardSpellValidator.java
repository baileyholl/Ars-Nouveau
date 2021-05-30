package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellValidator;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;

import java.util.*;

/**
 * Primary class containing the logic to decide if a spell's recipe is <em>valid</em>.
 *
 * That is to say if it contains all the required elements to be cast, and that it does not violate any configured
 * limits to spell construction such as limits on the number of times a given glyph may appear.
 *
 * Instances of this class should be obtained from {@link com.hollingsworth.arsnouveau.api.ArsNouveauAPI}.
 */
public class StandardSpellValidator implements ISpellValidator {
    // Validators are static and immutable. It's safe to eagerly construct these and pull them in when a validator is requested.
    private static final ISpellValidator MAX_ONE_CAST_METHOD = new MaxOneCastMethodSpellValidator();
    private static final ISpellValidator NON_EMPTY_SPELL = new NonEmptySpellValidator();
    private static final ISpellValidator REQUIRE_CAST_METHOD_START = new StartingCastMethodSpellValidator();
    private static final ISpellValidator GLYPH_OCCURRENCES_POLICY = new GlyphOccurrencesPolicyValidator();
    private static final ISpellValidator EFFECT_AUGMENTATION_POLICY = new ActionAugmentationPolicyValidator();
    private static final ISpellValidator AUGMENT_COMPATIBILITY = new AugmentCompatibilityValidator();

    private final ISpellValidator combinedValidator;

    /**
     * Creates a standard spell recipe validator that enforces all standard rules.
     *
     * @param enforceCastTimeValidations if <code>true</code>, the spell must be valid for casting, which enables
     *                                   validations for having exactly one cast method as the first glyph. Pass
     *                                   <code>false</code>, when validating for spell crafting.
     */
    public StandardSpellValidator(boolean enforceCastTimeValidations) {
        List<ISpellValidator> validators = new LinkedList<>();

        // Unconditional validators
        validators.add(MAX_ONE_CAST_METHOD);
        validators.add(GLYPH_OCCURRENCES_POLICY);
        validators.add(EFFECT_AUGMENTATION_POLICY);

        // Validators only applicable at crafting time
        if (!enforceCastTimeValidations) {
            // Not enforcing this at cast time so we don't break existing spells with ineffective augments
            validators.add(AUGMENT_COMPATIBILITY);
        }

        // Validators only applicable at casting time
        if (enforceCastTimeValidations) {
            validators.add(NON_EMPTY_SPELL);
            validators.add(REQUIRE_CAST_METHOD_START);
        }

        // Combine them all together.
        this.combinedValidator = new CombinedSpellValidator(validators);
    }

    @Override
    public List<SpellValidationError> validate(List<AbstractSpellPart> spellRecipe) {
        return combinedValidator.validate(spellRecipe);
    }
}
