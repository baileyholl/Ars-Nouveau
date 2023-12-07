package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class ContextSpellValidator extends ScanningSpellValidator<Stack<AbstractEffect>> {
    private static ConcurrentHashMap.KeySetView<AbstractEffect, Boolean> set = ConcurrentHashMap.newKeySet();

    public static void RegisterContextCreator(AbstractEffect context){
        set.add(context);
    }
    @Override
    protected Stack<AbstractEffect> initContext() {
        return new Stack<AbstractEffect>();
    }

    @Override
    protected void digestSpellPart(Stack<AbstractEffect> context, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors) {
        if(spellPart instanceof AbstractEffect effect) {

            if(effect instanceof IContextManipulator manip){
                if(context.isEmpty()){
                    validationErrors.add(new EmptyManipulationSpellValidationError(position, effect));
                }
                else {
                    IContextManipulator.EscapeResult result = manip.CanEscape(context.peek());
                    if (result == IContextManipulator.EscapeResult.ESCAPE) {
                        context.pop();
                    } else if (result == IContextManipulator.EscapeResult.ERROR) {
                        validationErrors.add(new InvalidManipulationSpellValidationError(position, effect, context.peek()));
                    }
                    //otherwise we ignore the context
                }
            }

            for(ResourceLocation invalidPart : spellPart.invalidNestings.parseComboLimits()){
                AbstractSpellPart offendingPart = GlyphRegistry.getSpellPart(invalidPart);
                if(offendingPart == null)
                    continue;
                if(context.contains(offendingPart)){
                    LoggerContext.getContext().getLogger(ContextSpellValidator.class).info("context has invalid part");
                    validationErrors.add(new InvalidNestingValidationError(position, spellPart, offendingPart));
                }else if(offendingPart.invalidNestings.contains(spellPart.getRegistryName()) && offendingPart != spellPart){
                    LoggerContext.getContext().getLogger(ContextSpellValidator.class).info("offending part has spell part");
                    validationErrors.add(new InvalidNestingValidationError(position, offendingPart, spellPart));
                }
            }

            for(AbstractEffect other : context){
                AbstractSpellPart part = GlyphRegistry.getSpellPart(other.getRegistryName());
                if(part == null)
                    return;
                if(part.invalidNestings.contains(spellPart.getRegistryName())){
                    LoggerContext.getContext().getLogger(ContextSpellValidator.class).info("context has part that has us");
                    validationErrors.add(new InvalidNestingValidationError(position, part, spellPart));
                }
            }

            //push after validating so a glyph can be an invalid nesting with itself
            if (set.contains(effect)){
                context.push(effect);//push context onto the stack
            }
        }
    }

    @Override
    protected void finish(Stack<AbstractEffect>context, List<SpellValidationError> validationErrors) {

    }

    private static class InvalidNestingValidationError extends BaseSpellValidationError {
        public InvalidNestingValidationError(int position, AbstractSpellPart part, AbstractSpellPart other) {
            super(position, part, "invalid_nesting_policy", part, other);
        }
    }
    private static class EmptyManipulationSpellValidationError extends BaseSpellValidationError {
        public EmptyManipulationSpellValidationError(int position, AbstractSpellPart part) {
            super(position, part, "empty_manipulation", part);
        }
    }
    private static class InvalidManipulationSpellValidationError extends BaseSpellValidationError {
        public InvalidManipulationSpellValidationError(int position, AbstractSpellPart part, AbstractSpellPart other) {
            super(position, part, "invalid_manipulation", part, other);
        }
    }
}