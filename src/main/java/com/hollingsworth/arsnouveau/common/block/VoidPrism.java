package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.api.block.IPrismaticBlock;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class VoidPrism extends ModBlock implements IPrismaticBlock {

    public VoidPrism() {
        super(defaultProperties());
    }

    @Override
    public void onHit(Level level, BlockState state, BlockPos pos, EntityProjectileSpell spell) {
        spell.remove(Entity.RemovalReason.DISCARDED);
        BlockUtil.updateObservers(level, pos);
    }
}
