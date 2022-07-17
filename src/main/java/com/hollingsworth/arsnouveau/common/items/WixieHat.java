package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyPotionBehavior;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class WixieHat extends ModItem{

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if(pInteractionTarget instanceof Starbuncle starbuncle){
            starbuncle.setBehavior(new StarbyPotionBehavior(starbuncle, new CompoundTag()));
            PortUtil.sendMessage(pPlayer, Component.translatable("ars_nouveau.starbuncle.potion_behavior_set"));
        }
        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        return super.useOn(pContext);
    }
}
