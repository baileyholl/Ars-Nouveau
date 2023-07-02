package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ManipulationEssence extends ModItem{

    public ManipulationEssence() {
        super();
        withTooltip(Component.translatable("tooltip.ars_nouveau.essences"));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        tooltip2.add(Component.translatable("ars_nouveau.manipulation_essence.tooltip").withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
    }
}
