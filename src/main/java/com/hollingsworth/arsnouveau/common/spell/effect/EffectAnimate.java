package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.AnimBlockSummon;
import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectAnimate extends AbstractEffect {

    public static EffectAnimate INSTANCE = new EffectAnimate();

    public EffectAnimate() {
        super("animate", "Animate Block");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockState state = world.getBlockState(rayTraceResult.getBlockPos());
        if (EnchantedFallingBlock.canFall(world, rayTraceResult.getBlockPos(), shooter, spellStats)) {
            animateBlock(rayTraceResult, world, shooter, spellStats, spellContext, state);
            world.setBlock(rayTraceResult.getBlockPos(), state.getFluidState().createLegacyBlock(), 3);
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof EnchantedFallingBlock fallingBlock){
            animateBlock(rayTraceResult, world, shooter, spellStats, spellContext, fallingBlock.getBlockState());
            fallingBlock.discard();
        }
    }

    private void animateBlock(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, BlockState state) {
        AnimBlockSummon blockSummon = new AnimBlockSummon(world, state);
        blockSummon.setColor(spellContext.getColors().getColor());
        blockSummon.setPos(rayTraceResult.getLocation());
        int ticks = (int) (20 * (GENERIC_INT.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier()));
        blockSummon.setTicksLeft(ticks);
        blockSummon.setTarget(shooter.getLastHurtMob());
        blockSummon.setAggressive(true);
        blockSummon.setTame(true);
        if (shooter instanceof Player player) blockSummon.tame(player);
        summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, blockSummon);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 60, "Base duration in seconds", "duration");
        addExtendTimeConfig(builder, 60);
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Animate a block to fight for you."; //TODO Are these actually even used?
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
