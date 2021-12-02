package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.block.tile.CreativeManaJarTile;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;

public class CreativeManaJar extends ManaJar {

    public CreativeManaJar(){
        super(ModBlock.defaultProperties().noOcclusion(), LibBlockNames.CREATIVE_MANA_JAR);
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new CreativeManaJarTile();
    }
}
