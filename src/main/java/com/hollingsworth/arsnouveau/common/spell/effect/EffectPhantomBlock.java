package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;

import javax.annotation.Nullable;
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
                if (world.getBlockState(pos).getMaterial() == Material.AIR && world.func_217350_a(BlockRegistry.PHANTOM_BLOCK.getDefaultState(), pos, ISelectionContext.dummy())) {
                    world.setBlockState(pos, BlockRegistry.PHANTOM_BLOCK.getDefaultState());
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 5;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.SCAFFOLDING;
    }

    @Override
    protected String getBookDescription() {
        return "Creates a temporary block that will disappear after a short time.";
    }
}
