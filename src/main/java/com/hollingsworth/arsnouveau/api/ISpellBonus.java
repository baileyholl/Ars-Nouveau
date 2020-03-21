package com.hollingsworth.arsnouveau.api;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;

import java.util.ArrayList;
import java.util.List;

public interface ISpellBonus {
    /*
     * Augment to be applied to EVERY effect type
     */
    AbstractAugment getBonusAugment();

    /**
     * Get the number of bonus augments that should be applied
     */
    int getBonusLevel();

    //Helper method to get the total list of bonuses
    default List<AbstractAugment> getList(){
        ArrayList<AbstractAugment> augments = new ArrayList<>();
        for(int i = 0; i < getBonusLevel(); i++){
            augments.add(getBonusAugment());
        }
        return augments;
    }
}
