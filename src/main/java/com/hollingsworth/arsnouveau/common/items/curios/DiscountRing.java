package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import net.minecraft.entity.LivingEntity;

public abstract class DiscountRing extends ArsNouveauCurio implements IManaEquipment {


    public DiscountRing(String registry) {
        super(registry);
    }

    @Override
    public void wearableTick(LivingEntity wearer) { }

    public abstract int getManaDiscount();

    @Override
    public int getMaxManaBoost() {
        return 10;
    }

    @Override
    public int getManaRegenBonus() {
        return 1;
    }
}
