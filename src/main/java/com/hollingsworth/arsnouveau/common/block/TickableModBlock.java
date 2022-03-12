package com.hollingsworth.arsnouveau.common.block;

public abstract class TickableModBlock extends ModBlock implements ITickableBlock{

    public TickableModBlock(Properties properties, String registry) {
        super(properties, registry);
    }

    public TickableModBlock(String registryName){
        this(defaultProperties(), registryName);
    }

    public TickableModBlock(Properties properties){
        super(properties);
    }
}
