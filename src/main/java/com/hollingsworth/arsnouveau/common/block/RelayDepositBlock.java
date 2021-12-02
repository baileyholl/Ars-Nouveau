package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RelayDepositTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

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
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new RelayDepositTile();
    }
}
