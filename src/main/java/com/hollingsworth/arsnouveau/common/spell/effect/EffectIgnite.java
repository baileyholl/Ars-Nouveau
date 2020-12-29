package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectIgnite  extends AbstractEffect {

    public EffectIgnite() {
        super(ModConfig.EffectIgniteID, "Ignite");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            int duration = 3 + 2*getBuffCount(augments, AugmentExtendTime.class);
            ((EntityRayTraceResult) rayTraceResult).getEntity().setFire(duration);
        }else if(rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getPos().up()).getMaterial() == Material.AIR){
            Direction face = ((BlockRayTraceResult) rayTraceResult).getFace();
            for(BlockPos pos : SpellUtil.calcAOEBlocks( shooter, ((BlockRayTraceResult) rayTraceResult).getPos(), (BlockRayTraceResult)rayTraceResult, getBuffCount(augments, AugmentAOE.class))) {

                BlockPos blockpos1 = pos.offset(face);
                if (AbstractFireBlock.canLightBlock(world, blockpos1, face)) {
                    BlockState blockstate1 = AbstractFireBlock.getFireForPlacement(world, blockpos1);
                    world.setBlockState(blockpos1, blockstate1, 11);
                }
//                if(world.getBlockState(pos.up()).getMaterial() == Material.AIR)
//                    world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return livingEntityHitSuccess(rayTraceResult) || rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getPos().up()).getMaterial() == Material.AIR;
    }

    @Override
    public int getManaCost() {
        return 15;
    }

    @Override
    public Tier getTier() {
        return Tier.ONE;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.FLINT_AND_STEEL;
    }

    @Override
    protected String getBookDescription() {
        return "Sets blocks and mobs on fire for a short time";
    }
}
