package com.hollingsworth.craftedmagic.api.item;

import com.hollingsworth.craftedmagic.api.ISpellBonus;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import com.hollingsworth.craftedmagic.api.util.RomanNumber;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractAugmentItem extends Item implements ISpellBonus {
    public AbstractAugmentItem(Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent(this.getBonusAugment().description +" " + RomanNumber.toRoman(this.getBonusLevel())));

    }
}
