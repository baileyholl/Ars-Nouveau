package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
//import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;

public class WornNotebook extends ModItem{

    public WornNotebook() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1));
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if(playerIn instanceof ServerPlayer) {
//            PatchouliAPI.get().openBookGUI((ServerPlayer) playerIn, Registry.ITEM.getKey(this));
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }
}
