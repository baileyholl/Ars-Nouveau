package com.hollingsworth.craftedmagic.enchantment;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.armor.MagicArmor;
import com.hollingsworth.craftedmagic.armor.NoviceArmor;
import com.hollingsworth.craftedmagic.items.SpellBook;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

import static com.hollingsworth.craftedmagic.InjectionUtil.Null;


@ObjectHolder(ArsNouveau.MODID)
public class EnchantmentRegistry {

    public static ManaRegenEnchantment MANA_REGEN_ENCHANTMENT = Null();
    public static ManaBoost MANA_BOOST_ENCHANTMENT = Null();
    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerEnchants(final RegistryEvent.Register<Enchantment> event) {
            MANA_REGEN_ENCHANTMENT = new ManaRegenEnchantment();
            MANA_BOOST_ENCHANTMENT = new ManaBoost();
            final IForgeRegistry<Enchantment> registry = event.getRegistry();
            registry.register(MANA_REGEN_ENCHANTMENT);
            registry.register(MANA_BOOST_ENCHANTMENT);
        }
    }
}