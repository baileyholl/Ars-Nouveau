package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.VolcanicSourcelinkTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VolcanicSourcelinkBlock extends SourcelinkBlock {

    public VolcanicSourcelinkBlock() {
        super(defaultProperties().noOcclusion().lightLevel(state -> 15));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VolcanicSourcelinkTile(pos, state);
    }

}
