package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            int duration = 3 + 2*getBuffCount(augments, AugmentExtendTime.class);
            ((EntityRayTraceResult) rayTraceResult).getEntity().setFire(duration);
        }else if(rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getPos().up()).getMaterial() == Material.AIR){
            for(BlockPos pos : SpellUtil.calcAOEBlocks( shooter, ((BlockRayTraceResult) rayTraceResult).getPos(), (BlockRayTraceResult)rayTraceResult, getBuffCount(augments, AugmentAOE.class))) {
                if(world.getBlockState(pos.up()).getMaterial() == Material.AIR)
                    world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
            }
        }
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
