package com.hollingsworth.arsnouveau.common.spell.rewind;

import com.hollingsworth.arsnouveau.common.entity.EnchantedFallingBlock;
import com.hollingsworth.arsnouveau.common.event.timed.RewindEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockToEntityRewind implements IRewindCallback {
    public BlockPos pos;
    public BlockState state;

    public BlockToEntityRewind(BlockPos pos, BlockState state) {
        this.pos = pos;
        this.state = state;
    }

    @Override
    public void onRewind(RewindEvent event) {
        var entity = event.entity;
        if(entity instanceof EnchantedFallingBlock enchantedFallingBlock && !entity.isRemoved() && state == enchantedFallingBlock.getBlockState()){
            enchantedFallingBlock.groundBlock(true);
        }
    }
}
