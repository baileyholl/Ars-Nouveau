package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentRandomize;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectHeal extends AbstractEffect implements IDamageEffect {
    public static EffectHeal INSTANCE = new EffectHeal();

    private EffectHeal() {
        super(GlyphLib.EffectHealID, "Heal");
    }

    @Override
    public boolean canDamage(LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver, @NotNull Entity entity) {
        return entity instanceof LivingEntity living && living.isInvertedHealAndHarm() && IDamageEffect.super.canDamage(shooter, stats, spellContext, resolver, entity);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity entity && world instanceof ServerLevel serverLevel) {
            if (entity.isRemoved() || entity.getHealth() <= 0)
                return;

            float healVal = (float) (GENERIC_DOUBLE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
            if (entity.isInvertedHealAndHarm()) {
                attemptDamage(world, shooter, spellStats, spellContext, resolver, entity, buildDamageSource(world, shooter), healVal);
            } else {
                if(entity instanceof Player player){
                    player.causeFoodExhaustion(2.5f);
                }
                //apply random here to not apply twice in attemptDamage
                if (spellStats.isRandomized())
                    healVal += randomRolls(spellStats, serverLevel);
                entity.heal(healVal);
            }

        }
    }

    @Override
    public DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        return DamageUtil.source(world, DamageTypes.MAGIC, shooter);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericDouble(builder, 3.0, "Base heal amount", "base_heal");
        addAmpConfig(builder, 3.0);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAmplify.INSTANCE, "Increases the amount of healing done or damage dealt to undead.");
        map.put(AugmentDampen.INSTANCE, "Decreases the amount of healing done or damage dealt to undead.");
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentFortune.INSTANCE, AugmentRandomize.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Heals a small amount of health and consumes hunger from the caster. When used on Undead, the spell will deal an equal amount of magic damage.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ABJURATION);
    }
}
