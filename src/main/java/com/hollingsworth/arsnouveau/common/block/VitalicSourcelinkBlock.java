package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.VitalicSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

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
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new VitalicSourcelinkTile();
    }
}
