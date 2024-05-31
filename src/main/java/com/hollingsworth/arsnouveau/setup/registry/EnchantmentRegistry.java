package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.common.enchantment.ManaBoost;
import com.hollingsworth.arsnouveau.common.enchantment.ManaRegenEnchantment;
import com.hollingsworth.arsnouveau.common.enchantment.ReactiveEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class EnchantmentRegistry {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);
    public static RegistryObject<Enchantment> MANA_REGEN_ENCHANTMENT = ENCHANTMENTS.register("mana_regen", ManaRegenEnchantment::new);
    public static RegistryObject<Enchantment> MANA_BOOST_ENCHANTMENT = ENCHANTMENTS.register("mana_boost", ManaBoost::new);
    public static RegistryObject<Enchantment> REACTIVE_ENCHANTMENT = ENCHANTMENTS.register("reactive", ReactiveEnchantment::new);

}