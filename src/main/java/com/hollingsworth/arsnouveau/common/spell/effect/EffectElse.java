package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class EffectElse extends AbstractEffect implements IConditionalContextEscape {

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
    public Spell getSecondConditionSpell(SpellContext context, int posInRecipe) {
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
                    pushCount +=1;
                }
            }
            else if (part instanceof IContextEscape escape){
                pushCount = escape.pop(pushCount, context, contextPos);
                if(pushCount < 0){
                    break;
                }
            }

            contextPos +=1;

        }

        return context.getSpell().clone().setRecipe(new ArrayList<>(context.getSpell().recipe.subList(elsePos, contextPos)));

    }

    @Override
    public Spell getPostContextSpell(SpellContext context, int posInRecipe) {
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
                    pushCount +=1;
                }
            }
            else if (part instanceof IContextEscape escape){
                pushCount = escape.pop(pushCount, context, contextPos);
                if(pushCount < 0){
                    break;
                }
            }

            contextPos +=1;

        }

        return context.getSpell().clone().setRecipe(new ArrayList<>(context.getSpell().recipe.subList(contextPos,context.getSpell().recipe.size())));
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
    public void pop(Stack<AbstractSpellPart> stack, AbstractSpellPart part, int posInRecipe, List<SpellValidationError> validationErrors) {
        //intentionally left blank so that the stack isn't modified
    }
}
