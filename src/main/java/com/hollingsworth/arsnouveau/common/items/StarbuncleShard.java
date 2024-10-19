package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.items.data.StarbuncleCharmData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StarbuncleShard extends ModItem{

    public StarbuncleShard(){
        super();
        withTooltip(Component.translatable("tooltip.starbuncle_shard"));
        withTooltip(Component.translatable("tooltip.starbuncle_shard2").withStyle(ItemsRegistry.LORE_STYLE));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        StarbuncleCharmData data = stack.get(DataComponentRegistry.STARBUNCLE_DATA);
        if(data != null){
            data.getName().ifPresent(tooltip2::add);
            if(data.getAdopter() != null && !data.getAdopter().isEmpty()){
                tooltip2.add(Component.translatable("ars_nouveau.adopter", data.getAdopter()).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
            }
            if(data.getBio() != null && !data.getBio().isEmpty()){
                tooltip2.add(Component.literal(data.getBio()).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE)));
            }
        }else{
            super.appendHoverText(stack, context, tooltip2, flagIn);
        }
    }
}
