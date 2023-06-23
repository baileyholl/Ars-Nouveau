package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public abstract class ArsNouveauCurio extends ModItem implements ICurioItem {

    public ArsNouveauCurio() {
        this(new Properties().stacksTo(1));
    }


    public ArsNouveauCurio(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
