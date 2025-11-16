package com.hollingsworth.arsnouveau.api.entity;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

//TODO: Merge with ICharmSerializable?
public interface IDecoratable {

    @NotNull ItemStack getCosmeticItem();

    void setCosmeticItem(ItemStack cosmeticItem);

}
