package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.network.NetworkHandler;
import vazkii.patchouli.common.network.message.MessageOpenBookGui;

import javax.annotation.Nonnull;

public class WornNotebook extends ModItem{

    public WornNotebook() {
        super(ItemsRegistry.defaultItemProperties().maxStackSize(1), LibItemNames.WORN_NOTEBOOK);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        System.out.println( BookRegistry.INSTANCE.books);
        Book book = BookRegistry.INSTANCE.books.get(ItemsRegistry.wornNotebook.getRegistryName());

        if(playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity player=  (ServerPlayerEntity) playerIn;
//            UseItemSuccessTrigger.INSTANCE.trigger(player, stack, player.getServerWorld(), player.posX, player.posY, player.posZ);
            NetworkHandler.sendToPlayer(new MessageOpenBookGui(book.resourceLoc.toString()), (ServerPlayerEntity) playerIn);
            SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.book_open);
            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, sfx, SoundCategory.PLAYERS, 1F, (float) (0.7 + Math.random() * 0.4));
        }

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}
