package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.AnimBlockSummon;
import com.hollingsworth.arsnouveau.common.entity.AnimHeadSummon;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectAnimate extends AbstractEffect {

    public static EffectAnimate INSTANCE = new EffectAnimate();

    public EffectAnimate() {
        super(GlyphLib.EffectAnimateID, "Animate Block");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (EnchantedFallingBlock.canFall(world, pos, shooter, spellStats)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            animateBlock(rayTraceResult, new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), world, shooter, spellStats, spellContext, resolver, state, blockEntity == null ? new CompoundTag() : blockEntity.saveWithoutMetadata());
            world.setBlock(pos, state.getFluidState().createLegacyBlock(), 3);
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof EnchantedFallingBlock fallingBlock && !fallingBlock.isRemoved()) {
            AnimBlockSummon summon = animateBlock(rayTraceResult, fallingBlock.position, world, shooter, spellStats, spellContext, resolver, fallingBlock.getBlockState(), fallingBlock.blockData);
            summon.setDeltaMovement(fallingBlock.getDeltaMovement());
            summon.hurtMarked = true;
            summon.fallDistance = 0.0f;
            fallingBlock.discard();
        }
    }

    private AnimBlockSummon animateBlock(HitResult rayTraceResult, Vec3 pos, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, BlockState state, CompoundTag data) {
        AnimBlockSummon blockSummon = state.getBlock() instanceof AbstractSkullBlock ? new AnimHeadSummon(world, state, data) : new AnimBlockSummon(world, state);
        blockSummon.setColor(spellContext.getColors().getColor());
        blockSummon.setPos(pos);
        int ticks = (int) (20 * (GENERIC_INT.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier()));
        blockSummon.setTicksLeft(ticks);
        blockSummon.setTarget(shooter.getLastHurtMob());
        blockSummon.setAggressive(true);
        blockSummon.setTame(true);
        if (shooter instanceof Player player) blockSummon.tame(player);
        summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver, blockSummon);
        return blockSummon;
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 60, "Base duration in seconds", "duration");
        addExtendTimeConfig(builder, 60);
    }

    @Override
    public int getDefaultManaCost() {
        return 200;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Animates a block to fight for you. The animated block will convert to a falling block when it dies. Costs a large amount of mana, but does not grant Summoning Sickness";
    }
    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return getSummonAugments();
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION, SpellSchools.CONJURATION);
    }
}
