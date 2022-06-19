package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;


public abstract class ArsNouveauCurio extends ModItem {


    public ArsNouveauCurio() {
        this(new Item.Properties().stacksTo(1).tab(ArsNouveau.itemGroup));
    }


    public ArsNouveauCurio(Item.Properties properties){
        super(properties);
    }

    abstract public void wearableTick(LivingEntity wearer);
}
