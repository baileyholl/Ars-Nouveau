package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.common.block.ManaCondenserBlock;
import com.hollingsworth.arsnouveau.common.block.BlockRegistry;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;

public class LightTile extends AnimatedTile {
    public LightTile() {
        super(BlockRegistry.LIGHT_TILE);
    }
    public static final IProperty stage = IntegerProperty.create("stage", 1, 8);
    @Override
    public void tick() {
        if(world.isRemote || world.getGameTime() % 6 != 0)
            return;
        counter +=1;
        if(counter > 8)
            counter = 1;

        world.setBlockState(pos, world.getBlockState(pos).with(stage, counter), 3);
    }

}
