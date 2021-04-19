package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;

public abstract class ArsNouveauCurio extends ModItem {


    public ArsNouveauCurio() {
        super(new Item.Properties().stacksTo(1).tab(ArsNouveau.itemGroup));
    }

    public ArsNouveauCurio(Item.Properties properties, String registryName){
        super(properties, registryName);
    }

    public ArsNouveauCurio(String registryName){
        super(new Item.Properties().stacksTo(1).tab(ArsNouveau.itemGroup), registryName);
    }

    abstract public void wearableTick(LivingEntity wearer);
}
