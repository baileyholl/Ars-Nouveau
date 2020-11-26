package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.capability.CasterCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Debug extends Caster{
    public Debug() {
        super(new Item.Properties());
        setRegistryName("debug");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerIn, Hand handIn) {
        if(world.isRemote)
            return ActionResult.resultPass(playerIn.getHeldItem(handIn));

        CasterCapability.getCaster(playerIn.getHeldItem(handIn)).ifPresent(a ->{
            System.out.println(a.getCurrentSlot());
            a.setCurrentSlot(a.getCurrentSlot() + 1);
        });

        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }
}
