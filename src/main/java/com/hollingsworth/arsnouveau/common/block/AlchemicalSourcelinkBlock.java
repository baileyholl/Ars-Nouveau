package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.AlchemicalSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class AlchemicalSourcelinkBlock extends SourcelinkBlock {

    public AlchemicalSourcelinkBlock(){
        super(ModBlock.defaultProperties().noOcclusion(), LibBlockNames.ALCHEMICAL_SOURCELINK);
    }

    public AlchemicalSourcelinkBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    public AlchemicalSourcelinkBlock(String registryName) {
        super(registryName);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new AlchemicalSourcelinkTile();
    }
}
