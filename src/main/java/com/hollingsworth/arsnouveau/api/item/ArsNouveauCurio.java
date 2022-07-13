package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;


import net.minecraft.world.item.Item.Properties;

public abstract class ArsNouveauCurio extends ModItem implements ICurioItem {

    public ArsNouveauCurio() {
        this(new Properties().stacksTo(1).tab(ArsNouveau.itemGroup));
    }


    public ArsNouveauCurio(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
