package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.validation.ContextSpellValidator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EffectElse extends AbstractEffect implements IContextManipulator {
    public static EffectElse INSTANCE = new EffectElse();
    public EffectElse() {
        super("else", "Else");
        ArsNouveauAPI.RegisterContextCreator(this);
    }

    @Override
    public boolean CanEscape(AbstractEffect effect){
        if(effect instanceof IFilter) {
            return true;
        }
        return false;
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    protected int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return new HashSet<>();
    }

    @Override
    public SpellContext manipulate(SpellContext context, boolean passedFilter) {
        Spell remainder = context.getRemainingSpell();

        int index = remainder.recipe.indexOf(EffectPopContext.INSTANCE);
        int innerIndex = remainder.recipe.indexOf(EffectElse.INSTANCE);

        SpellContext newContext;

        if(passedFilter){
            //from effect to the else
            newContext = context.clone().withSpell(remainder.clone().setRecipe(new ArrayList<>(remainder.recipe.subList(0, innerIndex))));
        }
        else{
            //from the else to the pop context
            //if there is no pop context, there is nothing to pass here
            int maxIndex = index == -1 ? remainder.recipe.size() : index;
            newContext = context.clone().withSpell(remainder.clone().setRecipe(new ArrayList<>(remainder.recipe.subList(innerIndex, maxIndex))));
        }

        if(index == -1){
            //no other escape context
            context.setCanceled(true);
        }
        else {
            //since index comes from the remaining spell, it is an offset on the total index
            context.setCurrentIndex(context.getCurrentIndex() + index + 1);
        }

        return newContext;
    }
}
