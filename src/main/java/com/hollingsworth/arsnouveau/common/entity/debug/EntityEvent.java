package com.hollingsworth.arsnouveau.common.entity.debug;

import net.minecraft.world.entity.Entity;

public class EntityEvent extends DebugEvent {
    public final Entity entity;

    public EntityEvent(Entity entity, String id, String message) {
        super(id, message);
        this.entity = entity;
    }
}
