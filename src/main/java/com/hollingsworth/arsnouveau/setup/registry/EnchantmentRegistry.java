package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class EnchantmentRegistry {

    public static final DeferredRegister<DataComponentType<?>> ENCHANTMENTS = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, MODID);
    public static ResourceKey<Enchantment> MANA_REGEN_ENCHANTMENT = key("mana_regen");


    public static ResourceKey<Enchantment> MANA_BOOST_ENCHANTMENT = key("mana_boost");
    public static ResourceKey<Enchantment> REACTIVE_ENCHANTMENT = key("reactive");

    private static ResourceKey<Enchantment> key(String p_345314_) {
        return ResourceKey.create(Registries.ENCHANTMENT, ArsNouveau.prefix(p_345314_));
    }
}