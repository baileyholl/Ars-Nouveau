package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for spell modifiers as they exist for a single effect before resolving.
 */
public class SpellStats {

    public double amplification;

    public double damageModifier;

    public int tickDuration;

    public List<AbstractAugment> augments;

    public List<ItemStack> modifierItems;

    public SpellStats(){
        augments = new ArrayList<>();
        modifierItems = new ArrayList<>();
    }

    public SpellStats setAmplification(double power){
        this.amplification = power;
        return this;
    }

    public SpellStats addAmplification(double amplification){
        this.amplification += amplification;
        return this;
    }

    public SpellStats setDuration(int duration){
        this.tickDuration = duration;
        return this;
    }

    public SpellStats addDuration(int duration){
        this.tickDuration += duration;
        return this;
    }

    public SpellStats setItems(List<ItemStack> items){
        this.modifierItems = items;
        return this;
    }

    public SpellStats addItem(ItemStack itemStack){
        this.modifierItems.add(itemStack);
        return this;
    }
}
