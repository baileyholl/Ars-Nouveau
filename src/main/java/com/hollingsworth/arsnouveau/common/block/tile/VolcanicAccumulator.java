package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ModBlock;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class VolcanicAccumulator extends ModBlock {
    public VolcanicAccumulator() {
        super(LibBlockNames.VOLCANIC_ACCUMULATOR);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        super.randomTick(state, worldIn, pos, random);
        VolcanicTile tile = (VolcanicTile) worldIn.getTileEntity(pos);
        if(tile == null)
            return;
        tile.doRandomAction();
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new VolcanicTile();
    }
}
