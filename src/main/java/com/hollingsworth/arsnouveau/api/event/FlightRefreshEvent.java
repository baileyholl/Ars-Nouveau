package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class FlightRefreshEvent extends PlayerEvent {

    private boolean canContinueFlight;
    public FlightRefreshEvent(PlayerEntity entity) {
        super(entity);
    }

    public void setCanFly(boolean canFly){
        this.canContinueFlight = canFly;
    }

    public boolean canFly(){
        return !this.isCanceled() && canContinueFlight;
    }
}
