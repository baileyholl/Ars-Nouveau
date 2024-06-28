package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentRegistry {

    public static ResourceKey<Enchantment> MANA_REGEN_ENCHANTMENT = key("mana_regen");


    public static ResourceKey<Enchantment> MANA_BOOST_ENCHANTMENT = key("mana_boost");
    public static ResourceKey<Enchantment> REACTIVE_ENCHANTMENT = key("reactive");

    private static ResourceKey<Enchantment> key(String p_345314_) {
        return ResourceKey.create(Registries.ENCHANTMENT, ArsNouveau.prefix(p_345314_));
    }
}