package com.hollingsworth.arsnouveau.common.spell.rewind;

import com.hollingsworth.arsnouveau.common.event.timed.RewindEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EntityToBlockRewind implements IRewindCallback{
    public BlockPos pos;
    public BlockState state;

    public EntityToBlockRewind(BlockPos pos, BlockState state){
        this.pos = pos;
        this.state = state;
    }

    @Override
    public void onRewind(RewindEvent event) {

    }
}
