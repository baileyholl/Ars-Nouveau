package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;

/**
 * A basic timed event for the EventQueue.
 */
public interface ITimedEvent {

    void tick();


    /**
     * If this event should be removed from the queue
     */
    boolean isExpired();
    // For rendering client events only.
    default void tick(RenderWorldLastEvent evt, PlayerEntity player, float renderPartialTicks){};
}
