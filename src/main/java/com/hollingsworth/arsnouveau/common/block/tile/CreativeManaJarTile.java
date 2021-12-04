package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeManaJarTile extends ManaJarTile{

    public CreativeManaJarTile(BlockPos pos, BlockState state){
        super(BlockRegistry.CREATIVE_JAR_TILE, pos, state);
    }
    @Override
    public int getCurrentMana() {
        return this.getMaxMana();
    }
}
