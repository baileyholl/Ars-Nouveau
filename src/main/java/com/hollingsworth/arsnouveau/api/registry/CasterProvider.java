package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.spell.ISpellCasterProvider;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class CasterProvider implements ISpellCasterProvider {

    ItemStack stack;
    Function<ItemStack, SpellCaster> casterGetter;

    public CasterProvider(ItemStack stack, Function<ItemStack, SpellCaster> casterGetter) {
        this.stack = stack;
        this.casterGetter = casterGetter;
    }

    @Override
    public SpellCaster getSpellCaster(ItemStack stack) {
        return casterGetter.apply(stack);
    }
}
