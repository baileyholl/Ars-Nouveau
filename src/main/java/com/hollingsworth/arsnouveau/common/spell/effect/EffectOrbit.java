package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityOrbitProjectile;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectOrbit extends AbstractEffect {
    public static EffectOrbit INSTANCE = new EffectOrbit();

    private EffectOrbit() {
        super(GlyphLib.EffectOrbitID, "Orbit");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        if (spellContext.getRemainingSpell().isEmpty()) return;
        int total = 3 + stats.getBuffCount(AugmentSplit.INSTANCE);
        SpellContext newContext = resolver.spellContext.makeChildContext();
        Spell spell = newContext.getSpell();
        spell.recipe.add(0, MethodProjectile.INSTANCE);
        spellContext.setCanceled(true);
        for (int i = 0; i < total; i++) {
            EntityOrbitProjectile wardProjectile = new EntityOrbitProjectile(world, resolver.getNewResolver(newContext), rayTraceResult.getLocation());
            wardProjectile.setOffset(i);
            wardProjectile.setAccelerates((int) stats.getAccMultiplier());
            wardProjectile.setAoe((float) stats.getAoeMultiplier());
            wardProjectile.extendTimes = (int) stats.getDurationMultiplier();
            wardProjectile.setTotal(total);
            wardProjectile.setColor(resolver.spellContext.getColors());
            world.addFreshEntity(wardProjectile);
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        if (spellContext.getRemainingSpell().isEmpty()) return;
        int total = 3 + stats.getBuffCount(AugmentSplit.INSTANCE);
        Spell newSpell = spellContext.getRemainingSpell();
        newSpell.recipe.add(0, MethodProjectile.INSTANCE);
        SpellContext newContext = resolver.spellContext.makeChildContext().withSpell(newSpell);
        spellContext.setCanceled(true);
        for (int i = 0; i < total; i++) {
            EntityOrbitProjectile wardProjectile = new EntityOrbitProjectile(world, resolver.getNewResolver(newContext), rayTraceResult.getEntity());
            wardProjectile.setOffset(i);
            wardProjectile.setAccelerates((int) stats.getAccMultiplier());
            wardProjectile.setAoe((float) stats.getAoeMultiplier());
            wardProjectile.extendTimes = (int) stats.getDurationMultiplier();
            wardProjectile.setTotal(total);
            wardProjectile.setColor(resolver.spellContext.getColors());
            world.addFreshEntity(wardProjectile);
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    public String getBookDescription() {
        return "Summons three orbiting projectiles around the target that will cast a spell on any entities it may hit. Additional projectiles, their speed, radius, and duration may be augmented. Sensitive will cause Orbit to hit blocks.";
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAccelerate.INSTANCE, AugmentDecelerate.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentExtendTime.INSTANCE,
                AugmentDurationDown.INSTANCE, AugmentSensitive.INSTANCE);
    }
}
