package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class StackUtil {

    public static @Nonnull ItemStack getHeldSpellbook(Player playerEntity){
        ItemStack book = playerEntity.getMainHandItem().getItem() instanceof SpellBook ? playerEntity.getMainHandItem() : null;
        return book == null ? (playerEntity.getOffhandItem().getItem() instanceof SpellBook ? playerEntity.getOffhandItem() : ItemStack.EMPTY) : book;
    }
}
