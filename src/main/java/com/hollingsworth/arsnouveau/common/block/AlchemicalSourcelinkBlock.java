package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.AlchemicalSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

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
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new AlchemicalSourcelinkTile();
    }
}
