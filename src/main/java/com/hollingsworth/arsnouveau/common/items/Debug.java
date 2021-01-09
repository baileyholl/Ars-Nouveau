package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.entity.EntityRitualProjectile;
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


        world.addEntity(new EntityRitualProjectile(world, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ()));
        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }
}
