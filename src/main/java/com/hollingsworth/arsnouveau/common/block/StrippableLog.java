package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;

import java.util.function.Supplier;

public class StrippableLog extends RotatedPillarBlock {
    Supplier<Block> strippedState;
    public StrippableLog(Properties properties, String registryName, Supplier<Block> stateSupplier) {
        super(properties);
        this.strippedState = stateSupplier;
        setRegistryName(registryName);
    }

//TODO: Restore modified state
//    @Override
//    public BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolType toolType) {
//        if (toolType == ToolType.AXE) return strippedState.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
//        return null;
//    }
}
