package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.spell.ISpellCasterProvider;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

public class SpellCasterRegistry {
    private static final ConcurrentHashMap<ResourceLocation, ISpellCasterProvider> MAP = new ConcurrentHashMap<>();

    static{
        register(ItemsRegistry.NOVICE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
        register(ItemsRegistry.APPRENTICE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
        register(ItemsRegistry.ARCHMAGE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));
        register(ItemsRegistry.CREATIVE_SPELLBOOK, (stack) -> stack.get(DataComponentRegistry.SPELL_CASTER));

    }

    public static @Nullable SpellCaster from(ItemStack stack){
        return MAP.getOrDefault(stack.getItem().builtInRegistryHolder().key().location(), (s) -> s.get(DataComponentRegistry.SPELL_CASTER)).getSpellCaster(stack);
    }

    public static void register(ItemLike itemLike, ISpellCasterProvider provider){
        MAP.put(itemLike.asItem().builtInRegistryHolder().key().location(), provider);
    }

    public static void register(ResourceLocation location, ISpellCasterProvider provider){
        MAP.put(location, provider);
    }
}
