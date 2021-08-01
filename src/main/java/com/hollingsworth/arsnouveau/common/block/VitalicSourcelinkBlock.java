package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.VitalicSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class VitalicSourcelinkBlock extends SourcelinkBlock{
    public VitalicSourcelinkBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    public VitalicSourcelinkBlock(String registryName) {
        super(registryName);
    }

    public VitalicSourcelinkBlock(){
        super(ModBlock.defaultProperties().noOcclusion(), LibBlockNames.VITALIC_SOURCELINK);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new VitalicSourcelinkTile();
    }
}
