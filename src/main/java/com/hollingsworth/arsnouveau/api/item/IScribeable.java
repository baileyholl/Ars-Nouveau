package com.hollingsworth.arsnouveau.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IScribeable {

    /**
     * When *other* items are used on the scribes block while this item is currently on the stand.
     * Params come from ScribesBlock#onBlockActivated
     * @return True if the item was successfully scribed, false if it was not.
     */
    boolean onScribe(World world, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack thisStack);
}
