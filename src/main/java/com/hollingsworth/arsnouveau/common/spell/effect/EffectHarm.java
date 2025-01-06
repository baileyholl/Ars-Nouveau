package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectHarm extends AbstractEffect implements IDamageEffect, IPotionEffect {
    public static EffectHarm INSTANCE = new EffectHarm();

    private EffectHarm() {
        super(GlyphLib.EffectHarmID, "Harm");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level level, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(rayTraceResult.getEntity() instanceof ItemEntity)) {
            double damage = DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
            int time = (int) spellStats.getDurationMultiplier();
            if (time > 0 && rayTraceResult.getEntity() instanceof LivingEntity entity) {
                this.applyConfigPotion(entity, MobEffects.POISON, spellStats);
            } else {
                attemptDamage(level, shooter, spellStats, spellContext, resolver, rayTraceResult.getEntity(), buildDamageSource(level, getPlayer(shooter, (ServerLevel) level)), (float) damage);
            }
        }
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 5.0);
        addAmpConfig(builder, 2.0);
        addPotionConfig(builder, 5);
        addExtendTimeConfig(builder, 5);
    }

    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentFortune.INSTANCE, AugmentRandomize.INSTANCE
        );
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentExtendTime.INSTANCE, "Applies Poison instead of dealing damage, increases the duration.");
        map.put(AugmentDurationDown.INSTANCE, "Decreases the duration of Poison applied.");
        map.put(AugmentAmplify.INSTANCE, "Increases damage dealt, or the level of Poison applied.");
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public String getBookDescription() {
        return "A spell you start with. Damages a target. May be increased by Amplify, or applies the Poison debuff when using Extend Time. Note, multiple Harms without a delay will not apply due to invincibility on hit.";
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 30 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 8 : EXTEND_TIME.get();
    }
}
