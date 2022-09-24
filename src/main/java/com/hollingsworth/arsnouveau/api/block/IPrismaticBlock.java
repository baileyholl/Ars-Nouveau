package com.hollingsworth.arsnouveau.api.block;

import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface IPrismaticBlock {

    /**
     * When a spell projectile hits this block.
     */
    void onHit(ServerLevel world, BlockPos pos, EntityProjectileSpell spell);

}
