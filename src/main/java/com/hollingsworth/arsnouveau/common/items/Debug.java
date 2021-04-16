package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Debug extends ModItem{
    public Debug() {
        super(new Item.Properties());
        setRegistryName("debug");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerIn, Hand handIn) {
        System.out.println(world.getDimensionKey().getLocation().toString());
        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }
}
