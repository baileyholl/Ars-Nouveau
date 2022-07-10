package com.hollingsworth.arsnouveau.common.entity.debug;

import com.google.common.collect.EvictingQueue;
import net.minecraft.world.entity.Entity;

import java.io.IOException;
import java.io.PrintWriter;

public class EntityDebugger implements IDebugger{
    public EvictingQueue<DebugEvent> events = EvictingQueue.create(100);

    public final Entity entity;

    public EntityDebugger(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void addEntityEvent(DebugEvent event) {
        events.add(new EntityEvent(entity, event.id, event.message));
    }

    @Override
    public void writeFile(PrintWriter writer) throws IOException {

    }
}
