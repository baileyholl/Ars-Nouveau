package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RelayDepositTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RelayDepositBlock extends ArcaneRelay{

    public RelayDepositBlock(String registryName) {
        super(registryName);
    }

    public RelayDepositBlock(){
        super(LibBlockNames.RELAY_DEPOSIT);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RelayDepositTile(pos, state);
    }
}
