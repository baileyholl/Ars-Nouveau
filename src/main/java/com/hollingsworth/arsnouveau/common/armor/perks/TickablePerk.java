package com.hollingsworth.arsnouveau.common.armor.perks;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface TickablePerk {

    public void tick(ItemStack stack, Level world, Player player, Integer strength);
}
