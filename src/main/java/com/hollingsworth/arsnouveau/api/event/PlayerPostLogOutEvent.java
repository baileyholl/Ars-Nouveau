package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerPostLogOutEvent extends PlayerEvent {
    public PlayerPostLogOutEvent(Player player) {
        super(player);
    }
}
