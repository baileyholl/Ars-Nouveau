package com.hollingsworth.arsnouveau.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IScribeable {

    /**
     * When *other* items are used on the scribes block while this item is currently on the stand.
     * Params come from ScribesBlock#onBlockActivated
     *
     * @return True if the item was successfully scribed, false if it was not.
     */
    boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack);
}
