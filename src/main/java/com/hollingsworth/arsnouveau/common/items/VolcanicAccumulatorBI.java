package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public class VolcanicAccumulatorBI extends AnimBlockItem{
    public VolcanicAccumulatorBI(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    public InteractionResult useOn(UseOnContext context) {
        return InteractionResult.PASS;
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        BlockHitResult blockraytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.ANY);
        BlockHitResult blockraytraceresult1 = blockraytraceresult.withPosition(blockraytraceresult.getBlockPos().above());
        if(worldIn.getBlockState(blockraytraceresult.getBlockPos()).isAir())
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
        super.useOn(new UseOnContext(playerIn, handIn, blockraytraceresult1));
        return new InteractionResultHolder<>(InteractionResult.FAIL, playerIn.getItemInHand(handIn));
    }
}
