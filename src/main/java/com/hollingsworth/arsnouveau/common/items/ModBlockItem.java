package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;

public class ModBlockItem extends BlockItem {
    public List<Component> tooltip = new ArrayList<>();
    public Rarity rarity;

    public ModBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    public ModBlockItem withTooltip(Component tip) {
        tooltip.add(tip);
        return this;
    }

    public ModBlockItem withRarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public Rarity getRarity() {
        return rarity;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
        @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, display, tooltip2, flagIn);
        if (tooltip != null && !tooltip.isEmpty()) {
            tooltip.forEach(tooltip2);
        }
    }
}
