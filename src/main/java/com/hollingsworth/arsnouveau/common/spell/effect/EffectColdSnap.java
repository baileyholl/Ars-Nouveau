package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.IceShardEntity;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EffectColdSnap extends AbstractEffect implements IDamageEffect {

    public static EffectColdSnap INSTANCE = new EffectColdSnap();

    private EffectColdSnap() {
        super(GlyphLib.EffectColdSnapID, "Cold Snap");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(rayTraceResult.getEntity() instanceof LivingEntity livingEntity && world instanceof ServerLevel level))
            return;
        Vec3 vec = safelyGetHitPos(rayTraceResult);
        float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
        int snareSec = (int) (POTION_TIME.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier());

        if (!canDamage(livingEntity))
            return;
        this.damage(vec, level, shooter, livingEntity, spellStats, spellContext, resolver, snareSec, damage);
        spawnIce(shooter, level, BlockPos.containing(vec.x, vec.y + (rayTraceResult.getEntity().onGround() ? 1 : 0), vec.z), spellStats, spellContext, resolver);
        if(livingEntity.hasEffect(ModPotions.FREEZING_EFFECT.get())){
            livingEntity.setTicksFrozen(livingEntity.getTicksRequiredToFreeze() + 3);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveBlock(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        if(!world.getBlockState(rayTraceResult.getBlockPos()).is(BlockTags.ICE))
            return;
        world.setBlock(rayTraceResult.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
        spawnIce(shooter, world, rayTraceResult.getBlockPos(), spellStats, spellContext, resolver);
    }

    public void spawnIce(LivingEntity shooter, Level level, BlockPos targetPos, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver){
        Vec3 middleVec = new Vec3(0.2, 0.3, 0.2);
        Vec3 cornerScaleVec = new Vec3(0.1, 0.2, 0.1);
        Set<BlockPos> corners = new HashSet<>();
        Set<BlockPos> sides = new HashSet<>();

        for(int i = 1; i < 2 + spellStats.getAoeMultiplier(); i++) {
            // Middle sides
            sides.addAll(replaceableBetween(level, targetPos.offset(-i, -i, 0), targetPos.offset(-i, i, 0)));
            sides.addAll(replaceableBetween(level, targetPos.offset(i, -i, 0), targetPos.offset(i, i, 0)));
            sides.addAll(replaceableBetween(level, targetPos.offset(0, -i, -i), targetPos.offset(0, i, -i)));
            sides.addAll(replaceableBetween(level, targetPos.offset(0, -i, i), targetPos.offset(0, i, i)));
            // corners
            corners.addAll(replaceableBetween(level, targetPos.offset(-i, -i, -i), targetPos.offset(-i, i, -i)));
            corners.addAll(replaceableBetween(level, targetPos.offset(i, -i, -i), targetPos.offset(i, i, -i)));
            corners.addAll(replaceableBetween(level, targetPos.offset(-i, -i, i), targetPos.offset(-i, i, i)));
            corners.addAll(replaceableBetween(level, targetPos.offset(i, -i, i), targetPos.offset(i, i, i)));

        }
        // top and bottom
        corners.addAll(replaceableBetween(level, targetPos.offset(0, -1, 0), targetPos.offset(0, 1, 0)));

        for (BlockPos pos : corners) {
            if (!level.getBlockState(pos).canBeReplaced()) {
                continue;
            }
            spawnIce(level, pos, targetPos, cornerScaleVec, spellStats, spellContext, resolver, shooter);
        }

        for (BlockPos pos : sides) {
            if (!level.getBlockState(pos).canBeReplaced()) {
                continue;
            }
            spawnIce(level, pos, targetPos, middleVec, spellStats, spellContext, resolver, shooter);
        }
    }

    public void spawnIce(Level level, BlockPos pos, BlockPos targetPos, Vec3 scaleVec, SpellStats spellStats, SpellContext context, SpellResolver resolver, LivingEntity shooter){
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        IceShardEntity fallingBlock = new IceShardEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, Blocks.ICE.defaultBlockState());
        // Send the falling block the opposite direction of the target
        fallingBlock.setDeltaMovement(pos.getX() - targetPos.getX(), pos.getY() - targetPos.getY(), pos.getZ() - targetPos.getZ());
        fallingBlock.setDeltaMovement(fallingBlock.getDeltaMovement().multiply(scaleVec));
        fallingBlock.cancelDrop = true;
        fallingBlock.dropItem = false;
        fallingBlock.hurtEntities = true;
        fallingBlock.baseDamage = ((float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier())) * 0.5f;
        fallingBlock.shooter = shooter;
        level.addFreshEntity(fallingBlock);
        ShapersFocus.tryPropagateEntitySpell(fallingBlock, level, shooter, context, resolver);
    }

    public Set<BlockPos> replaceableBetween(Level level, BlockPos pos1, BlockPos pos2) {
        HashSet<BlockPos> set = new HashSet<>();
        for (BlockPos pos : BlockPos.betweenClosed(pos1, pos2)) {
            if (!level.getBlockState(pos).canBeReplaced()) {
                continue;
            }
            set.add(pos.immutable());
        }
        return set;
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
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 6.0);
        addAmpConfig(builder, 2.5);
        addPotionConfig(builder, 5);
        addExtendTimeConfig(builder, 1);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
        defaults.put(AugmentAOE.INSTANCE.getRegistryName(), 1);
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
                AugmentFortune.INSTANCE, AugmentRandomize.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Causes wet, slowed, or freezing entities to take a burst of damage and erupt into falling ice. Falling ice will slow and damage nearby entities. Can also be used to erupt a block of ice instead. Falling blocks of ice can be manipulated with the Focus of Block Shaping. Entities afflicted with Freezing will be set to the maximum freeze level immediately.";
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