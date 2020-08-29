package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import net.minecraft.entity.LivingEntity;

public abstract class AbstractManaCurio extends ArsNouveauCurio implements IManaEquipment {
    public AbstractManaCurio(String reg){
        super(reg);
    }

    @Override
    public void wearableTick(LivingEntity wearer) { }
}
