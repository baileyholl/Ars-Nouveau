package com.hollingsworth.arsnouveau.api.entity;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IDecoratable {

    @NotNull ItemStack getCosmeticItem();
    void setCosmeticItem(ItemStack cosmeticItem);

}
