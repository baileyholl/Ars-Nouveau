package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectGrow extends AbstractEffect {
    public static EffectGrow INSTANCE = new EffectGrow();

    private EffectGrow() {
        super(GlyphLib.EffectGrowID, "Grow");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        for (BlockPos blockpos : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats)) {
            ItemStack stack = new ItemStack(Items.BONE_MEAL, 64);
            if (BlockUtil.destroyRespectsClaim(shooter, world, blockpos) && world instanceof ServerLevel serverLevel) {
                if (BoneMealItem.applyBonemeal(stack, world, blockpos, ANFakePlayer.getPlayer(serverLevel))) {
                    if (!world.isClientSide) {
                        world.levelEvent(1505, blockpos, 0); //particles
                    }
                } else {
                    BlockPos relative = blockpos.relative(rayTraceResult.getDirection());
                    boolean flag = world.getBlockState(blockpos).isFaceSturdy(world, blockpos, rayTraceResult.getDirection());
                    if (flag && BoneMealItem.growWaterPlant(stack, world, relative, rayTraceResult.getDirection())) {
                        if (!world.isClientSide) {
                            world.levelEvent(1505, relative, 0); //particles
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 70;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
    }

    @Override
    public String getBookDescription() {
        return "Causes plants to accelerate in growth as if they were bonemealed.";
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
