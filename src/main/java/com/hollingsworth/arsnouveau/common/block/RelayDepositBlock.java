package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RelayDepositTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class RelayDepositBlock extends ArcaneRelay{

    public RelayDepositBlock(String registryName) {
        super(registryName);
    }

    public RelayDepositBlock(){
        super(LibBlockNames.RELAY_DEPOSIT);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new RelayDepositTile();
    }
}
