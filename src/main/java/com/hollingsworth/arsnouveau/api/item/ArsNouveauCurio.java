package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;

public abstract class ArsNouveauCurio extends Item {


    public ArsNouveauCurio() {
        super(new Item.Properties().maxStackSize(1).group(ArsNouveau.itemGroup));
    }

    abstract public void wearableTick(LivingEntity wearer);
}
