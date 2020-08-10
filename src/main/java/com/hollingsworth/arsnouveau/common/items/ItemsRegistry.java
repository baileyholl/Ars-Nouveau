package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.common.armor.ApprenticeArmor;
import com.hollingsworth.arsnouveau.common.armor.MagicArmor;
import com.hollingsworth.arsnouveau.common.armor.MasterArmor;
import com.hollingsworth.arsnouveau.common.armor.NoviceArmor;
import com.hollingsworth.arsnouveau.common.items.curios.BeltOfLevitation;
import com.hollingsworth.arsnouveau.common.items.curios.GreaterDiscountRing;
import com.hollingsworth.arsnouveau.common.items.curios.LesserDiscountRing;
import com.hollingsworth.arsnouveau.common.items.curios.RingOfAmplify;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashSet;
import java.util.Set;

import static com.hollingsworth.arsnouveau.setup.InjectionUtil.Null;

@ObjectHolder(ArsNouveau.MODID)
public class ItemsRegistry {

    @ObjectHolder(LibItemNames.NOVICE_SPELL_BOOK) public static SpellBook noviceSpellBook;

    @ObjectHolder(LibItemNames.APPRENTICE_SPELL_BOOK) public static SpellBook apprenticeSpellBook;
    @ObjectHolder(LibItemNames.ARCHMAGE_SPELL_BOOK) public static SpellBook archmageSpellBook;


    @ObjectHolder(LibItemNames.BLANK_GLYPH) public static  Item blankGlyph;
    @ObjectHolder(LibItemNames.BUCKET_OF_MANA) public static ModItem bucketOfMana;

    @ObjectHolder(LibItemNames.MAGIC_CLAY) public static ModItem magicClay;
    @ObjectHolder(LibItemNames.MARVELOUS_CLAY) public static ModItem marvelousClay;
    @ObjectHolder(LibItemNames.MYTHICAL_CLAY) public static ModItem mythicalClay;

    @ObjectHolder(LibItemNames.ARCANE_BRICK) public static ModItem arcaneBrick;
    @ObjectHolder(LibItemNames.MANA_BLOOM) public static ModItem manaBloom;


    @ObjectHolder(LibItemNames.MANA_FIBER) public static ModItem manaFiber;
    @ObjectHolder(LibItemNames.BLAZE_FIBER) public static ModItem blazeFiber;
    @ObjectHolder(LibItemNames.END_FIBER) public static ModItem endFiber;

    @ObjectHolder(LibItemNames.MUNDANE_BELT) public static ModItem mundaneBelt;
    @ObjectHolder(LibItemNames.JAR_OF_LIGHT) public static JarOfLight jarOfLight;

    @ObjectHolder(LibItemNames.BELT_OF_LEVITATION)public static BeltOfLevitation beltOfLevitation;

    @ObjectHolder(LibItemNames.WORN_NOTEBOOK) public static WornNotebook wornNotebook = Null();

    @ObjectHolder(LibItemNames.RING_OF_POTENTIAL) public  static ModItem ringOfPotential;

    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler{
        public static final Set<Item> ITEMS = new HashSet<>();

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {

            Item[] items = {
                    new ModItem(LibItemNames.BLANK_GLYPH),
                    new ModItem(LibItemNames.MARVELOUS_CLAY),
                    new ModItem(LibItemNames.MAGIC_CLAY),
                    new ModItem(LibItemNames.MYTHICAL_CLAY),
                    new ModItem(LibItemNames.BLAZE_FIBER),
                    new ModItem(LibItemNames.END_FIBER),
                    new ModItem(LibItemNames.MANA_BLOOM),
                    new ModItem(LibItemNames.MANA_FIBER),
                    new ModItem(LibItemNames.MUNDANE_BELT),
                    new ModItem(LibItemNames.ARCANE_BRICK),
                    new ModItem(LibItemNames.RING_OF_POTENTIAL),
                    new ModItem(new Item.Properties().group(ArsNouveau.itemGroup).maxStackSize(1), "bucket_of_mana"),
                    new NoviceArmor(EquipmentSlotType.FEET).setRegistryName("novice_boots"),
                    new NoviceArmor(EquipmentSlotType.LEGS).setRegistryName("novice_leggings"),
                    new NoviceArmor(EquipmentSlotType.CHEST).setRegistryName("novice_robes"),
                    new NoviceArmor(EquipmentSlotType.HEAD).setRegistryName("novice_hood"),
                    new ApprenticeArmor(EquipmentSlotType.FEET).setRegistryName("apprentice_boots"),
                    new ApprenticeArmor(EquipmentSlotType.LEGS).setRegistryName("apprentice_leggings"),
                    new ApprenticeArmor(EquipmentSlotType.CHEST).setRegistryName("apprentice_robes"),
                    new ApprenticeArmor(EquipmentSlotType.HEAD).setRegistryName("apprentice_hood"),
                    new MasterArmor(EquipmentSlotType.FEET).setRegistryName("archmage_boots"),
                    new MasterArmor(EquipmentSlotType.LEGS).setRegistryName("archmage_leggings"),
                    new MasterArmor(EquipmentSlotType.CHEST).setRegistryName("archmage_robes"),
                    new MasterArmor(EquipmentSlotType.HEAD).setRegistryName("archmage_hood"),
                    new SpellBook(ISpellTier.Tier.ONE).setRegistryName(LibItemNames.NOVICE_SPELL_BOOK),
                    new SpellBook(ISpellTier.Tier.TWO).setRegistryName(LibItemNames.APPRENTICE_SPELL_BOOK),
                    new SpellBook(ISpellTier.Tier.THREE).setRegistryName(LibItemNames.ARCHMAGE_SPELL_BOOK),
                    new RingOfAmplify(),
                    new BeltOfLevitation(),
                    new LesserDiscountRing(),
                    new GreaterDiscountRing(),
                    new JarOfLight(),
                    new WornNotebook()
            };

            final IForgeRegistry<Item> registry = event.getRegistry();
            for(Glyph glyph : ArsNouveauAPI.getInstance().getGlyphMap().values()){
                registry.register(glyph);
                ITEMS.add(glyph);
            }
            for (final Item item : items) {
                registry.register(item);
                ITEMS.add(item);
            }
        }
    }

    public static Item.Properties defaultItemProperties() {
        return new Item.Properties().group(ArsNouveau.itemGroup);
    }
}

