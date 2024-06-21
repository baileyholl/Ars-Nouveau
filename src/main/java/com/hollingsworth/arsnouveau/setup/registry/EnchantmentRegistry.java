package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.common.enchantment.ManaBoost;
import com.hollingsworth.arsnouveau.common.enchantment.ManaRegenEnchantment;
import com.hollingsworth.arsnouveau.common.enchantment.ReactiveEnchantment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class EnchantmentRegistry {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT, MODID);
    public static DeferredHolder<Enchantment, ManaRegenEnchantment> MANA_REGEN_ENCHANTMENT = ENCHANTMENTS.register("mana_regen", ManaRegenEnchantment::new);
    public static DeferredHolder<Enchantment, ManaBoost> MANA_BOOST_ENCHANTMENT = ENCHANTMENTS.register("mana_boost", ManaBoost::new);
    public static DeferredHolder<Enchantment, ReactiveEnchantment> REACTIVE_ENCHANTMENT = ENCHANTMENTS.register("reactive", ReactiveEnchantment::new);

}