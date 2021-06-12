package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class DrygmyTile extends TileEntity implements ITickableTileEntity {
    public DrygmyTile() {
        super(BlockRegistry.DRYGMY_TILE);
    }

    @Override
    public void tick() {

    }
}
