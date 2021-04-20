package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.common.armor.ApprenticeArmor;
import com.hollingsworth.arsnouveau.common.armor.MasterArmor;
import com.hollingsworth.arsnouveau.common.armor.NoviceArmor;
import com.hollingsworth.arsnouveau.common.items.*;
import com.hollingsworth.arsnouveau.common.items.curios.*;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

import static com.hollingsworth.arsnouveau.setup.InjectionUtil.Null;

@ObjectHolder(ArsNouveau.MODID)
public class ItemsRegistry {
    @ObjectHolder(LibItemNames.RUNIC_CHALK)public static RunicChalk runicChalk;
    @ObjectHolder(LibItemNames.NOVICE_SPELL_BOOK) public static SpellBook noviceSpellBook;

    @ObjectHolder(LibItemNames.APPRENTICE_SPELL_BOOK) public static SpellBook apprenticeSpellBook;
    @ObjectHolder(LibItemNames.ARCHMAGE_SPELL_BOOK) public static SpellBook archmageSpellBook;
    @ObjectHolder(LibItemNames.CREATIVE_SPELL_BOOK) public static SpellBook creativeSpellBook;


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

    @ObjectHolder(LibItemNames.RING_OF_LESSER_DISCOUNT) public static DiscountRing ringOfLesserDiscount;


    @ObjectHolder(LibItemNames.RING_OF_GREATER_DISCOUNT) public static DiscountRing ringOfGreaterDiscount;

    @ObjectHolder(LibItemNames.BELT_OF_UNSTABLE_GIFTS) public static BeltOfUnstableGifts beltOfUnstableGifts;

    @ObjectHolder(LibItemNames.WARP_SCROLL) public static WarpScroll warpScroll;

    @ObjectHolder(LibItemNames.SPELL_PARCHMENT) public static SpellParchment spellParchment;

    @ObjectHolder(LibItemNames.WHELP_CHARM) public static WhelpCharm whelpCharm;
    @ObjectHolder(LibItemNames.DOMINION_WAND) public static DominionWand DOMINION_ROD;

    @ObjectHolder(LibItemNames.AMULET_OF_MANA_BOOST)public static AbstractManaCurio amuletOfManaBoost;
    @ObjectHolder(LibItemNames.AMULET_OF_MANA_REGEN)public static AbstractManaCurio amuletOfManaRegen;
    @ObjectHolder(LibItemNames.DULL_TRINKET)public static ModItem dullTrinket;
    @ObjectHolder(LibItemNames.CARBUNCLE_CHARM) public static CarbuncleCharm carbuncleCharm;
    @ObjectHolder(LibItemNames.DOMINION_WAND)public static DominionWand dominionWand;
    @ObjectHolder("debug")public static Debug debug;

    @ObjectHolder(LibItemNames.CARBUNCLE_SHARD)public static ModItem carbuncleShard;


    @ObjectHolder(LibItemNames.EARTH_ELEMENTAL_SHARD)public static ModItem earthElementalShard;
    @ObjectHolder(LibItemNames.SYLPH_CHARM)public static SylphCharm sylphCharm;
    @ObjectHolder(LibItemNames.SYLPH_SHARD)public static ModItem sylphShard;
    @ObjectHolder(LibItemNames.MANA_GEM)public static ModItem manaGem;
    @ObjectHolder(LibItemNames.ALLOW_ITEM_SCROLL)public static ItemScroll ALLOW_ITEM_SCROLL;
    @ObjectHolder(LibItemNames.DENY_ITEM_SCROLL)public static ItemScroll DENY_ITEM_SCROLL;
    @ObjectHolder(LibItemNames.BLANK_PARCHMENT)public static ModItem BLANK_PARCHMENT;
    @ObjectHolder(LibItemNames.WAND)public static Wand WAND;
    @ObjectHolder(LibItemNames.VOID_JAR)public static VoidJar VOID_JAR;
    @ObjectHolder(LibItemNames.WIXIE_CHARM)public static WixieCharm WIXIE_CHARM;
    @ObjectHolder(LibItemNames.WIXIE_SHARD)public static ModItem WIXIE_SHARD;
    @ObjectHolder(LibItemNames.RITUAL_BOOK)public static RitualBook RITUAL_BOOK;
    @ObjectHolder(LibItemNames.SPELL_BOW)public static SpellBow SPELL_BOW;

    @ObjectHolder(LibItemNames.AMPLIFY_ARROW)public static SpellArrow AMPLIFY_ARROW;
    @ObjectHolder(LibItemNames.SPLIT_ARROW)public static SpellArrow SPLIT_ARROW;
    @ObjectHolder(LibItemNames.PIERCE_ARROW)public static SpellArrow PIERCE_ARROW;

    @ObjectHolder(LibItemNames.WILDEN_HORN)public static ModItem WILDEN_HORN;
    @ObjectHolder(LibItemNames.WILDEN_SPIKE)public static ModItem WILDEN_SPIKE;
    @ObjectHolder(LibItemNames.WILDEN_WING)public static ModItem WILDEN_WING;


    @ObjectHolder(LibItemNames.POTION_FLASK)public static PotionFlask POTION_FLASK;
    @ObjectHolder(LibItemNames.POTION_FLASK_AMPLIFY)public static PotionFlask POTION_FLASK_AMPLIFY;
    @ObjectHolder(LibItemNames.POTION_FLASK_EXTEND_TIME)public static PotionFlask POTION_FLASK_EXTEND_TIME;

    public static Food MANA_BERRY_FOOD = (new Food.Builder()).nutrition(2).saturationMod(0.1F).effect(() -> new EffectInstance(ModPotions.MANA_REGEN_EFFECT, 100), 1.0f).alwaysEat().build();

    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler{
        public static final Set<Item> ITEMS = new HashSet<>();

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {

            Item[] items = {
                    new Debug(),
                    new WhelpCharm(),
                    new DominionWand(),
                    new RunicChalk(),
                    new ModItem(LibItemNames.BLANK_GLYPH),
                    new ModItem(LibItemNames.DULL_TRINKET),
                    new ModItem(LibItemNames.MARVELOUS_CLAY),
                    new ModItem(LibItemNames.MAGIC_CLAY),
                    new ModItem(LibItemNames.MYTHICAL_CLAY),
                    new ModItem(LibItemNames.BLAZE_FIBER),
                    new ModItem(LibItemNames.END_FIBER),
                    new CompostableItem(LibItemNames.MANA_BLOOM, 0.65F),
                    new ModItem(LibItemNames.MANA_FIBER),
                    new ModItem(LibItemNames.MUNDANE_BELT),
                    new ModItem(LibItemNames.ARCANE_BRICK).withTooltip(new TranslationTextComponent("tooltip.arcane_brick")),
                    new ModItem(LibItemNames.RING_OF_POTENTIAL),
                    new BeltOfUnstableGifts(LibItemNames.BELT_OF_UNSTABLE_GIFTS),
                    new ModItem(defaultItemProperties().stacksTo(1), LibItemNames.BUCKET_OF_MANA),
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
                    new SpellBook(ISpellTier.Tier.THREE).setRegistryName(LibItemNames.CREATIVE_SPELL_BOOK),
                    new RingOfAmplify(),
                    new BeltOfLevitation(),
                    new WarpScroll(),
                    new JarOfLight(),
                    new WornNotebook().withTooltip(new TranslationTextComponent("tooltip.worn_notebook")),
                    new CarbuncleCharm(),
                    new ModItem(LibItemNames.CARBUNCLE_SHARD).withTooltip(new TranslationTextComponent("tooltip.carbuncle_shard")),
                    new WixieCharm(),
                    new DiscountRing(LibItemNames.RING_OF_LESSER_DISCOUNT) {
                        @Override
                        public int getManaDiscount() {
                            return 10;
                        }
                    },
                    new DiscountRing(LibItemNames.RING_OF_GREATER_DISCOUNT) {
                        @Override
                        public int getManaDiscount() {
                            return 20;
                        }
                    },
                    new SpellParchment(),
                    new AbstractManaCurio(LibItemNames.AMULET_OF_MANA_BOOST){
                        @Override
                        public int getMaxManaBoost() {
                            return 50;
                        }
                    },
                    new AbstractManaCurio(LibItemNames.AMULET_OF_MANA_REGEN){
                        @Override
                        public int getManaRegenBonus() {
                            return 3;
                        }
                    },
                    new ModItem(LibItemNames.SYLPH_SHARD).withTooltip(new TranslationTextComponent("tooltip.sylph_shard")),
                    new SylphCharm(),
                    new ModItem(LibItemNames.MANA_GEM).withTooltip(new TranslationTextComponent("tooltip.mana_gem")),
                    new ItemScroll(LibItemNames.ALLOW_ITEM_SCROLL),
                    new ItemScroll(LibItemNames.DENY_ITEM_SCROLL),
                    new ModItem(LibItemNames.BLANK_PARCHMENT),
                    new ModItem(LibItemNames.WIXIE_SHARD).withTooltip(new TranslationTextComponent("tooltip.wixie_shard")),
                    new Wand(),
                    new VoidJar(),
                    new RitualBook().setRegistryName(LibItemNames.RITUAL_BOOK),
                    new SpellBow().setRegistryName(LibItemNames.SPELL_BOW),
                    new FormSpellArrow(LibItemNames.PIERCE_ARROW, new AugmentPierce(), 2),
                    new FormSpellArrow(LibItemNames.SPLIT_ARROW, new AugmentSplit(), 2),
                    new SpellArrow(LibItemNames.AMPLIFY_ARROW, new AugmentAmplify(), 2),
                    new ModItem(LibItemNames.WILDEN_HORN).withTooltip(new TranslationTextComponent("tooltip.wilden_horn")),
                    new ModItem(LibItemNames.WILDEN_WING).withTooltip(new TranslationTextComponent("tooltip.wilden_wing")),
                    new ModItem(LibItemNames.WILDEN_SPIKE).withTooltip(new TranslationTextComponent("tooltip.wilden_spike")),
                    new PotionFlask() {
                        @Nonnull
                        @Override
                        public EffectInstance getEffectInstance(EffectInstance effectInstance) {
                            return effectInstance;
                        }
                    }.withTooltip(new TranslationTextComponent("tooltip.potion_flask")),
                    new PotionFlask(LibItemNames.POTION_FLASK_EXTEND_TIME) {
                        @Override
                        public EffectInstance getEffectInstance(EffectInstance effectInstance) {
                            return new EffectInstance(effectInstance.getEffect(), effectInstance.getDuration() + effectInstance.getDuration()/2, effectInstance.getAmplifier());
                        }
                    }.withTooltip(new TranslationTextComponent("tooltip.potion_flask_extend_time")),
                    new PotionFlask(LibItemNames.POTION_FLASK_AMPLIFY) {
                        @Override
                        public EffectInstance getEffectInstance(EffectInstance effectInstance) {
                            return new EffectInstance(effectInstance.getEffect(), effectInstance.getDuration()/2, effectInstance.getAmplifier() + 1);
                        }
                    }.withTooltip(new TranslationTextComponent("tooltip.potion_flask_amplify"))

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
        return new Item.Properties().tab(ArsNouveau.itemGroup);
    }
}

