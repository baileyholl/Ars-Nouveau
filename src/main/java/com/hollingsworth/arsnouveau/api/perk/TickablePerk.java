package com.hollingsworth.arsnouveau.api.perk;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface TickablePerk extends IPerk {

    void tick(ItemStack stack, Level world, Player player, Integer strength);

}
