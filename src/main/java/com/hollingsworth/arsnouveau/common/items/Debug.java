package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.entity.FamiliarCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
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
    public ActionResult<ItemStack> use(World world, PlayerEntity playerIn, Hand handIn) {
        if(!world.isClientSide){
            FamiliarCarbuncle carbuncle = new FamiliarCarbuncle(ModEntities.ENTITY_FAMILIAR_CARBUNCLE, world);
            carbuncle.setPos(playerIn.blockPosition().getX(), playerIn.position.y, playerIn.position.z);
            carbuncle.setOwnerID(playerIn.getId());
            world.addFreshEntity(carbuncle);
        }
        return ActionResult.success(playerIn.getItemInHand(handIn));
    }
}
