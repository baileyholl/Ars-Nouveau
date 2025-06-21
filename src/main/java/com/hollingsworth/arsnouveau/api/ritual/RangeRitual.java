package com.hollingsworth.arsnouveau.api.ritual;

public abstract class RangeRitual extends AbstractRitual {

    @Override
    protected void tick() {
        if (!getWorld().isClientSide && getWorld().getGameTime() % 20 == 0 && !RitualEventQueue.containsPosition(tile.getLevel(), tile.getBlockPos())) {
            RitualEventQueue.addPosition(tile.getLevel(), tile.getBlockPos());
        }
    }
}
