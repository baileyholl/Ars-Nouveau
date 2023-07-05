package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ArchwoodChestTile extends ChestBlockEntity {

    public ArchwoodChestTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCHWOOD_CHEST_TILE.get(), pos, state);
    }

    public ArchwoodChestTile(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
