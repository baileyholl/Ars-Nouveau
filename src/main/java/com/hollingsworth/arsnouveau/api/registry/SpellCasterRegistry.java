package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.ItemCasterProvider;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;

public class SpellCasterRegistry {

    public static final Registry<ItemCasterProvider> SPELL_CASTER_TYPES = new MappedRegistry<>(ResourceKey.createRegistryKey(ArsNouveau.prefix("spell_caster_types")), Lifecycle.stable());

    static {
        register(ItemsRegistry.NOVICE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
        register(ItemsRegistry.APPRENTICE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
        register(ItemsRegistry.ARCHMAGE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
        register(ItemsRegistry.CREATIVE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
        register(ItemsRegistry.SCRY_CASTER, (stack) -> stack.get(DataComponentRegistry.SCRY_CASTER));
        register(ItemsRegistry.CASTER_TOME, (stack) -> stack.get(DataComponentRegistry.TOME_CASTER));
        register(ItemsRegistry.SPELL_PARCHMENT, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
    }

    public static @Nullable AbstractCaster<?> from(ItemStack stack){
        return SPELL_CASTER_TYPES.getOptional(BuiltInRegistries.ITEM.getKey(stack.getItem())).orElse((s) -> s.get(DataComponentRegistry.SPELL_CASTER)).getSpellCaster(stack);
    }

    public static boolean hasCaster(ItemStack stack) {
        return SPELL_CASTER_TYPES.containsKey(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public static void register(ItemLike itemLike, ItemCasterProvider provider) {
        Registry.registerForHolder(SPELL_CASTER_TYPES, BuiltInRegistries.ITEM.getKey(itemLike.asItem()), provider);
    }

    public static void register(ResourceLocation location, ItemCasterProvider provider){
        Registry.registerForHolder(SPELL_CASTER_TYPES, location, provider);
    }
}
