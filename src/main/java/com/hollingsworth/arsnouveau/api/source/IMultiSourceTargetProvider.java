package com.hollingsworth.arsnouveau.api.source;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.List;

public interface IMultiSourceTargetProvider {
    List<Pair<BlockPos, Direction>> getFromList();

    List<Pair<BlockPos, Direction>> getToList();
}
