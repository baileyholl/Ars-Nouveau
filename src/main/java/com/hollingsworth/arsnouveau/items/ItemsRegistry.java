package com.hollingsworth.arsnouveau.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.armor.ApprenticeArmor;
import com.hollingsworth.arsnouveau.armor.MagicArmor;
import com.hollingsworth.arsnouveau.armor.MasterArmor;
import com.hollingsworth.arsnouveau.armor.NoviceArmor;
import com.hollingsworth.arsnouveau.items.curios.BeltOfLevitation;
import com.hollingsworth.arsnouveau.items.curios.GreaterDiscountRing;
import com.hollingsworth.arsnouveau.items.curios.LesserDiscountRing;
import com.hollingsworth.arsnouveau.items.curios.RingOfAmplify;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashSet;
import java.util.Set;

import static com.hollingsworth.arsnouveau.InjectionUtil.Null;

@ObjectHolder(ArsNouveau.MODID)
public class ItemsRegistry {
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
            JarOfLight jarOfLight = new JarOfLight();
            final ApprenticeSpellBook apprenticeSpellBook = new ApprenticeSpellBook();
            final ArchmageSpellBook archmageSpellBook = new ArchmageSpellBook();


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

            final RingOfAmplify ringOfAmplify = new RingOfAmplify();
            final BeltOfLevitation beltOfLevitation = new BeltOfLevitation();


            final LesserDiscountRing lesserDiscountRing = new LesserDiscountRing();
            final GreaterDiscountRing greaterDiscountRing = new GreaterDiscountRing();

            final Item[] items = {
                    noviceBoots.setRegistryName("novice_boots"),
                    novicePants.setRegistryName("novice_leggings"),
                    noviceChest.setRegistryName("novice_robes"),
                    noviceHead.setRegistryName("novice_hood"),
                    apprenticeBoots.setRegistryName("apprentice_boots"),
                    apprenticePants.setRegistryName("apprentice_leggings"),
                    apprenticeChest.setRegistryName("apprentice_robes"),
                    apprenticeHead.setRegistryName("apprentice_hood"),
                    masterBoots.setRegistryName("archmage_boots"),
                    masterPants.setRegistryName("archmage_leggings"),
                    masterChest.setRegistryName("archmage_robes"),
                    masterHead.setRegistryName("archmage_hood"),
                    spellBook.setRegistryName("novice_spell_book"),
                    apprenticeSpellBook.setRegistryName("apprentice_spell_book"),
                    archmageSpellBook.setRegistryName("archmage_spell_book"),
                    ringOfAmplify,
                    beltOfLevitation,
                    lesserDiscountRing,
                    jarOfLight,
                    greaterDiscountRing,
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

