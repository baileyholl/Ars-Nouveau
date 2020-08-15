package com.hollingsworth.arsnouveau.api.mana;

public interface IManaEquipment{

    default int getMaxManaBoost(){
        return 0;
    }

    default int getManaRegenBonus(){
        return 0;
    }

    default int getManaDiscount(){
        return 0;
    }
}
