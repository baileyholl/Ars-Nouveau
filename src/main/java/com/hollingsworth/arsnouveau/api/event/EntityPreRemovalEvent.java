package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

public class EntityPreRemovalEvent extends Event {
    Level level;
    Entity entity;

    public EntityPreRemovalEvent(Level level, Entity entity) {
        this.level = level;
        this.entity = entity;
    }

    public Level getLevel() {
        return this.level;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
