package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RelayWarpTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class RelayWarpBlock extends ArcaneRelay{

    public RelayWarpBlock(String registryName) {
        super(registryName);
    }

    public RelayWarpBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    public RelayWarpBlock(){
        super(LibBlockNames.RELAY_WARP);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RelayWarpTile();
    }
}
