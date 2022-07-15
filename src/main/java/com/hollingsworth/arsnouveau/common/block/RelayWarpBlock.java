package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RelayWarpTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RelayWarpBlock extends Relay {

    public RelayWarpBlock(String registryName) {
        super();
    }

    public RelayWarpBlock(Properties properties, String registry) {
        super(properties);
    }

    public RelayWarpBlock() {
        super();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RelayWarpTile(pos, state);
    }
}
