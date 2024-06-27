package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class DiscountRing extends AbstractManaCurio {

    public abstract int getManaDiscount();

    @Override
    public int getMaxManaBoost(ItemStack i) {
        return 10;
    }

    @Override
    public int getManaRegenBonus(ItemStack i) {
        return 1;
    }

    @Override
    public int getManaDiscount(ItemStack i, Spell spell) {
        return getManaDiscount();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        tooltip2.add(Component.translatable("tooltip.discount_item", getManaDiscount(stack)));
    }
}
