package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.compat.PatchouliHandler;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

public class WornNotebook extends ModItem {

    public WornNotebook() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1));
        withTooltip(Component.translatable("tooltip.worn_notebook"));
    }

   @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (ArsNouveau.patchouliLoaded && playerIn instanceof ServerPlayer player) {
            PatchouliHandler.openBookGUI(player);
        }else if(!ArsNouveau.patchouliLoaded){
            try {
                Util.getPlatform().openUri(new URI("https://www.arsnouveau.wiki/"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }
}
