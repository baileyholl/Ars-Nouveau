package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.IContextManipulator;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectReset;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvalidCombinationValidator extends ScanningSpellValidator<Map<ResourceLocation, Integer>> {
    @Override
    protected Map<ResourceLocation, Integer> initContext() {
        return new HashMap<>();
    }

    @Override
    protected void digestSpellPart(Map<ResourceLocation, Integer> partCounts, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors) {
        if (spellPart instanceof IContextManipulator manipulator) {
            for (AbstractSpellPart resettable : manipulator.bypassCombinationLimitsFor()) {
                partCounts.remove(resettable.getRegistryName());
            }
        }

        if (partCounts.containsKey(spellPart.getRegistryName())) {
            partCounts.put(spellPart.getRegistryName(), partCounts.get(spellPart.getRegistryName()) + 1);
        } else {
            partCounts.put(spellPart.getRegistryName(), 1);
        }

        for (ResourceLocation invalidPart : spellPart.invalidCombinations.parseComboLimits()) {
            AbstractSpellPart offendingPart = GlyphRegistry.getSpellPart(invalidPart);
            if (offendingPart == null)
                continue;
            if (partCounts.containsKey(invalidPart)) {
                if (EffectReset.RESET_LIMITS.contains(spellPart)) {
                    validationErrors.add(new InvalidCombinationValidator.ResetableInvalidCombinationValidatorError(position, spellPart, offendingPart));
                } else {
                    validationErrors.add(new InvalidCombinationValidator.InvalidCombinationValidatorError(position, spellPart, offendingPart));
                }
            } else if (offendingPart.invalidCombinations.contains(spellPart.getRegistryName())) {
                if (EffectReset.RESET_LIMITS.contains(spellPart)) {
                    validationErrors.add(new InvalidCombinationValidator.ResetableInvalidCombinationValidatorError(position, offendingPart, spellPart));
                } else {
                    validationErrors.add(new InvalidCombinationValidator.InvalidCombinationValidatorError(position, offendingPart, spellPart));
                }
            }
        }
        for (ResourceLocation location : partCounts.keySet()) {
            AbstractSpellPart part = GlyphRegistry.getSpellPart(location);
            if (part == null)
                return;
            if (part.invalidCombinations.contains(spellPart.getRegistryName())) {
                if (EffectReset.RESET_LIMITS.contains(spellPart)) {
                    validationErrors.add(new InvalidCombinationValidator.ResetableInvalidCombinationValidatorError(position, part, spellPart));
                } else {
                    validationErrors.add(new InvalidCombinationValidator.InvalidCombinationValidatorError(position, part, spellPart));
                }
            }
        }
    }

    @Override
    protected void finish(Map<ResourceLocation, Integer> context, List<SpellValidationError> validationErrors) {
    }

    protected static class InvalidCombinationValidatorError extends BaseSpellValidationError {
        public InvalidCombinationValidatorError(int position, AbstractSpellPart part, AbstractSpellPart invalidPart) {
            super(
                    position,
                    part,
                    "invalid_combination_policy",
                    part,
                    invalidPart
            );
        }
    }

    protected static class ResetableInvalidCombinationValidatorError extends BaseSpellValidationError {
        public ResetableInvalidCombinationValidatorError(int position, AbstractSpellPart part, AbstractSpellPart invalidPart) {
            super(
                    position,
                    part,
                    "invalid_combination_reset_policy",
                    part,
                    invalidPart
            );
        }
    }
}
