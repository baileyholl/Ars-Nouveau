package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class TickableModBlock extends ModBlock implements ITickableBlock {

    public TickableModBlock() {
        this(defaultProperties());
    }

    public TickableModBlock(Properties properties) {
        super(properties);
    }
}
