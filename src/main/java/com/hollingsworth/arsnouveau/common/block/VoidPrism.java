package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.block.IPrismaticBlock;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class VoidPrism extends ModBlock implements IPrismaticBlock {

    public VoidPrism() {
        super(defaultProperties());
    }

    @Override
    public void onHit(ServerLevel world, BlockPos pos, EntityProjectileSpell spell) {
        spell.remove(Entity.RemovalReason.DISCARDED);
        BlockUtil.updateObservers(world, pos);
    }
}
