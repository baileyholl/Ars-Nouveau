package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.VolcanicSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class VolcanicSourcelinkBlock extends SourcelinkBlock {

    public VolcanicSourcelinkBlock() {
        super(defaultProperties().noOcclusion().lightLevel(state -> 15), LibBlockNames.VOLCANIC_ACCUMULATOR);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new VolcanicSourcelinkTile();
    }
}
