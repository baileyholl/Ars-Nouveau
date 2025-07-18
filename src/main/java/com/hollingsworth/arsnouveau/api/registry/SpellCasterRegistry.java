package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.ItemCasterProvider;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;

public class SpellCasterRegistry {
    public static @Nullable AbstractCaster<?> from(ItemStack stack) {
        var key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        var type = ANRegistries.SPELL_CASTER_TYPES.get(key);
        if (type == null) {
            return stack.get(DataComponentRegistry.SPELL_CASTER);
        }
        return type.getSpellCaster(stack);
    }

    public static AbstractCaster<?> fromOrCreate(ItemStack stack) {
        var key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        var type = ANRegistries.SPELL_CASTER_TYPES.get(key);
        if (type == null) {
            var component = stack.get(DataComponentRegistry.SPELL_CASTER);
            if (component == null) {
                component = new SpellCaster();
                stack.set(DataComponentRegistry.SPELL_CASTER, component);
            }
            return component;
        }
        return type.getSpellCaster(stack);
    }

    public static boolean hasCaster(ItemStack stack) {
        return ANRegistries.SPELL_CASTER_TYPES.containsKey(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public static void register(ItemLike itemLike, ItemCasterProvider provider) {
        Registry.registerForHolder(ANRegistries.SPELL_CASTER_TYPES, BuiltInRegistries.ITEM.getKey(itemLike.asItem()), provider);
    }

    public static <C extends AbstractCaster<?>> void registerComponent(ItemLike itemLike, DeferredHolder<DataComponentType<?>, DataComponentType<C>> type) {
        register(itemLike, s -> s.get(type));
    }

    public static void registerGeneric(ItemLike itemLike) {
        registerComponent(itemLike, DataComponentRegistry.SPELL_CASTER);
    }

    public static void registerTome(ItemLike itemLike) {
        registerComponent(itemLike, DataComponentRegistry.TOME_CASTER);
    }

    public static void registerScry(ItemLike itemLike) {
        registerComponent(itemLike, DataComponentRegistry.SCRY_CASTER);
    }

    public static void register(ResourceLocation location, ItemCasterProvider provider){
        Registry.registerForHolder(ANRegistries.SPELL_CASTER_TYPES, location, provider);
    }
}
