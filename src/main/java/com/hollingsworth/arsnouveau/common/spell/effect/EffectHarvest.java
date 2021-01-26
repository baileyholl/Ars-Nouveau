package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
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

import javax.annotation.Nullable;
import java.util.List;

public class EffectHarvest extends AbstractEffect {

    public EffectHarvest() {
        super(ModConfig.EffectHarvestID, "Harvest");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof BlockRayTraceResult){
            BlockRayTraceResult ray = (BlockRayTraceResult) rayTraceResult;
            if(world.isRemote)
                return;
            for(BlockPos blockpos : SpellUtil.calcAOEBlocks(shooter, ray.getPos(), ray, getBuffCount(augments, AugmentAOE.class))){
                BlockState state = world.getBlockState(blockpos);

                if(state.getBlock() instanceof FarmlandBlock || world.getBlockState(blockpos.up()).getBlock() instanceof CropsBlock){
                    blockpos = blockpos.up();
                    state = world.getBlockState(blockpos);
                }

                if(!(state.getBlock() instanceof CropsBlock))
                    continue;
                CropsBlock cropsBlock = (CropsBlock)world.getBlockState(blockpos).getBlock();

                if(!cropsBlock.isMaxAge(state) || !(world instanceof ServerWorld))
                    continue;

                List<ItemStack> cropDrops = Block.getDrops(state, (ServerWorld)world, blockpos, world.getTileEntity(blockpos));

                if(hasBuff(augments, AugmentFortune.class)){
                    cropDrops = state.getDrops(LootUtil.getFortuneContext((ServerWorld) world, blockpos, shooter, getBuffCount(augments, AugmentFortune.class)));
                }
                BlockPos finalBlockpos = blockpos;
                cropDrops.forEach(d -> {
                    if(d.getItem() == BlockRegistry.MANA_BLOOM_CROP.asItem()){
                        return;
                    }
                    world.addEntity(new ItemEntity(world, finalBlockpos.getX(), finalBlockpos.getY(), finalBlockpos.getZ(), d));
                });
                world.setBlockState(blockpos,cropsBlock.withAge(1));
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(!(rayTraceResult instanceof BlockRayTraceResult))
            return false;

        BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getPos();
        BlockState state = world.getBlockState(pos);

        if(state.getBlock() instanceof FarmlandBlock || world.getBlockState(pos.up()).getBlock() instanceof CropsBlock){
            pos = pos.up();
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

    @Override
    protected String getBookDescription() {
        return "When used on grown crops, this spell will obtain the fully grown product without destroying the plant.";
    }
}
