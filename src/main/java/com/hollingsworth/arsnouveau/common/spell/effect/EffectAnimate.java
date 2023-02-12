package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.AnimBlockSummon;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectAnimate extends AbstractEffect {

    public static EffectAnimate INSTANCE = new EffectAnimate();

    public EffectAnimate() {
        super(GlyphLib.EffectAnimateID, "Animate Block");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockState state = world.getBlockState(rayTraceResult.getBlockPos());
        if (EnchantedFallingBlock.canFall(world, rayTraceResult.getBlockPos(), shooter, spellStats)) {
            animateBlock(rayTraceResult, rayTraceResult.getLocation(), world, shooter, spellStats, spellContext, state);
            world.setBlock(rayTraceResult.getBlockPos(), state.getFluidState().createLegacyBlock(), 3);
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof EnchantedFallingBlock fallingBlock && !fallingBlock.isRemoved()){
            AnimBlockSummon summon = animateBlock(rayTraceResult, fallingBlock.position, world, shooter, spellStats, spellContext, fallingBlock.getBlockState());
            summon.setDeltaMovement(fallingBlock.getDeltaMovement());
            summon.hurtMarked = true;
            summon.fallDistance = 0.0f;
            fallingBlock.discard();
        }
    }

    private AnimBlockSummon animateBlock(HitResult rayTraceResult, Vec3 pos, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, BlockState state) {
        AnimBlockSummon blockSummon = new AnimBlockSummon(world, state);
        blockSummon.setColor(spellContext.getColors().getColor());
        blockSummon.setPos(pos);
        int ticks = (int) (20 * (GENERIC_INT.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier()));
        blockSummon.setTicksLeft(ticks);
        blockSummon.setTarget(shooter.getLastHurtMob());
        blockSummon.setAggressive(true);
        blockSummon.setTame(true);
        if (shooter instanceof Player player) blockSummon.tame(player);
        summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, blockSummon);
        return blockSummon;
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 60, "Base duration in seconds", "duration");
        addExtendTimeConfig(builder, 60);
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Animate a block to fight for you.";
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
