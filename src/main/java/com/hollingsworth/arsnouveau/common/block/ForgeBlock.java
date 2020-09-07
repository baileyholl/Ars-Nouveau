package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ForgeBlock extends ModBlock{
    public ForgeBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return null;
    }
}
