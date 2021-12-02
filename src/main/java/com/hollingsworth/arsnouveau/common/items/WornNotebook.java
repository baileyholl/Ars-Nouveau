package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;

public class WornNotebook extends ModItem{

    public WornNotebook() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1), LibItemNames.WORN_NOTEBOOK);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

//        System.out.println( BookRegistry.INSTANCE.books);
//        Book book = BookRegistry.INSTANCE.books.get(ItemsRegistry.wornNotebook.getRegistryName());

        if(playerIn instanceof ServerPlayer) {
            ServerPlayer player=  (ServerPlayer) playerIn;
//            UseItemSuccessTrigger.INSTANCE.trigger(player, stack, player.getServerWorld(), player.posX, player.posY, player.posZ);
            PatchouliAPI.instance.openBookGUI((ServerPlayer) playerIn, Registry.ITEM.getKey(this));

//            NetworkHandler.sendToPlayer(new MessageOpenBookGui(book.resourceLoc.toString()), (ServerPlayerEntity) playerIn);
//            SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.book_open);
//            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, sfx, SoundCategory.PLAYERS, 1F, (float) (0.7 + Math.random() * 0.4));
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }
}
