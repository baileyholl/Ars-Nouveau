package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectHarvest extends AbstractEffect {
    public static EffectHarvest INSTANCE = new EffectHarvest();

    private EffectHarvest() {
        super(GlyphLib.EffectHarvestID, "Harvest");
    }

    public static void harvestNetherwart(BlockPos pos, BlockState state, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (state.getValue(NetherWartBlock.AGE) != 3)
            return;
        processAndSpawnDrops(pos, state, world, shooter, spellStats, spellContext);
        world.setBlockAndUpdate(pos, state.setValue(NetherWartBlock.AGE, 0));
    }

    public static void processAndSpawnDrops(BlockPos pos, BlockState state, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        List<ItemStack> cropDrops = Block.getDrops(state, (ServerLevel) world, pos, world.getBlockEntity(pos));
        if (spellStats.hasBuff(AugmentFortune.INSTANCE)) {
            cropDrops = state.getDrops(LootUtil.getFortuneContext((ServerLevel) world, pos, shooter, spellStats.getBuffCount(AugmentFortune.INSTANCE)));
        }
        for (ItemStack i : cropDrops) {
            if (i.getItem() instanceof BlockItem && ((BlockItem) i.getItem()).getBlock() == state.getBlock()) {
                i.shrink(1);
                break;
            }
        }
        cropDrops.forEach(d -> {
            if (d.isEmpty() || d.getItem() == BlockRegistry.MAGE_BLOOM_CROP.asItem()) {
                return;
            }
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), d));
        });
    }

    @Override
    public void onResolveBlock(BlockHitResult ray, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        for (BlockPos blockpos : SpellUtil.calcAOEBlocks(shooter, ray.getBlockPos(), ray, spellStats)) {
            BlockState state = world.getBlockState(blockpos);

            if (state.getBlock() instanceof FarmBlock || world.getBlockState(blockpos.above()).getBlock() instanceof CropBlock || world.getBlockState(blockpos.above()).getBlock() instanceof NetherWartBlock) {
                blockpos = blockpos.above();
                state = world.getBlockState(blockpos);
            }
            if (state.getBlock() instanceof NetherWartBlock) {
                harvestNetherwart(blockpos, state, world, shooter, spellStats, spellContext);
                continue;
            }

            if (!(state.getBlock() instanceof CropBlock))
                continue;
            CropBlock cropsBlock = (CropBlock) world.getBlockState(blockpos).getBlock();

            if (!cropsBlock.isMaxAge(state) || !(world instanceof ServerLevel))
                continue;

            processAndSpawnDrops(blockpos, state, world, shooter, spellStats, spellContext);
            world.setBlockAndUpdate(blockpos, cropsBlock.getStateForAge(1));
        }
    }


    @Override
    public int getDefaultManaCost() {
        return 10;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentFortune.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "When used on grown crops, this spell will obtain the fully grown product without destroying the plant.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
