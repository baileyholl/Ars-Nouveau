package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ISpellCasterProvider {

    ISpellCaster getSpellCaster();

    default ISpellCaster getSpellCaster(ItemStack stack){
        return getSpellCaster(stack.getOrCreateTag());
    }

    ISpellCaster getSpellCaster(CompoundTag tag);

}
