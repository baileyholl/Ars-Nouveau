package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RelayWarpTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RelayWarpTile(pos, state);
    }
}
