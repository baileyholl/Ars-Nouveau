package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BookwyrmLecternTile extends SummoningTile implements IWandable {
    int tier;
    int taskIndex;

    public BookwyrmLecternTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.BOOKWYRM_LECTERN_TILE, pos, state);
        tier = 1;
    }

}
