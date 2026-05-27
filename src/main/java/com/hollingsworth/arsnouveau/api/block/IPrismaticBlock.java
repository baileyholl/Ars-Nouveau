package com.hollingsworth.arsnouveau.api.block;

import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IPrismaticBlock {

    default void onHit(Level level, BlockState state, BlockPos pos, EntityProjectileSpell projectileSpell) {

    }

    /**
     * When a spell projectile hits this block.
     */
    @Deprecated(forRemoval = true)
    default void onHit(ServerLevel world, BlockPos pos, EntityProjectileSpell spell) {
        onHit(world, world.getBlockState(pos), pos, spell);
    }

}
