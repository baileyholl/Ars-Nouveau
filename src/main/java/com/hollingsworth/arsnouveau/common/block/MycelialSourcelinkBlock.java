package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.MycelialSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class MycelialSourcelinkBlock extends SourcelinkBlock {

    public MycelialSourcelinkBlock(){
        super(ModBlock.defaultProperties().noOcclusion(), LibBlockNames.MYCELIAL_SOURCELINK);
    }

    public MycelialSourcelinkBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    public MycelialSourcelinkBlock(String registryName) {
        super(registryName);
    }


    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MycelialSourcelinkTile();
    }
}
