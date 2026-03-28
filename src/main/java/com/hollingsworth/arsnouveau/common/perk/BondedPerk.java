package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.Identifier;

public class BondedPerk extends Perk {

    public static BondedPerk INSTANCE = new BondedPerk(ArsNouveau.prefix("thread_bonded"));

    public BondedPerk(Identifier key) {
        super(key);
    }

    @Override
    public String getLangDescription() {
        return "Damage you take is shared with your familiar, increasing the amount of damage shared by tier. ";
    }

    @Override
    public String getLangName() {
        return "Familiar Bond";
    }
}
