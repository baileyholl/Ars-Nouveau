package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Debug extends ModItem{
    public Debug() {
        super(new Item.Properties());
        setRegistryName("debug");
    }


    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if(!context.getLevel().isClientSide){
//            EventQueue.getServerInstance().addEvent(new EarthquakeEvent(context.getLevel(), context.getClickedPos(), context.getClickedPos().north(20).east(0)));
//            EventQueue.getServerInstance().addEvent(new EarthquakeEvent(context.getLevel(), context.getClickedPos(), context.getClickedPos().north(20).east(20)));
//            EventQueue.getServerInstance().addEvent(new EarthquakeEvent(context.getLevel(), context.getClickedPos(), context.getClickedPos().north(20).west(20)));
        }
        return super.useOn(context);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity playerIn, Hand handIn) {

        return ActionResult.success(playerIn.getItemInHand(handIn));
    }
}
