package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.TempLightTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TempLightBlock extends LightBlock {


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TempLightTile(pos, state);
    }

}
