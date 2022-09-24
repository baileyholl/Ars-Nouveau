package com.hollingsworth.arsnouveau.api.perk;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ITickablePerk {

    void tick(ItemStack stack, Level world, Player player, PerkInstance strength);

}
