package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.world.level.block.LeavesBlock;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MagicLeaves extends LeavesBlock {
    public MagicLeaves(Properties properties) {
        super(properties);
        //this.setDefaultState(this.stateContainer.getBaseState().with(DISTANCE, 7).with(PERSISTENT, Boolean.TRUE));
    }
}
