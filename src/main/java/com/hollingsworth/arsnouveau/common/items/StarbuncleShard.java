package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.items.data.StarbuncleCharmData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class StarbuncleShard extends ModItem{

    public StarbuncleShard(){
        super();
        withTooltip(Component.translatable("tooltip.starbuncle_shard"));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        StarbuncleCharmData data = stack.get(DataComponentRegistry.STARBUNCLE_DATA);
        if(data != null){
            data.name.ifPresent(tooltip2::add);
            if(data.adopter != null){
                tooltip2.add(Component.translatable("ars_nouveau.adopter", data.adopter).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
            }
            if(data.bio != null){
                tooltip2.add(Component.literal(data.bio).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE)));
            }
        }else{
            super.appendHoverText(stack, context, tooltip2, flagIn);
        }
    }
}
