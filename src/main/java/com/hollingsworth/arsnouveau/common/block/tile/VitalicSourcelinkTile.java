package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.tileentity.TileEntityType;

public class VitalicSourcelinkTile extends SourcelinkTile{
    public VitalicSourcelinkTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public VitalicSourcelinkTile(){
        super(BlockRegistry.VITALIC_TILE);
    }
}
