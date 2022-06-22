package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import net.minecraft.world.item.ItemStack;

public abstract class DiscountRing extends ArsNouveauCurio implements IManaEquipment {

    public DiscountRing() {
        super();
    }

    public abstract int getManaDiscount();

    @Override
    public int getMaxManaBoost(ItemStack i) {
        return 10;
    }

    @Override
    public int getManaRegenBonus(ItemStack i) {
        return 1;
    }

    @Override
    public int getManaDiscount(ItemStack i) {
        return getManaDiscount();
    }
}
