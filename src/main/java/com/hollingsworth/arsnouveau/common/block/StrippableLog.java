package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;

import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class StrippableLog extends RotatedPillarBlock {
    Supplier<Block> strippedState;
    public StrippableLog(Properties properties, String registryName, Supplier<Block> stateSupplier) {
        super(properties);
        this.strippedState = stateSupplier;
        setRegistryName(registryName);
    }


    @Override
    public BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolType toolType) {
        if (toolType == ToolType.AXE) return strippedState.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
        return null;
    }
}
