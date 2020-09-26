package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.tileentity.TileEntityType;

public class CrystallizerTile extends AbstractManaTile{
    public CrystallizerTile() {
        super(BlockRegistry.CRYSTALLIZER_TILE);
    }

    @Override
    public int getTransferRate() {
        return 0;
    }

    @Override
    public void tick() {

    }
}
