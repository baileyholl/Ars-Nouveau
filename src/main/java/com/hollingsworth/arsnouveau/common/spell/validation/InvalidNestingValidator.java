package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class InvalidNestingValidator extends ScanningSpellValidator<InvalidNestingValidator.NestingContextStack> {

    @Override
    protected NestingContextStack initContext() {
        return new NestingContextStack();
    }

    public class NestingContextStack extends Stack<AbstractSpellPart>{
        List<SpellValidationError> errors = new ArrayList<>();
        @Override
        public AbstractSpellPart push(AbstractSpellPart item) {
            if(item instanceof IContextManipulator) {
                return super.push(item);
            }
            else{
                throw new IllegalArgumentException("item must extend IContextManipulator");
            }
        }

        @Override
        public synchronized AbstractSpellPart pop() {
            AbstractSpellPart part = super.pop();
            try {
                ((IContextManipulator) part).onPopped(this);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                return part;
            }
        }

        public synchronized AbstractSpellPart popWithoutEffects(){
            return super.pop();
        }

        public void AddValidatorError(SpellValidationError error){
            errors.add(error);
        }
    }

    @Override
    protected void digestSpellPart(NestingContextStack context, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors) {
        for(ResourceLocation invalidPart : spellPart.invalidNestings){
            AbstractSpellPart offendingPart = GlyphRegistry.getSpellPart(invalidPart);
            if(offendingPart == null)
                continue;

            if(context.contains(offendingPart)){
                validationErrors.add(new InvalidNestingCombinationValidationError(position, spellPart, offendingPart));
            }
            else if(offendingPart.invalidNestings.contains(spellPart.getRegistryName()) && offendingPart != spellPart){
                validationErrors.add(new InvalidNestingCombinationValidationError(position, offendingPart,spellPart));
            }
        }

        for(AbstractSpellPart part : context){
            if(part.invalidNestings.contains(spellPart.getRegistryName()) && part != spellPart){
                validationErrors.add(new InvalidNestingCombinationValidationError(position,part,spellPart));
            }
        }
        //we have to add AFTER validation so that a glyph still works if it cannot nest with itself
        //which is most nesting glyphs
        if(spellPart instanceof IContextManipulator manip){
            if(manip.isEscapable()){
                manip.push(context, spellPart, position, validationErrors);
            }
        }
        if(spellPart instanceof IContextEscape escape){
            if(context.empty()){
                //can't have an escape context without matching context manipulator
                validationErrors.add(new InvalidContextEscapeValidationError(position,spellPart));
            }
            else{
                escape.pop(context, spellPart, position, validationErrors);
            }
        }
    }

    @Override
    protected void finish(NestingContextStack context, List<SpellValidationError> validationErrors) {
        validationErrors.addAll(context.errors);
    }

    public static class InvalidContextEscapeValidationError extends BaseSpellValidationError{
        public InvalidContextEscapeValidationError(int position, AbstractSpellPart part) {
            super(
                    position,
                    part,
                    "invalid_context_escape",
                    part
            );
        }
    }

    private static class InvalidNestingCombinationValidationError extends BaseSpellValidationError{
        public InvalidNestingCombinationValidationError(int position, AbstractSpellPart part, AbstractSpellPart incompatPart) {
            super(
                    position,
                    part,
                    "invalid_nesting_combination",
                    part,
                    incompatPart
            );
        }
    }
}
