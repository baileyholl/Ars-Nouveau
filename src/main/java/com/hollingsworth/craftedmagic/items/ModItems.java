package com.hollingsworth.craftedmagic.items;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.armor.*;
import com.hollingsworth.craftedmagic.items.SpellBook;
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
public class ModItems {
//    @ObjectHolder("ars_nouveau:spell_book")
//    public static SpellBook spellBook;
//
//    @ObjectHolder("ars_noveau:test")
//    public static Test test;

    public static SpellBook spellBook = Null();

    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler{
        public static final Set<Item> ITEMS = new HashSet<>();

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            spellBook = new SpellBook();
            final AdvancedSpellBook advancedSpellBook = new AdvancedSpellBook();

            final MagicArmor noviceBoots = new NoviceArmor(EquipmentSlotType.FEET);
            final MagicArmor novicePants = new NoviceArmor(EquipmentSlotType.LEGS);
            final MagicArmor noviceChest = new NoviceArmor(EquipmentSlotType.CHEST);
            final MagicArmor noviceHead = new NoviceArmor(EquipmentSlotType.HEAD);

            final MagicArmor apprenticeBoots = new ApprenticeArmor(EquipmentSlotType.FEET);
            final MagicArmor apprenticePants = new ApprenticeArmor(EquipmentSlotType.LEGS);
            final MagicArmor apprenticeChest = new ApprenticeArmor(EquipmentSlotType.CHEST);
            final MagicArmor apprenticeHead = new ApprenticeArmor(EquipmentSlotType.HEAD);

            final MagicArmor masterBoots = new MasterArmor(EquipmentSlotType.FEET);
            final MagicArmor masterPants = new MasterArmor(EquipmentSlotType.LEGS);
            final MagicArmor masterChest = new MasterArmor(EquipmentSlotType.CHEST);
            final MagicArmor masterHead = new MasterArmor(EquipmentSlotType.HEAD);

            final Item[] items = {
                    noviceBoots.setRegistryName("novice_boots"),
                    novicePants.setRegistryName("novice_legs"),
                    noviceChest.setRegistryName("novice_chest"),
                    noviceHead.setRegistryName("novice_head"),
                    apprenticeBoots.setRegistryName("apprentice_boots"),
                    apprenticePants.setRegistryName("apprentice_pants"),
                    apprenticeChest.setRegistryName("apprentice_chest"),
                    apprenticeHead.setRegistryName("apprentice_head"),
                    masterBoots.setRegistryName("master_boots"),
                    masterPants.setRegistryName("master_pants"),
                    masterChest.setRegistryName("master_chest"),
                    masterHead.setRegistryName("master_head"),
                    spellBook.setRegistryName("spell_book"),
                    advancedSpellBook.setRegistryName("advanced_spell_book")
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

