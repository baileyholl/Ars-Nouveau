package com.hollingsworth.arsnouveau.common.block;

public abstract class TickableModBlock extends ModBlock implements ITickableBlock {

    public TickableModBlock() {
        this(defaultProperties());
    }

    public TickableModBlock(Properties properties) {
        super(properties);
    }
}
