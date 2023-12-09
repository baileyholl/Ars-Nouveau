package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EffectPopContext extends AbstractEffect implements IContextManipulator{
    public static EffectPopContext INSTANCE = new EffectPopContext();
    public EffectPopContext() {
        super("escape_context", "Escape Context");
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

        SpellContext newContext;

        if(passedFilter){
            //regular inner context
            newContext = context.clone().withSpell(remainder.clone().setRecipe(new ArrayList<>(remainder.recipe.subList(0, index))));
        }
        else{
            //didn't pass filter so send empty context
            newContext = context.clone().withSpell(new Spell());
            newContext.setCanceled(true);
        }

        //since index comes from the remaining spell, it is an offset on the total index
        context.setCurrentIndex(context.getCurrentIndex() + index + 1);
        return newContext;
    }
}
