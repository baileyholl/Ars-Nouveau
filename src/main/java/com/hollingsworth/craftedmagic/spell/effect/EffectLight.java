package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.api.util.SpellUtil;
import com.hollingsworth.craftedmagic.block.ModBlocks;
import com.hollingsworth.craftedmagic.spell.augment.AugmentAOE;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectLight extends AbstractEffect {

    public EffectLight() {
        super(ModConfig.EffectLightID, "Light");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            applyPotion((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity(), Effects.NIGHT_VISION, augments);
        }

        if(rayTraceResult instanceof BlockRayTraceResult){
            BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getPos().offset(((BlockRayTraceResult) rayTraceResult).getFace());
            if (world.getBlockState(pos).getMaterial() == Material.AIR && world.func_217350_a(ModBlocks.lightBlock.getDefaultState(), pos, ISelectionContext.dummy())) {
                world.setBlockState(pos, ModBlocks.lightBlock.getDefaultState());
            }

        }
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public int getManaCost() {
        return 25;
    }
}
