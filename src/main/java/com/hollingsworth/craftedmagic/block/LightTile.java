package com.hollingsworth.craftedmagic.block;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import top.theillusivec4.curios.api.CuriosAPI;

public class LightTile extends AnimatedTile {
    public LightTile() {
        super(ModBlocks.LIGHT_TILE);
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
