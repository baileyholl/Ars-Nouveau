package com.hollingsworth.arsnouveau.api.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public interface IScryer {

    boolean shouldRevealBlock(BlockState state, BlockPos p, Player player);

    IScryer fromTag(CompoundTag tag);

    default CompoundTag toTag(CompoundTag tag){
        tag.putString("id", getID());
        return tag;
    }

    String getID();

    Vec3i DEFAULT_SIZE = new Vec3i(20, 120, 20);

    default Vec3i getScryingSize(){
        return DEFAULT_SIZE;
    }

    default int getScryMax(){
        return 50;
    }

}
