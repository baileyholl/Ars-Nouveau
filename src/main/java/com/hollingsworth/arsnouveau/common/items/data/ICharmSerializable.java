package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ICharmSerializable {

    private LivingEntity asEntity() {
        return (LivingEntity) this;
    }

    default PersistentFamiliarData createCharmData() {
        LivingEntity entity = asEntity();
        return new PersistentFamiliarData(entity.getCustomName(), getColor(), getCosmetic());
    }

    void fromCharmData(PersistentFamiliarData data);

    default String getColor() {
        return "";
    }

    @NotNull
    default ItemStack getCosmetic() {
        if (this instanceof IDecoratable decoratable) {
            return decoratable.getCosmeticItem();
        }
        return ItemStack.EMPTY;
    }
}
