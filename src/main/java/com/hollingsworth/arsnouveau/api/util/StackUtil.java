package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StackUtil {

    public static @Nonnull ItemStack getHeldSpellbook(Player playerEntity) {
        ItemStack book = playerEntity.getMainHandItem().getItem() instanceof SpellBook ? playerEntity.getMainHandItem() : null;
        return book == null ? (playerEntity.getOffhandItem().getItem() instanceof SpellBook ? playerEntity.getOffhandItem() : ItemStack.EMPTY) : book;
    }

    public static @Nullable InteractionHand getHeldCasterTool(Player player) {
        InteractionHand casterTool = player.getMainHandItem().getItem() instanceof ICasterTool ? InteractionHand.MAIN_HAND : null;
        return casterTool == null ? (player.getOffhandItem().getItem() instanceof ICasterTool ? InteractionHand.OFF_HAND : null) : casterTool;
    }
}
