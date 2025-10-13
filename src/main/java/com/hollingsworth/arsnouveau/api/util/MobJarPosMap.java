package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MobJarPosMap<T extends Entity> extends LevelPosMap {
    public MobJarPosMap(Class<T> entityClass) {
        super(
            (level, pos) ->
                    !(level.getBlockEntity(pos) instanceof MobJarTile mobJarTile)
                    || !(entityClass.isInstance(mobJarTile.getEntity()))
        );
    }
}
