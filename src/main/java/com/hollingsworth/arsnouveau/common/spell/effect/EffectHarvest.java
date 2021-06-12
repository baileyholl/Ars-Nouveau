package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectHarvest extends AbstractEffect {
    public static EffectHarvest INSTANCE = new EffectHarvest();

    private EffectHarvest() {
        super(GlyphLib.EffectHarvestID, "Harvest");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof BlockRayTraceResult){
            BlockRayTraceResult ray = (BlockRayTraceResult) rayTraceResult;
            if(world.isClientSide)
                return;
            for(BlockPos blockpos : SpellUtil.calcAOEBlocks(shooter, ray.getBlockPos(), ray, getBuffCount(augments, AugmentAOE.class), getBuffCount(augments, AugmentPierce.class))){
                BlockState state = world.getBlockState(blockpos);

                if(state.getBlock() instanceof FarmlandBlock || world.getBlockState(blockpos.above()).getBlock() instanceof CropsBlock){
                    blockpos = blockpos.above();
                    state = world.getBlockState(blockpos);
                }

                if(!(state.getBlock() instanceof CropsBlock))
                    continue;
                CropsBlock cropsBlock = (CropsBlock)world.getBlockState(blockpos).getBlock();

                if(!cropsBlock.isMaxAge(state) || !(world instanceof ServerWorld))
                    continue;

                List<ItemStack> cropDrops = Block.getDrops(state, (ServerWorld)world, blockpos, world.getBlockEntity(blockpos));

                if(hasBuff(augments, AugmentFortune.class)){
                    cropDrops = state.getDrops(LootUtil.getFortuneContext((ServerWorld) world, blockpos, shooter, getBuffCount(augments, AugmentFortune.class)));
                }
                BlockPos finalBlockpos = blockpos;
                cropDrops.forEach(d -> {
                    if(d.getItem() == BlockRegistry.MANA_BLOOM_CROP.asItem()){
                        return;
                    }
                    world.addFreshEntity(new ItemEntity(world, finalBlockpos.getX(), finalBlockpos.getY(), finalBlockpos.getZ(), d));
                });
                world.setBlockAndUpdate(blockpos,cropsBlock.getStateForAge(1));
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(!(rayTraceResult instanceof BlockRayTraceResult))
            return false;

        BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getBlockPos();
        BlockState state = world.getBlockState(pos);

        if(state.getBlock() instanceof FarmlandBlock || world.getBlockState(pos.above()).getBlock() instanceof CropsBlock){
            pos = pos.above();
            state = world.getBlockState(pos);
        }
        if(!(state.getBlock() instanceof CropsBlock))
            return false;


        CropsBlock cropsBlock = (CropsBlock)world.getBlockState(pos).getBlock();
        return cropsBlock.isMaxAge(state) && world instanceof ServerWorld;
    }


    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.IRON_HOE;
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentFortune.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "When used on grown crops, this spell will obtain the fully grown product without destroying the plant.";
    }
}
