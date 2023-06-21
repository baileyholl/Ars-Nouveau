package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;

public class ModBlock extends Block {

    public ModBlock(Properties properties) {
        super(properties);
    }

    public ModBlock() {
        super(defaultProperties());
    }

    public static Block.Properties defaultProperties() {
        return Block.Properties.of().sound(SoundType.STONE).strength(2.0f, 6.0f).mapColor(MapColor.STONE);
    }

    @Override
    public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
        return true;
    }
}
