package com.hollingsworth.craftedmagic.items;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.armor.MagicArmor;
import com.hollingsworth.craftedmagic.armor.Materials;
import com.hollingsworth.craftedmagic.armor.NoviceArmor;
import com.hollingsworth.craftedmagic.items.SpellBook;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashSet;
import java.util.Set;

@ObjectHolder(ArsNouveau.MODID)
public class ModItems {
    @ObjectHolder("ars_nouveau:spell_book")
    public static SpellBook spellBook;



    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler{
        public static final Set<Item> ITEMS = new HashSet<>();

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            final MagicArmor noviceBoots = new NoviceArmor(EquipmentSlotType.FEET);
            final MagicArmor novicePants = new NoviceArmor(EquipmentSlotType.LEGS);
            final MagicArmor noviceChest = new NoviceArmor(EquipmentSlotType.CHEST);
            final MagicArmor noviceHead = new NoviceArmor(EquipmentSlotType.HEAD);
            final Item[] items = {
                    noviceBoots.setRegistryName("novice_boots"),
                    novicePants.setRegistryName("novice_legs"),
                    noviceChest.setRegistryName("novice_chest"),
                    noviceHead.setRegistryName("novice_head"),
            };
            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final Item item : items) {
                registry.register(item);
                ITEMS.add(item);
            }
        }
    }
//
//    @SideOnly(Side.CLIENT)
//    public static void initModels() {
//        spell.initModel();
//
//    }

    public static Item.Properties defaultItemProperties() {
        return new Item.Properties().group(ArsNouveau.itemGroup);
    }
}

