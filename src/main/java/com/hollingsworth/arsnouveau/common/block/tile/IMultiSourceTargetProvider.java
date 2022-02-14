package com.hollingsworth.arsnouveau.common.block.tile;

import net.minecraft.core.BlockPos;

import java.util.List;

public interface IMultiSourceTargetProvider {
    List<BlockPos> getFromList();
    List<BlockPos> getToList();
}
