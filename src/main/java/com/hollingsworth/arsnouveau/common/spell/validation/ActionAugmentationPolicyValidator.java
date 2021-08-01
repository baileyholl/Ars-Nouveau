package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.setup.Config;

import java.util.List;

/**
 * Spell validation that enforces the augmentation limits per action (cast method or effect) set out in the configuration.
 */
public class ActionAugmentationPolicyValidator extends SpellPhraseValidator {
    @Override
    protected void validatePhrase(SpellPhrase phrase, List<SpellValidationError> errors) {
        // No validation of augments without the effect present
        AbstractSpellPart action = phrase.getAction();
        if (action != null) {
            phrase.getAugmentPositionMap().forEach((augmentTag, augments) -> {
                int limit = action.getAugmentLimit(augmentTag);
                if (augments.size() > limit) {
                    // Iterate, but skip until the limit, then mark the rest.
                    for (int i = limit; i < augments.size(); i++) {
                        errors.add(new ActionAugmentationPolicyValidationError(
                                augments.get(i).position,
                                augments.get(i).spellPart,
                                action,
                                limit
                        ));
                    }
                }
            });
        }
    }

    private static class ActionAugmentationPolicyValidationError extends BaseSpellValidationError {
        public ActionAugmentationPolicyValidationError(int position, AbstractAugment augment, AbstractSpellPart action, int limit) {
            super(
                    position,
                    augment,
                    limit <= 0 ? "action_augmentation_policy.zero" : "action_augmentation_policy",
                    action,
                    augment
            );
        }
    }
}
