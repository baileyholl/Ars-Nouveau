package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;

public abstract class DiscountRing extends AbstractManaCurio implements IManaDiscountEquipment {

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
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, display, tooltip2, flagIn);
        tooltip2.accept(Component.translatable("tooltip.discount_item", getManaDiscount(stack)));
    }
}
