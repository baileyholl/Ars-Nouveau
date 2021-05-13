package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class ExperienceGem extends ModItem{

    public ExperienceGem(Properties properties) {
        super(properties);
    }

    public ExperienceGem(Properties properties, String registryName){
        this(properties);
        setRegistryName(ArsNouveau.MODID, registryName);
    }

    public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        if(!world.isClientSide) {
            if(playerEntity.isCrouching()){
                playerEntity.giveExperiencePoints(getValue() * playerEntity.getItemInHand(hand).getCount());
                playerEntity.getItemInHand(hand).shrink( playerEntity.getItemInHand(hand).getCount());
            }else{
                playerEntity.giveExperiencePoints(getValue());
                playerEntity.getItemInHand(hand).shrink(1);
            }

        }
        return ActionResult.pass(playerEntity.getItemInHand(hand));
    }

    public abstract int getValue();
}
