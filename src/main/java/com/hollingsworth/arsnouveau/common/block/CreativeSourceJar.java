package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.CreativeSourceJarTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeSourceJar extends SourceJar {

    public CreativeSourceJar(){
        super(TickableModBlock.defaultProperties().noOcclusion(), LibBlockNames.CREATIVE_SOURCE_JAR);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeSourceJarTile(pos, state);
    }
}
