package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import net.minecraft.world.level.Level;

public class FlyingItemEvent implements ITimedEvent {

    Level level;
    EntityFlyingItem flyingItem;
    int delay;

    public FlyingItemEvent(Level level, EntityFlyingItem flyingItem, int delay) {
        this.level = level;
        this.flyingItem = flyingItem;
        this.delay = delay;
    }

    @Override
    public void tick(boolean serverSide) {
        if (delay > 0) {
            delay--;
        }
        if (serverSide && delay <= 0) {
            level.addFreshEntity(flyingItem);
        }
    }

    @Override
    public boolean isExpired() {
        return delay <= 0;
    }
}
