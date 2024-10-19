package com.hollingsworth.arsnouveau.common.items.data;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ICharmSerializable {

    private LivingEntity asEntity(){
        return (LivingEntity) this;
    }

    default PersistentFamiliarData createCharmData(){
        LivingEntity entity = asEntity();
        return new PersistentFamiliarData(entity.getCustomName(), getColor(), getCosmetic());
    }

    void fromCharmData(PersistentFamiliarData data);

    default String getColor(){
        return "";
    }

    default ItemStack getCosmetic(){
        return ItemStack.EMPTY;
    }
}
