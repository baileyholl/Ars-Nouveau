package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.CreativeManaJarTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

public class CreativeManaJar extends ManaJar {

    public CreativeManaJar(){
        super(TickableModBlock.defaultProperties().noOcclusion(), LibBlockNames.CREATIVE_MANA_JAR);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeManaJarTile(pos, state);
    }
}
