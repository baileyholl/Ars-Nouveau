package com.hollingsworth.arsnouveau.common.enchantment;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static com.hollingsworth.arsnouveau.setup.InjectionUtil.Null;


@ObjectHolder(ArsNouveau.MODID)
public class EnchantmentRegistry {

    public static ManaRegenEnchantment MANA_REGEN_ENCHANTMENT = Null();
    public static ManaBoost MANA_BOOST_ENCHANTMENT = Null();
    public static ReactiveEnchantment REACTIVE_ENCHANTMENT = Null();
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerEnchants(final RegistryEvent.Register<Enchantment> event) {
            MANA_REGEN_ENCHANTMENT = new ManaRegenEnchantment();
            MANA_BOOST_ENCHANTMENT = new ManaBoost();
            REACTIVE_ENCHANTMENT = new ReactiveEnchantment();
            final IForgeRegistry<Enchantment> registry = event.getRegistry();
            registry.register(MANA_REGEN_ENCHANTMENT);
            registry.register(MANA_BOOST_ENCHANTMENT);
            registry.register(REACTIVE_ENCHANTMENT);
        }
    }
}