package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.util.Log;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.LoggerContext;

import java.util.List;
import java.util.Stack;

public class InvalidNestingValidator extends ScanningSpellValidator<Stack<AbstractSpellPart>> {

    @Override
    protected Stack<AbstractSpellPart> initContext() {
        return new Stack<>();
    }

    @Override
    protected void digestSpellPart(Stack<AbstractSpellPart> context, int position, AbstractSpellPart spellPart, List<SpellValidationError> validationErrors) {

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
                context.push(spellPart);
            }
        }
        else if(spellPart instanceof IContextEscape escape){
            if(context.empty()){
                //can't have an escape context without matching context manipulator
                validationErrors.add(new InvalidContextEscapeValidationError(position,spellPart));
            }
            else{
                context.pop();
            }
        }
    }

    @Override
    protected void finish(Stack<AbstractSpellPart> context, List<SpellValidationError> validationErrors) {

    }

    private static class InvalidContextEscapeValidationError extends BaseSpellValidationError{
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
