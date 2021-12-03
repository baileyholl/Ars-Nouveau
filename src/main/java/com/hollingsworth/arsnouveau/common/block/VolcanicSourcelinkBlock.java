package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.block.tile.VolcanicSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

public class VolcanicSourcelinkBlock extends SourcelinkBlock {

    public VolcanicSourcelinkBlock() {
        super(defaultProperties().noOcclusion().lightLevel(state -> 15), LibBlockNames.VOLCANIC_ACCUMULATOR);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VolcanicSourcelinkTile(pos, state);
    }

}
