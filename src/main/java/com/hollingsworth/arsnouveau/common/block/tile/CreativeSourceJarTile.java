package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeSourceJarTile extends SourceJarTile {

    public CreativeSourceJarTile(BlockPos pos, BlockState state){
        super(BlockRegistry.CREATIVE_SOURCE_JAR_TILE, pos, state);
    }

    @Override
    public int getCurrentMana() {
        return this.getMaxMana();
    }
}
