package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class EffectElse extends AbstractEffect {
    public static EffectElse INSTANCE = new EffectElse();
    public EffectElse() {
        super("else", "Else");
    }

    @Override
    public void onContextCanceled(SpellContext context) {
        if(context.canceledAt() instanceof IFilter){
            context.setCanceled(false);
            // Find the first else in the remaining spell and set the current index to it
            Spell remainder = context.getRemainingSpell();
            int index = remainder.recipe.indexOf(EffectElse.INSTANCE);
            context.setCurrentIndex(context.getCurrentIndex() + index + 1);
        }
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
}
