package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.RelaySplitterTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RelaySplitter extends Relay {

    public RelaySplitter() {
        super(defaultProperties().lightLevel((state) -> 8).noOcclusion(), LibBlockNames.RELAY_SPLITTER);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RelaySplitterTile(pos, state);
    }
}
