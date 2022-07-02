package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class FlightRefreshEvent extends PlayerEvent {

    private boolean canContinueFlight;

    public FlightRefreshEvent(Player entity) {
        super(entity);
    }

    public void setCanFly(boolean canFly) {
        this.canContinueFlight = canFly;
    }

    public boolean canFly() {
        return !this.isCanceled() && canContinueFlight;
    }
}
