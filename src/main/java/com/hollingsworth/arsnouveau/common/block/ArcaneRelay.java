package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ArcaneRelayTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class ArcaneRelay extends ModBlock {

    public ArcaneRelay() {
        this(LibBlockNames.ARCANE_RELAY);
    }

    public ArcaneRelay(String registryName){
        this(defaultProperties().lightLevel((blockState) ->8).noOcclusion(), registryName);
    }

    public ArcaneRelay(AbstractBlock.Properties properties, String registryName){
        super(properties, registryName);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ArcaneRelayTile();
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

//    @Override
//    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
//        return Block.box(1D, 1.0D, 1.0D, 15, 15, 15);
//    }
}
