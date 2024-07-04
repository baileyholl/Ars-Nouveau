package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.world.item.ItemStack;

public interface ItemCasterProvider {

    SpellCaster getSpellCaster(ItemStack stack);
}
