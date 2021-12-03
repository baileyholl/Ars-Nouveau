package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.MycelialSourcelinkTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MycelialSourcelinkBlock extends SourcelinkBlock {

    public MycelialSourcelinkBlock(){
        super(TickableModBlock.defaultProperties().noOcclusion(), LibBlockNames.MYCELIAL_SOURCELINK);
    }

    public MycelialSourcelinkBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    public MycelialSourcelinkBlock(String registryName) {
        super(registryName);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MycelialSourcelinkTile(pos, state);
    }
}
