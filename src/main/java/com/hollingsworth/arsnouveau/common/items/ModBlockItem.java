package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import javax.annotation.Nullable;
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

    @Override
    public Rarity getRarity(ItemStack stack) {
        return rarity != null ? rarity : super.getRarity(stack);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        if (tooltip != null && !tooltip.isEmpty()) {
            tooltip2.addAll(tooltip);
        }
    }
}
