package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectColdSnap extends AbstractEffect implements IDamageEffect {

    public static EffectColdSnap INSTANCE = new EffectColdSnap();

    private EffectColdSnap() {
        super(GlyphLib.EffectColdSnapID, "Cold Snap");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(rayTraceResult.getEntity() instanceof LivingEntity livingEntity && world instanceof ServerLevel level))
            return;
        Vec3 vec = safelyGetHitPos(rayTraceResult);
        float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
        double range = 3 + spellStats.getAoeMultiplier();
        int snareSec = (int) (POTION_TIME.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier());

        if (!canDamage(livingEntity))
            return;

        damage(vec, level, shooter, livingEntity, spellStats, spellContext, resolver, snareSec, damage);

        for (LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, new AABB(livingEntity.position().add(range, range, range), livingEntity.position().subtract(range, range, range)))) {
            if (e.equals(livingEntity) || e.equals(shooter))
                continue;
            if (canDamage(e)) {
                vec = e.position();
                damage(vec, level, shooter, e, spellStats, spellContext, resolver, snareSec, damage);
            } else {
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * snareSec, (int) spellStats.getAmpMultiplier()));
            }
        }
    }

    public boolean canDamage(LivingEntity livingEntity) {
        return livingEntity.isInWaterOrRain() || livingEntity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) || livingEntity.getPercentFrozen() > 0.0;
    }

    public void damage(Vec3 vec, ServerLevel world, LivingEntity shooter, LivingEntity livingEntity, SpellStats stats, SpellContext context, SpellResolver resolver, int snareTime, float damage) {
        if (attemptDamage(world, shooter, stats, context, resolver, livingEntity, buildDamageSource(world, shooter), damage)) {
            world.sendParticles(ParticleTypes.SPIT, vec.x, vec.y + 0.5, vec.z, 50,
                    ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), ParticleUtil.inRange(-0.1, 0.1), 0.3);

            livingEntity.addEffect(new MobEffectInstance(ModPotions.SNARE_EFFECT.get(), 20 * snareTime));
        }
    }
    @Override
    public DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        return DamageUtil.source(world, DamageTypesRegistry.COLD_SNAP, shooter == null ? ANFakePlayer.getPlayer((ServerLevel) world) : shooter);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 6.0);
        addAmpConfig(builder, 2.5);
        addPotionConfig(builder, 5);
        addExtendTimeConfig(builder, 1);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentAOE.INSTANCE,
                AugmentFortune.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Snares and causes a burst of damage to an entity that is afflicted by slowness, freezing, or is wet. Nearby enemies that are not afflicted by Slow will be slowed. Nearby Enemies afflicted by Slow or wetness will also be hit by Cold Snap. Can be augmented using Amplify, Extend Time, and AOE.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_WATER);
    }
}