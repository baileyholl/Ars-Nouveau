package com.hollingsworth.arsnouveau.api.perk;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility wrapper for mapping a collection of perks from armor, tools, or capabilities.
 *
 */
public class PerkSet {

    private final Map<Perk, Integer> perks;


    public PerkSet() {
        this.perks = new HashMap<>();
    }

    /**
     * Returns the applicable count of a perk, ignoring wasted perks
     */
    public int countForPerk(Perk perk){
        return Math.min(perks.getOrDefault(perk, 0), perk.getCountCap());
    }

    public Map<Perk, Integer> getPerkMap() {
        return perks;
    }

}
