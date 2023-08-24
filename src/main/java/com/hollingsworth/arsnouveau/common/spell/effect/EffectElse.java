package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.validation.BaseSpellValidationError;
import com.hollingsworth.arsnouveau.common.spell.validation.InvalidNestingValidator;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class EffectElse extends AbstractEffect implements IConditionalContextEscape, IContextManipulator {

    public static EffectElse INSTANCE = new EffectElse(GlyphLib.EffectElseID,"Else");

    public EffectElse(String tag, String description) {
        super(tag, description);
    }

    @Override
    protected int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Works in combination with filter glyphs to allow casting different effects if the filter fails. An escape context glyph after this will run regardless of the results of the filter. ";
    }

    @Override
    public Spell getSecondConditionSpell(SpellContext context, int posInRecipe) {
        LoggerContext.getContext().getLogger(getClass()).info("second condition spell called");
        int elsePos = posInRecipe;
        if(elsePos > context.getSpell().recipe.size()){
            return context.getSpell().clone().setRecipe(new ArrayList<>());
        }

        int contextPos = elsePos;
        int pushCount = 0;

        while(contextPos < context.getSpell().recipe.size()){
            AbstractSpellPart part = context.getSpell().recipe.get(contextPos);
            if(part instanceof IContextManipulator manip){
                if(manip.isEscapable()){
                    pushCount = manip.push(pushCount,context,contextPos);
                    pushCount +=1;
                }
            }
            else if (part instanceof IContextEscape escape){
                pushCount = escape.pop(pushCount, context, contextPos);
                if(pushCount < 0 || (pushCount == 0 && escape.shouldProvideSpell(context,contextPos))){
                    break;
                }
            }

            contextPos +=1;

        }

        return context.getSpell().clone().setRecipe(new ArrayList<>(context.getSpell().recipe.subList(elsePos, contextPos)));
    }

    @Override
    public Spell getPostContextSpell(SpellContext context, int posInRecipe) {
        LoggerContext.getContext().getLogger(getClass()).info("post context called");
        int elsePos = posInRecipe;
        LoggerContext.getContext().getLogger(getClass()).info("else pos: "+elsePos);
        if(elsePos > context.getSpell().recipe.size()){
            return context.getSpell().clone().setRecipe(new ArrayList<>());
        }

        int contextPos = elsePos;
        int pushCount = 0;

        while(contextPos < context.getSpell().recipe.size()){
            AbstractSpellPart part = context.getSpell().recipe.get(contextPos);
            if(part instanceof IContextManipulator manip){
                if(manip.isEscapable()){
                    pushCount +=1;
                }
            }
            else if (part instanceof IContextEscape escape){
                pushCount = escape.pop(pushCount, context, contextPos);
                if(pushCount < 0 || (pushCount == 0 && escape.shouldProvideSpell(context,contextPos))){
                    break;
                }
            }

            contextPos +=1;

        }

        LoggerContext.getContext().getLogger(getClass()).info("context pos: "+contextPos);

        Spell value = context.getSpell().clone().setRecipe(new ArrayList<>(context.getSpell().recipe.subList(contextPos,context.getSpell().recipe.size())));
        LoggerContext.getContext().getLogger(getClass()).info("post context returned: "+value.serializeRecipe());
        return value;
    }

    @Override
    public boolean shouldProvideSpell(@Nullable SpellContext context, int posInRecipe) {
        return true;
    }

    @Override
    public int pop(int currentPushCount, @Nullable SpellContext context, int posInRecipe) {
        return currentPushCount;
    }

    @Override
    public boolean isEscapable() {
        return true;
    }

    @Override
    public int push(int currentPushCount, @Nullable SpellContext context, int posInRecipe) {
        return currentPushCount;
    }

    @Override
    public void push(InvalidNestingValidator.NestingContextStack stack, AbstractSpellPart part, int posInRecipe, List<SpellValidationError> validationErrors) {
        if(stack.empty()){
            validationErrors.add(new InvalidNestingValidator.InvalidContextEscapeValidationError(posInRecipe,part));
        }
        else if(stack.peek() instanceof EffectElse){
            validationErrors.add(new BaseSpellValidationError(posInRecipe, part, "ars_nouveau.spell.validation.add_else_to_else"));
        }
        IContextManipulator.super.push(stack, part, posInRecipe, validationErrors);
    }


    @Override
    public void pop(InvalidNestingValidator.NestingContextStack stack, AbstractSpellPart part, int posInRecipe, List<SpellValidationError> validationErrors) {
        //don't pop previous context yet, do it when we are popped
    }


    @Override
    public void onPopped(InvalidNestingValidator.NestingContextStack stack) {
        stack.pop();//pops should pop the original part that this is attached to
    }
}
