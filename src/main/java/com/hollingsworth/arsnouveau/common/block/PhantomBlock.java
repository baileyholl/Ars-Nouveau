package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.PhantomBlockTile;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class PhantomBlock extends ModBlock {

    public PhantomBlock() {
        super(defaultProperties().lightValue(7), "phantom_block");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PhantomBlockTile();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canDropFromExplosion(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
        return false;
    }
}

