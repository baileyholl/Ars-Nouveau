package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.api.util.SpellUtil;
import com.hollingsworth.craftedmagic.block.ModBlocks;
import com.hollingsworth.craftedmagic.spell.augment.AugmentAOE;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectPhantomBlock extends AbstractEffect {

    public EffectPhantomBlock() {
        super(ModConfig.EffectPhantomBlockID, "Phantom");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(rayTraceResult instanceof BlockRayTraceResult){

            for(BlockPos pos : SpellUtil.calcAOEBlocks((PlayerEntity) shooter, ((BlockRayTraceResult) rayTraceResult).getPos(), (BlockRayTraceResult)rayTraceResult, getBuffCount(augments, AugmentAOE.class))) {
                pos = pos.offset(((BlockRayTraceResult) rayTraceResult).getFace());
                if (world.getBlockState(pos).getMaterial() == Material.AIR && world.func_217350_a(ModBlocks.PHANTOM_BLOCK.getDefaultState(), pos, ISelectionContext.dummy())) {
                    world.setBlockState(pos, ModBlocks.PHANTOM_BLOCK.getDefaultState());
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 5;
    }
}
