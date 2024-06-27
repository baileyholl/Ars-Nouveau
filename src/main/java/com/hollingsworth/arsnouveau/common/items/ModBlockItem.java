package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        if (tooltip != null && !tooltip.isEmpty()) {
            tooltip2.addAll(tooltip);
        }
    }
}
