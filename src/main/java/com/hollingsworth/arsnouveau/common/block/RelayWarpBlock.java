package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RelayWarpTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

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
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new RelayWarpTile();
    }
}
