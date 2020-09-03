package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;

public class StackUtil {

    public static @Nullable ItemStack getHeldSpellbook(PlayerEntity playerEntity){
        ItemStack book = playerEntity.getHeldItemMainhand().getItem() instanceof SpellBook ? playerEntity.getHeldItemMainhand() : null;
        return book == null ? (playerEntity.getHeldItemOffhand().getItem() instanceof SpellBook ? playerEntity.getHeldItemOffhand() : null) : book;
    }
}
