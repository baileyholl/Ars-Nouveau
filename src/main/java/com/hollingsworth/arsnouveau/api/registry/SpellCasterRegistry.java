package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.ItemCasterProvider;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("deprecation")
public class SpellCasterRegistry {

    private static final ConcurrentHashMap<ResourceLocation, ItemCasterProvider> MAP = new ConcurrentHashMap<>();

    static{
        register(ItemsRegistry.NOVICE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
        register(ItemsRegistry.APPRENTICE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
        register(ItemsRegistry.ARCHMAGE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
        register(ItemsRegistry.CREATIVE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
        register(ItemsRegistry.SCRY_CASTER, (stack) -> stack.get(DataComponentRegistry.SCRY_CASTER));
        register(ItemsRegistry.CASTER_TOME, (stack) -> stack.get(DataComponentRegistry.TOME_CASTER));
        register(ItemsRegistry.SPELL_PARCHMENT, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
    }

    public static @Nullable AbstractCaster<?> from(ItemStack stack){
        return MAP.getOrDefault(stack.getItem().builtInRegistryHolder().key().location(), (s) -> s.get(DataComponentRegistry.SPELL_CASTER)).getSpellCaster(stack);
    }

    public static boolean hasCaster(ItemStack stack) {
        return MAP.containsKey(stack.getItem().builtInRegistryHolder().key().location());
    }

    public static void register(ItemLike itemLike, ItemCasterProvider provider){
        MAP.put(itemLike.asItem().builtInRegistryHolder().key().location(), provider);
    }

    public static void register(ResourceLocation location, ItemCasterProvider provider){
        MAP.put(location, provider);
    }
}
