package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class EffectBurst extends AbstractEffect {

    public static final EffectBurst INSTANCE = new EffectBurst();

    public EffectBurst() {
        super(GlyphLib.EffectBurstID, "Burst");
        EffectReset.RESET_LIMITS.add(this);
    }

    @Override
    public String getBookDescription() {
        return "Resolves the spell in a spherical area around the target. Augment with Sensitive to target blocks instead of entities and Dampen to make an empty sphere. Augment with AOE to increase the radius. ";
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        makeSphere(rayTraceResult.getBlockPos(), world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        makeSphere(rayTraceResult.getEntity().blockPosition(), world, shooter, spellStats, spellContext, resolver);
    }

    public void makeSphere(BlockPos center, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (spellContext.getRemainingSpell().isEmpty()) return;

        // like linger, reduce the radius if sensitive
        int radius = (int) spellStats.getAoeMultiplier() + (spellStats.isSensitive() ? 1 : 3);

        Predicate<Double> sphere = spellStats.hasBuff(AugmentDampen.INSTANCE) ? distance -> distance <= radius + 0.5 && distance >= radius - 0.5 : distance -> distance <= radius + 0.5;

        if (spellStats.isSensitive()) {
            for (BlockPos pos : BlockPos.withinManhattan(center, radius, radius, radius)) {
                if (sphere.test(BlockUtil.distanceFromCenter(pos, center))) {
                    pos = pos.immutable();
                    SpellResolver resolver1 = resolver.getNewResolver(spellContext.clone().makeChildContext());
                    //TODO it needs a direction, UP as a dummy for now
                    resolver1.onResolveEffect(world, new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false));
                    ParticleEmitter emitter = resolveEmitter(spellContext, pos.getCenter());
                    emitter.tick(world);
                }
            }
        } else {
            for (Entity entity : world.getEntities(null, new AABB(center).inflate(radius, radius, radius))) {
                if ((entity instanceof LivingEntity || entity.getType().is(EntityTags.BURST_WHITELIST)) && sphere.test(BlockUtil.distanceFromCenter(entity.blockPosition(), center))) {
                    SpellResolver resolver1 = resolver.getNewResolver(spellContext.clone().makeChildContext());
                    resolver1.onResolveEffect(world, new EntityHitResult(entity));
                    ParticleEmitter emitter = resolveEmitter(spellContext, entity.position.add(0, entity.getBbHeight() / 2.0, 0));
                    emitter.tick(world);
                }
            }
        }
        playResolveSound(spellContext, world, center.getCenter());
        spellContext.setCanceled(true);
    }

    public ParticleEmitter resolveEmitter(SpellContext spellContext, Vec3 position) {
        TimelineEntryData entryData = spellContext.getParticleTimeline(ParticleTimelineRegistry.BURST_TIMELINE.get()).onResolvingEffect;
        return createStaticEmitter(entryData, position);
    }

    public void playResolveSound(SpellContext spellContext, Level level, Vec3 position) {
        spellContext.getParticleTimeline(ParticleTimelineRegistry.BURST_TIMELINE.get()).resolveSound.sound.playSound(level, position.x, position.y, position.z);
    }


    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1, 1);
    }

    @Override
    public int getDefaultManaCost() {
        return 500;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the target radius.");
        map.put(AugmentSensitive.INSTANCE, "Targets blocks instead of entities.");
        map.put(AugmentDampen.INSTANCE, "Targets an empty sphere.");
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentSensitive.INSTANCE, AugmentDampen.INSTANCE);
    }

    @Override
    protected void addDefaultInvalidCombos(Set<ResourceLocation> defaults) {
        defaults.add(EffectLinger.INSTANCE.getRegistryName());
        defaults.add(EffectWall.INSTANCE.getRegistryName());
    }
}
