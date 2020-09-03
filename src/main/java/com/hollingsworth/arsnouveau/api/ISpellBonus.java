package com.hollingsworth.arsnouveau.api;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface ISpellBonus {
    public static final String LEVEL = "level";
    public static final String AUGMENT = "augment";
    /*
     * Augment to be applied to EVERY effect type
     */
    default AbstractAugment getBonusAugment(ItemStack stack){
        if(!stack.hasTag() || !stack.getTag().contains(AUGMENT))
            return null;
        return ArsNouveauAPI.getInstance().getSpell_map().get(stack.getTag().getString(AUGMENT)) != null ? (AbstractAugment)ArsNouveauAPI.getInstance().getSpell_map().get(stack.getTag().getString(AUGMENT)): null;
    }

    /**
     * Get the number of bonus augments that should be applied
     */
    default int getBonusLevel(ItemStack stack){
        return stack.getTag().getInt(LEVEL);
    }

    //Helper method to get the total list of bonuses
    default List<AbstractAugment> getList(ItemStack stack){
        ArrayList<AbstractAugment> augments = new ArrayList<>();
        for(int i = 0; i < getBonusLevel(stack); i++){
            augments.add(getBonusAugment(stack));
        }
        return augments;
    }
}
