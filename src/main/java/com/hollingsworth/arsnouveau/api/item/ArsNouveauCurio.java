package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;


public abstract class ArsNouveauCurio extends ModItem implements ICurioItem {


    public ArsNouveauCurio() {
        this(new Item.Properties().stacksTo(1).tab(ArsNouveau.itemGroup));
    }


    public ArsNouveauCurio(Item.Properties properties) {
        super(properties);
    }

    //TODO change this to

    /**
     * {@link ICurioItem#curioTick(SlotContext, ItemStack)}
     */
    abstract public void wearableTick(LivingEntity wearer);
}
