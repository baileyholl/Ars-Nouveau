package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.ISpellBonus;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.util.RomanNumber;
import com.hollingsworth.arsnouveau.items.ModItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractAugmentItem extends ModItem implements ISpellBonus {
    public AbstractAugmentItem(Properties properties) {
        super(properties);
    }

    public AbstractAugmentItem(Properties properties, String registryName){
        super(properties, registryName);
    }

    public AbstractAugmentItem(String registryName){
        super(registryName);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent(this.getBonusAugment().description +" " + RomanNumber.toRoman(this.getBonusLevel())));

    }
}
