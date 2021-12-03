package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.AlchemicalSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AlchemicalSourcelinkBlock extends SourcelinkBlock {

    public AlchemicalSourcelinkBlock(){
        super(TickableModBlock.defaultProperties().noOcclusion(), LibBlockNames.ALCHEMICAL_SOURCELINK);
    }

    public AlchemicalSourcelinkBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    public AlchemicalSourcelinkBlock(String registryName) {
        super(registryName);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemicalSourcelinkTile(pos, state);
    }
}
