package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ISpellBonus;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.util.RomanNumber;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

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
    public void inventoryTick(ItemStack stack, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(stack, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        if(!stack.hasTag())
            stack.setTag(new CompoundNBT());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if(this.getBonusAugment(stack) == null)
            return;
        tooltip.add(new StringTextComponent(this.getBonusAugment(stack).name +" " + RomanNumber.toRoman(this.getBonusLevel(stack))));
    }
}
