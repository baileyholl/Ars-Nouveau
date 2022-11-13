package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.PatchouliAPI;

import org.jetbrains.annotations.NotNull;

public class WornNotebook extends ModItem {

    public WornNotebook() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1));
    }

   @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (playerIn instanceof ServerPlayer player) {
            PatchouliAPI.get().openBookGUI(player, ForgeRegistries.ITEMS.getKey(this));
        }

        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }
}
