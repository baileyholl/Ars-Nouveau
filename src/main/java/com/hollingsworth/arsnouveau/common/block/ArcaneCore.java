package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArcaneCoreTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ArcaneCore extends ModBlock{
    public ArcaneCore() {
        super(defaultProperties().noOcclusion().lightLevel((state) -> 15),LibBlockNames.ARCANE_CORE);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ArcaneCoreTile();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

}
