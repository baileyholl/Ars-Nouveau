package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import top.theillusivec4.curios.api.type.capability.ICurioItem;


public abstract class ArsNouveauCurio extends ModItem implements ICurioItem {

    public ArsNouveauCurio() {
        this(new Properties().stacksTo(1).tab(ArsNouveau.itemGroup));
    }


    public ArsNouveauCurio(Properties properties) {
        super(properties);
    }

}
