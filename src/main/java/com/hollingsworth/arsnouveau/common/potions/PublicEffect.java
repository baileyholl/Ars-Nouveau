package com.hollingsworth.arsnouveau.common.potions;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import java.util.List;

/**
 *  GOD HAVE MERCY ON OUR PROTECTED SOULS
 */
public class PublicEffect extends Effect {

    List<ItemStack> curativeItems;
    public PublicEffect(EffectType p_i50391_1_, int p_i50391_2_) {
        super(p_i50391_1_, p_i50391_2_);
    }

    public PublicEffect(EffectType type, int color, List<ItemStack> curativeItems){
        this(type,color);
        this.curativeItems = curativeItems;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return this.curativeItems != null ? curativeItems : super.getCurativeItems();
    }
}
