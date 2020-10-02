package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.CrystallizerTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class CrystallizerBlock extends ModBlock{
    public CrystallizerBlock() {
        super(defaultProperties().notSolid(), LibBlockNames.CRYSTALLIZER);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CrystallizerTile();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
