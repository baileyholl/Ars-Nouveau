package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.world.item.ItemStack;

public interface ISpellCasterProvider {

    SpellCaster getSpellCaster(ItemStack stack);
}
