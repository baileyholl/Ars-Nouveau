package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;

public class WornNotebook extends ModItem{

    public WornNotebook() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1), LibItemNames.WORN_NOTEBOOK);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

//        System.out.println( BookRegistry.INSTANCE.books);
//        Book book = BookRegistry.INSTANCE.books.get(ItemsRegistry.wornNotebook.getRegistryName());

        if(playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity player=  (ServerPlayerEntity) playerIn;
//            UseItemSuccessTrigger.INSTANCE.trigger(player, stack, player.getServerWorld(), player.posX, player.posY, player.posZ);
            PatchouliAPI.instance.openBookGUI((ServerPlayerEntity) playerIn, Registry.ITEM.getKey(this));

//            NetworkHandler.sendToPlayer(new MessageOpenBookGui(book.resourceLoc.toString()), (ServerPlayerEntity) playerIn);
//            SoundEvent sfx = PatchouliSounds.getSound(book.openSound, PatchouliSounds.book_open);
//            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, sfx, SoundCategory.PLAYERS, 1F, (float) (0.7 + Math.random() * 0.4));
        }

        return new ActionResult<>(ActionResultType.PASS, stack);
    }
}
