package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public abstract class ExperienceGem extends ModItem{

    public ExperienceGem(Properties properties) {
        super(properties);
    }

    public ExperienceGem(Properties properties, String registryName){
        this(properties);
        setRegistryName(ArsNouveau.MODID, registryName);
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
        if(!world.isClientSide) {
            if(playerEntity.isCrouching()){
                playerEntity.giveExperiencePoints(getValue() * playerEntity.getItemInHand(hand).getCount());
                playerEntity.getItemInHand(hand).shrink( playerEntity.getItemInHand(hand).getCount());
            }else{
                playerEntity.giveExperiencePoints(getValue());
                playerEntity.getItemInHand(hand).shrink(1);
            }

        }
        return InteractionResultHolder.pass(playerEntity.getItemInHand(hand));
    }

    public abstract int getValue();
}
