package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RelayCollectorTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RelayCollectorBlock extends Relay {

    public RelayCollectorBlock(String registryName) {
        super(registryName);
    }

    public RelayCollectorBlock(){
        super(LibBlockNames.RELAY_COLLECTOR);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RelayCollectorTile(pos, state);
    }
}
