package com.hollingsworth.craftedmagic.block.tile;

import com.hollingsworth.craftedmagic.block.ManaCondenserBlock;
import com.hollingsworth.craftedmagic.block.BlockRegistry;

public class LightTile extends AnimatedTile {
    public LightTile() {
        super(BlockRegistry.LIGHT_TILE);
    }

    @Override
    public void tick() {
        if(world.isRemote || world.getGameTime() % 6 != 0)
            return;
        counter +=1;
        if(counter > 8)
            counter = 1;

        world.setBlockState(pos, world.getBlockState(pos).with(ManaCondenserBlock.stage, counter), 3);
    }

}
