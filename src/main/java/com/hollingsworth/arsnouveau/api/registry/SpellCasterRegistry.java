package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.ItemCasterProvider;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;

public class SpellCasterRegistry {
    public static @Nullable AbstractCaster<?> from(ItemStack stack){
        return ANRegistries.SPELL_CASTER_TYPES.getOptional(BuiltInRegistries.ITEM.getKey(stack.getItem())).orElse((s) -> s.get(DataComponentRegistry.SPELL_CASTER)).getSpellCaster(stack);
    }

    public static boolean hasCaster(ItemStack stack) {
        return ANRegistries.SPELL_CASTER_TYPES.containsKey(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public static void register(ItemLike itemLike, ItemCasterProvider provider) {
        Registry.registerForHolder(ANRegistries.SPELL_CASTER_TYPES, BuiltInRegistries.ITEM.getKey(itemLike.asItem()), provider);
    }

    public static void register(ResourceLocation location, ItemCasterProvider provider){
        Registry.registerForHolder(ANRegistries.SPELL_CASTER_TYPES, location, provider);
    }
}
