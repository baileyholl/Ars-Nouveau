package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.tileentity.TileEntityType;

public class MycelialSourcelinkTile extends SourcelinkTile{
    public MycelialSourcelinkTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public MycelialSourcelinkTile(){
        super(BlockRegistry.MYCELIAL_TILE);
    }


    @Override
    public void tick() {
        super.tick();
    }
}
