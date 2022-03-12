package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.VitalicSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VitalicSourcelinkBlock extends SourcelinkBlock{
    public VitalicSourcelinkBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    public VitalicSourcelinkBlock(String registryName) {
        super(registryName);
    }

    public VitalicSourcelinkBlock(){
        super(TickableModBlock.defaultProperties().noOcclusion(), LibBlockNames.VITALIC_SOURCELINK);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VitalicSourcelinkTile(pos, state);
    }

}
