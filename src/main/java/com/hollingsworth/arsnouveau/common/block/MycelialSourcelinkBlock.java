package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.MycelialSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

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
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new MycelialSourcelinkTile();
    }
}
