package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.block.LeavesBlock;

import net.minecraft.block.AbstractBlock.Properties;

public class MagicLeaves extends LeavesBlock {
    public MagicLeaves(Properties properties) {
        super(properties);
        //this.setDefaultState(this.stateContainer.getBaseState().with(DISTANCE, 7).with(PERSISTENT, Boolean.TRUE));
    }
}
