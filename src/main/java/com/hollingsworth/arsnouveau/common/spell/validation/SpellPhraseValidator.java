package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A type of spell validator that validates a spell as a set of individual <em>phrases</em> where a spell phrase is any
 * action (cast form or effect) modified by a series of augments.
 *
 * Implementations of this class should be warned that positions may be <code>null</code>, which occurs while crafting
 * spells if there are gaps in the UI.
 *
 * The augment position map will exclude empty slots.
 */
public abstract class SpellPhraseValidator extends AbstractSpellValidator {
    /**
     * Implemented to define the per-phrase validation logic.
     *
     * @param phrase the spell phrase to validate
     * @param validationErrors a list the validator may add new errors to. Like <code>context</code>, this list is
     *                         private to the specific validator, allowing steps to remove or change existing errors if
     *                         desired.
     */
    protected abstract void validatePhrase(SpellPhrase phrase, List<SpellValidationError> validationErrors);

    @Override
    protected void validateImpl(List<AbstractSpellPart> spellRecipe, List<SpellValidationError> validationErrors) {
        List<SpellPhrase> phrases = splitSpellIntoPhrases(spellRecipe);

        for (SpellPhrase phrase : phrases) {
            validatePhrase(phrase, validationErrors);
        }
    }

    /**
     * Creates a list of SpellValidationPhrases from a spell recipe.
     *
     * @param recipe the spell recipe to break into phrases.
     */
    public static List<SpellPhrase> splitSpellIntoPhrases(List<AbstractSpellPart> recipe) {
        List<SpellPhrase> phrases = new LinkedList<>();

        // Initialize the accumulators
        AbstractSpellPart action = null;
        List<AbstractAugment> augments = new ArrayList<>();
        Map<String, List<SpellPhrase.SpellPartPosition<AbstractAugment>>> augmentPositionMap = new LinkedHashMap<>();
        int phraseStart = 0;

        // Walk through the recipe
        for (int pos = 0; pos < recipe.size(); pos++) {
            AbstractSpellPart currentPart = recipe.get(pos);
            if (currentPart instanceof AbstractAugment) {
                // Still in a phrase, add to the augment list
                augments.add((AbstractAugment) currentPart);

                // Note the position as well for easy lookup in the phrase validator
                if (!augmentPositionMap.containsKey(currentPart.tag)) {
                    augmentPositionMap.put(currentPart.tag, new LinkedList<>());
                }
                augmentPositionMap.get(currentPart.tag).add(new SpellPhrase.SpellPartPosition<>((AbstractAugment) currentPart, pos));
            } else if (currentPart != null) {
                // Action changed, wrap up the current phrase and add it to the list
                phrases.add(new SpellPhrase(action, augments, augmentPositionMap, phraseStart));

                // Reset the accumulators
                action = currentPart;
                augments = new ArrayList<>();
                augmentPositionMap = new LinkedHashMap<>();
                phraseStart = pos;
            }
        }
        // Be sure to grab the last one
        phrases.add(new SpellPhrase(action, augments, augmentPositionMap, phraseStart));

        return phrases;
    }

    /**
     * Class representing a single <em>phrase</em> of a spell. A phrase is either a cast method or an effect, followed
     * by zero or more augments.
     */
    public static class SpellPhrase {
        private final int firstPosition;
        private final AbstractSpellPart action;
        private final List<AbstractAugment> augments;
        private final Map<String, List<SpellPartPosition<AbstractAugment>>> augmentPositionMap;

        /**
         * Create a new phrase that contains an action (a cast method or an effect) followed by zero or more augments.
         *
         * @param action             the action that starts this phrase. If null, this phrase has no action. This is valid
         *                           for spells in the spell book that are intended to be applied to an item that has a cast
         *                           method.
         * @param augments           a list of augment spell parts that make up the rest of the phrase.
         * @param augmentPositionMap a map from augment tags to a list of pairs of spell parts and their positions within
         *                           the overall spell.
         * @param firstPosition      the position (0 index) of the first action glyph within the overall spell recipe.
         */
        private SpellPhrase(@Nullable AbstractSpellPart action, List<AbstractAugment> augments, Map<String, List<SpellPartPosition<AbstractAugment>>> augmentPositionMap, int firstPosition) {
            this.firstPosition = firstPosition;
            this.action = action;
            this.augments = augments;
            this.augmentPositionMap = augmentPositionMap;
        }

        public static final class SpellPartPosition<T extends AbstractSpellPart> {
            final T spellPart;
            final int position;

            public SpellPartPosition(T spellPart, int position) {
                this.spellPart = spellPart;
                this.position = position;
            }
        }

        /**
         * Returns the action that begins this phrase, if present.
         */
        @Nullable
        public AbstractSpellPart getAction() {
            return action;
        }

        /**
         * Returns the raw list of augments that make up this phrase.
         */
        public List<AbstractAugment> getAugments() {
            return augments;
        }

        /**
         * Returns the (0 based) index of the first glyph of this phrase within the overall spell recipe
         */
        public int getFirstPosition() {
            return firstPosition;
        }

        /**
         * Returns a map from augment tags to a list of pairs of {@link AbstractAugment} and their positions in the overall
         * spell being validated. The contained lists will always be in ascending position order.
         */
        public Map<String, List<SpellPartPosition<AbstractAugment>>> getAugmentPositionMap() {
            return augmentPositionMap;
        }
    }
}
