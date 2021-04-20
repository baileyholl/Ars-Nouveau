package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock.Properties;

public class StrippableLog extends RotatedPillarBlock {
    Supplier<Block> strippedState;
    public StrippableLog(Properties properties, String registryName, Supplier<Block> stateSupplier) {
        super(properties);
        this.strippedState = stateSupplier;
        setRegistryName(registryName);
    }


    @Override
    public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
        if (toolType == ToolType.AXE) return strippedState.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
        return null;
    }
}
