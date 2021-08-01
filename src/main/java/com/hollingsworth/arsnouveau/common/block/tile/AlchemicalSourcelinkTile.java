package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.tileentity.TileEntityType;

public class AlchemicalSourcelinkTile extends SourcelinkTile{
    public AlchemicalSourcelinkTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public AlchemicalSourcelinkTile(){
        super(BlockRegistry.ALCHEMICAL_TILE);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide){



        }
    }
}
