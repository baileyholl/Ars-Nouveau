package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ManipulationEssence extends AbstractEssence {

    public ManipulationEssence() {
        super("manipulation");
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        tooltip2.addAll(DocClientUtils.splitTooltip(Component.translatable("ars_nouveau.manipulation_essence.tooltip").withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), context));
    }
}
