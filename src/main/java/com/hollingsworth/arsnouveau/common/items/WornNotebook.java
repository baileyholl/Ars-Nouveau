package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.client.gui.documentation.IndexScreen;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WornNotebook extends ModItem {

    public WornNotebook() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1));
        withTooltip(Component.translatable("tooltip.worn_notebook"));
    }

   @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if(worldIn.isClientSide){
            IndexScreen.open();
        }
        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }
}
