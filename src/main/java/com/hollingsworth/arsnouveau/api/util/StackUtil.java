package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class StackUtil {

    public static @Nonnull ItemStack getHeldSpellbook(PlayerEntity playerEntity){
        ItemStack book = playerEntity.getMainHandItem().getItem() instanceof SpellBook ? playerEntity.getMainHandItem() : null;
        return book == null ? (playerEntity.getOffhandItem().getItem() instanceof SpellBook ? playerEntity.getOffhandItem() : ItemStack.EMPTY) : book;
    }
}
