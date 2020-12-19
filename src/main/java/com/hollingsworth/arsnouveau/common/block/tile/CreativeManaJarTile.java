package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;

public class CreativeManaJarTile extends ManaJarTile{

    public CreativeManaJarTile(){
        super(BlockRegistry.CREATIVE_JAR_TILE);
    }
    @Override
    public int getCurrentMana() {
        return this.getMaxMana();
    }
}
