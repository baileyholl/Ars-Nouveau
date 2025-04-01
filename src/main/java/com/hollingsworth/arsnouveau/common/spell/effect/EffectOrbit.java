package com.hollingsworth.arsnouveau.common.spell.effect;

import com.google.common.collect.ImmutableList;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectOrbit extends AbstractEffect {
    public static EffectOrbit INSTANCE = new EffectOrbit();

    private EffectOrbit() {
        super(GlyphLib.EffectOrbitID, "Orbit");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver) {
        if (!spellContext.hasRemainingSpell()) return;
        int total = 3 + stats.getBuffCount(AugmentSplit.INSTANCE);
        SpellContext newContext = resolver.spellContext.makeChildContext();
        var spell = newContext.getSpell().mutable().add(0, MethodProjectile.INSTANCE);
        newContext.withSpell(spell.immutable());
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
        if (spellContext.hasRemainingSpell()) return;
        int total = 3 + stats.getBuffCount(AugmentSplit.INSTANCE);
        ImmutableList.Builder<AbstractSpellPart> glyphs = ImmutableList.builderWithExpectedSize(1 + spellContext.getUnsafeRemainingSpellRecipeView().size());
        glyphs.add(MethodProjectile.INSTANCE);
        for (var glyph : spellContext.getUnsafeRemainingSpellRecipeView()) {
            glyphs.add(glyph);
        }
        var spell = spellContext.getSpell();
        Spell newSpell = new Spell(spell.name(), spell.color(), spell.sound(), glyphs.build());
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

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAccelerate.INSTANCE, "Increases the speed of the orbiting projectiles.");
        map.put(AugmentDecelerate.INSTANCE, "Decreases the speed of the orbiting projectiles.");
        map.put(AugmentAOE.INSTANCE, "Increases the radius of the orbiting projectiles.");
        map.put(AugmentPierce.INSTANCE, "Allows the orbiting projectiles to pierce through enemies.");
        map.put(AugmentSplit.INSTANCE, "Increases the number of orbiting projectiles.");
        map.put(AugmentExtendTime.INSTANCE, "Increases the duration of the orbiting projectiles.");
        map.put(AugmentDurationDown.INSTANCE, "Decreases the duration of the orbiting projectiles.");
        map.put(AugmentSensitive.INSTANCE, "Allows the orbiting projectiles to hit blocks.");
    }
}
