package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.common.armor.ApprenticeArmor;
import com.hollingsworth.arsnouveau.common.armor.MasterArmor;
import com.hollingsworth.arsnouveau.common.armor.NoviceArmor;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.items.*;
import com.hollingsworth.arsnouveau.common.items.curios.*;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.AllowItemScroll;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.DenyItemScroll;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.MimicItemScroll;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Tiers;
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
    @ObjectHolder(LibItemNames.RUNIC_CHALK)public static RunicChalk RUNIC_CHALK;
    @ObjectHolder(LibItemNames.NOVICE_SPELL_BOOK) public static SpellBook NOVICE_SPELLBOOK;

    @ObjectHolder(LibItemNames.APPRENTICE_SPELL_BOOK) public static SpellBook APPRENTICE_SPELLBOOK;
    @ObjectHolder(LibItemNames.ARCHMAGE_SPELL_BOOK) public static SpellBook ARCHMAGE_SPELLBOOK;
    @ObjectHolder(LibItemNames.CREATIVE_SPELL_BOOK) public static SpellBook CREATIVE_SPELLBOOK;


    @ObjectHolder(LibItemNames.BLANK_GLYPH) public static  Item BLANK_GLYPH;
    @ObjectHolder(LibItemNames.BUCKET_OF_SOURCE) public static ModItem BUCKET_OF_SOURCE;

    @ObjectHolder(LibItemNames.MAGIC_CLAY) public static ModItem MAGIC_CLAY;
    @ObjectHolder(LibItemNames.MARVELOUS_CLAY) public static ModItem MARVELOUS_CLAY;
    @ObjectHolder(LibItemNames.MYTHICAL_CLAY) public static ModItem MYTHICAL_CLAY;

    @ObjectHolder(LibItemNames.MAGE_BLOOM) public static ModItem MAGE_BLOOM;


    @ObjectHolder(LibItemNames.MAGE_FIBER) public static ModItem MAGE_FIBER;
    @ObjectHolder(LibItemNames.BLAZE_FIBER) public static ModItem BLAZE_FIBER;
    @ObjectHolder(LibItemNames.END_FIBER) public static ModItem END_FIBER;

    @ObjectHolder(LibItemNames.MUNDANE_BELT) public static ModItem MUNDANE_BELT;
    @ObjectHolder(LibItemNames.JAR_OF_LIGHT) public static JarOfLight JAR_OF_LIGHT;

    @ObjectHolder(LibItemNames.BELT_OF_LEVITATION)public static BeltOfLevitation BELT_OF_LEVITATION;

    @ObjectHolder(LibItemNames.WORN_NOTEBOOK) public static WornNotebook WORN_NOTEBOOK = Null();

    @ObjectHolder(LibItemNames.RING_OF_POTENTIAL) public  static ModItem RING_OF_POTENTIAL;

    @ObjectHolder(LibItemNames.RING_OF_LESSER_DISCOUNT) public static DiscountRing RING_OF_LESSER_DISCOUNT;


    @ObjectHolder(LibItemNames.RING_OF_GREATER_DISCOUNT) public static DiscountRing RING_OF_GREATER_DISCOUNT;

    @ObjectHolder(LibItemNames.BELT_OF_UNSTABLE_GIFTS) public static BeltOfUnstableGifts BELT_OF_UNSTABLE_GIFTS;

    @ObjectHolder(LibItemNames.WARP_SCROLL) public static WarpScroll WARP_SCROLL;

    @ObjectHolder(LibItemNames.SPELL_PARCHMENT) public static SpellParchment SPELL_PARCHMENT;

    @ObjectHolder(LibItemNames.BOOKWYRM_CHARM) public static BookwyrmCharm BOOKWYRM_CHARM;
    @ObjectHolder(LibItemNames.DOMINION_WAND) public static DominionWand DOMINION_ROD;

    @ObjectHolder(LibItemNames.AMULET_OF_MANA_BOOST)public static AbstractManaCurio AMULET_OF_MANA_BOOST;
    @ObjectHolder(LibItemNames.AMULET_OF_MANA_REGEN)public static AbstractManaCurio AMULET_OF_MANA_REGEN;
    @ObjectHolder(LibItemNames.DULL_TRINKET)public static ModItem DULL_TRINKET;
    @ObjectHolder(LibItemNames.STARBUNCLE_CHARM) public static StarbuncleCharm STARBUNCLE_CHARM;
    @ObjectHolder(LibItemNames.DOMINION_WAND)public static DominionWand DOMINION_WAND;
    @ObjectHolder("debug")public static Debug debug;

    @ObjectHolder(LibItemNames.STARBUNCLE_SHARDS)public static ModItem STARBUNCLE_SHARD;


    @ObjectHolder(LibItemNames.WHIRLISPRIG_CHARM)public static WhirlisprigCharm WHIRLISPRIG_CHARM;
    @ObjectHolder(LibItemNames.WHIRLISPRIG_SHARDS)public static ModItem WHIRLISPRIG_SHARDS;
    @ObjectHolder(LibItemNames.SOURCE_GEM)public static ModItem SOURCE_GEM;
    @ObjectHolder(LibItemNames.ALLOW_ITEM_SCROLL)public static AllowItemScroll ALLOW_ITEM_SCROLL;
    @ObjectHolder(LibItemNames.DENY_ITEM_SCROLL)public static DenyItemScroll DENY_ITEM_SCROLL;
    @ObjectHolder(LibItemNames.MIMIC_ITEM_SCROLL)public static MimicItemScroll MIMIC_ITEM_SCROLL;


    @ObjectHolder(LibItemNames.BLANK_PARCHMENT)public static ModItem BLANK_PARCHMENT;
    @ObjectHolder(LibItemNames.WAND)public static Wand WAND;
    @ObjectHolder(LibItemNames.VOID_JAR)public static VoidJar VOID_JAR;
    @ObjectHolder(LibItemNames.WIXIE_CHARM)public static WixieCharm WIXIE_CHARM;
    @ObjectHolder(LibItemNames.WIXIE_SHARD)public static ModItem WIXIE_SHARD;
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
    @ObjectHolder(LibItemNames.EXP_GEM)public static ExperienceGem EXPERIENCE_GEM;
    @ObjectHolder(LibItemNames.GREATER_EXP_GEM)public static ExperienceGem GREATER_EXPERIENCE_GEM;

    @ObjectHolder(LibItemNames.ENCHANTERS_SWORD)public static EnchantersSword ENCHANTERS_SWORD;
    @ObjectHolder(LibItemNames.ENCHANTERS_SHIELD)public static EnchantersShield ENCHANTERS_SHIELD;

    @ObjectHolder(LibItemNames.CASTER_TOME)public static CasterTome CASTER_TOME;
    @ObjectHolder(LibItemNames.DRYGMY_CHARM)public static DrygmyCharm DRYGMY_CHARM;
    @ObjectHolder(LibItemNames.DRYGMY_SHARD)public static ModItem DRYGMY_SHARD;
    @ObjectHolder(LibItemNames.WILDEN_TRIBUTE)public static ModItem WILDEN_TRIBUTE;
    @ObjectHolder(LibItemNames.SUMMON_FOCUS)public static SummoningFocus SUMMONING_FOCUS;
    @ObjectHolder(LibItemNames.SOURCE_BERRY_PIE)public static ModItem SOURCE_BERRY_PIE;
    @ObjectHolder(LibItemNames.SOURCE_BERRY_ROLL)public static ModItem SOURCE_BERRY_ROLL;
    @ObjectHolder(LibItemNames.ENCHANTERS_MIRROR)public static EnchantersMirror ENCHANTERS_MIRROR;

    public static FoodProperties SOURCE_BERRY_FOOD = (new FoodProperties.Builder()).nutrition(2).saturationMod(0.1F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 100), 1.0f).alwaysEat().build();
    public static FoodProperties SOURCE_PIE_FOOD = (new FoodProperties.Builder()).nutrition(9).saturationMod(0.9F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 60 * 20, 1), 1.0f).alwaysEat().build();
    public static FoodProperties SOURCE_ROLL_FOOD = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.6F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 60 * 20), 1.0f).alwaysEat().build();

    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler{
        public static final Set<Item> ITEMS = new HashSet<>();

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {

            Item[] items = {
                    new Debug(),
                    new BookwyrmCharm(),
                    new DominionWand(),
                    new RunicChalk(),
                    new ModItem(LibItemNames.BLANK_GLYPH),
                    new ModItem(LibItemNames.DULL_TRINKET).withTooltip(new TranslatableComponent("ars_nouveau.tooltip.dull")),
                    new ModItem(LibItemNames.MARVELOUS_CLAY),
                    new ModItem(LibItemNames.MAGIC_CLAY),
                    new ModItem(LibItemNames.MYTHICAL_CLAY),
                    new ModItem(LibItemNames.BLAZE_FIBER),
                    new ModItem(LibItemNames.END_FIBER),
                    new CompostableItem(LibItemNames.MAGE_BLOOM, 0.65F).withTooltip(new TranslatableComponent("ars_nouveau.tooltip.magebloom")),
                    new ModItem(LibItemNames.MAGE_FIBER),
                    new ModItem(LibItemNames.MUNDANE_BELT).withTooltip(new TranslatableComponent("ars_nouveau.tooltip.dull")),
                    new ModItem(LibItemNames.RING_OF_POTENTIAL).withTooltip(new TranslatableComponent("ars_nouveau.tooltip.dull")),
                    new BeltOfUnstableGifts(LibItemNames.BELT_OF_UNSTABLE_GIFTS),
                    new ModItem(defaultItemProperties().stacksTo(1), LibItemNames.BUCKET_OF_SOURCE),
                    new NoviceArmor(EquipmentSlot.FEET).setRegistryName("novice_boots"),
                    new NoviceArmor(EquipmentSlot.LEGS).setRegistryName("novice_leggings"),
                    new NoviceArmor(EquipmentSlot.CHEST).setRegistryName("novice_robes"),
                    new NoviceArmor(EquipmentSlot.HEAD).setRegistryName("novice_hood"),
                    new ApprenticeArmor(EquipmentSlot.FEET).setRegistryName("apprentice_boots"),
                    new ApprenticeArmor(EquipmentSlot.LEGS).setRegistryName("apprentice_leggings"),
                    new ApprenticeArmor(EquipmentSlot.CHEST).setRegistryName("apprentice_robes"),
                    new ApprenticeArmor(EquipmentSlot.HEAD).setRegistryName("apprentice_hood"),
                    new MasterArmor(EquipmentSlot.FEET).setRegistryName("archmage_boots"),
                    new MasterArmor(EquipmentSlot.LEGS).setRegistryName("archmage_leggings"),
                    new MasterArmor(EquipmentSlot.CHEST).setRegistryName("archmage_robes"),
                    new MasterArmor(EquipmentSlot.HEAD).setRegistryName("archmage_hood"),
                    new SpellBook(ISpellTier.Tier.ONE).setRegistryName(LibItemNames.NOVICE_SPELL_BOOK),
                    new SpellBook(ISpellTier.Tier.TWO).setRegistryName(LibItemNames.APPRENTICE_SPELL_BOOK),
                    new SpellBook(ISpellTier.Tier.THREE).setRegistryName(LibItemNames.ARCHMAGE_SPELL_BOOK),
                    new SpellBook(ISpellTier.Tier.THREE).setRegistryName(LibItemNames.CREATIVE_SPELL_BOOK),
                    new BeltOfLevitation(),
                    new WarpScroll(),
                    new JarOfLight(),
                    new WornNotebook().withTooltip(new TranslatableComponent("tooltip.worn_notebook")),
                    new StarbuncleCharm(),
                    new ModItem(LibItemNames.STARBUNCLE_SHARDS).withTooltip(new TranslatableComponent("tooltip.starbuncle_shard")),
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
                    new ModItem(LibItemNames.WHIRLISPRIG_SHARDS).withTooltip(new TranslatableComponent("tooltip.whirlisprig_shard")),
                    new WhirlisprigCharm(),
                    new ModItem(LibItemNames.SOURCE_GEM).withTooltip(new TranslatableComponent("tooltip.source_gem")),
                    new AllowItemScroll(LibItemNames.ALLOW_ITEM_SCROLL),
                    new DenyItemScroll(LibItemNames.DENY_ITEM_SCROLL),
                    new MimicItemScroll(LibItemNames.MIMIC_ITEM_SCROLL),
                    new ModItem(LibItemNames.BLANK_PARCHMENT),
                    new ModItem(LibItemNames.WIXIE_SHARD).withTooltip(new TranslatableComponent("tooltip.wixie_shard")),
                    new Wand(),
                    new VoidJar(),
                    new SpellBow().setRegistryName(LibItemNames.SPELL_BOW),
                    new FormSpellArrow(LibItemNames.PIERCE_ARROW, AugmentPierce.INSTANCE, 2),
                    new FormSpellArrow(LibItemNames.SPLIT_ARROW, AugmentSplit.INSTANCE, 2),
                    new SpellArrow(LibItemNames.AMPLIFY_ARROW, AugmentAmplify.INSTANCE, 2),
                    new ModItem(LibItemNames.WILDEN_HORN).withTooltip(new TranslatableComponent("tooltip.wilden_horn")),
                    new ModItem(LibItemNames.WILDEN_WING).withTooltip(new TranslatableComponent("tooltip.wilden_wing")),
                    new ModItem(LibItemNames.WILDEN_SPIKE).withTooltip(new TranslatableComponent("tooltip.wilden_spike")),
                    new PotionFlask() {
                        @Nonnull
                        @Override
                        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                            return effectInstance;
                        }
                    }.withTooltip(new TranslatableComponent("tooltip.potion_flask")),
                    new PotionFlask(LibItemNames.POTION_FLASK_EXTEND_TIME) {
                        @Override
                        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                            return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() + effectInstance.getDuration()/2, effectInstance.getAmplifier());
                        }
                    }.withTooltip(new TranslatableComponent("tooltip.potion_flask_extend_time")),
                    new PotionFlask(LibItemNames.POTION_FLASK_AMPLIFY) {
                        @Override
                        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                            return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration()/2, effectInstance.getAmplifier() + 1);
                        }
                    }.withTooltip(new TranslatableComponent("tooltip.potion_flask_amplify")),
                    new ExperienceGem(defaultItemProperties(), LibItemNames.EXP_GEM) {
                        @Override
                        public int getValue() {
                            return 3;
                        }
                    }.withTooltip(new TranslatableComponent("ars_nouveau.tooltip.exp_gem")),
                    new ExperienceGem(defaultItemProperties(), LibItemNames.GREATER_EXP_GEM) {
                        @Override
                        public int getValue() {
                            return 12;
                        }
                    }.withTooltip(new TranslatableComponent("ars_nouveau.tooltip.exp_gem")),
                    new EnchantersShield(),
                    new EnchantersSword(Tiers.NETHERITE, 3, -2.4F).setRegistryName(LibItemNames.ENCHANTERS_SWORD),
                    new SpawnEggItem(ModEntities.STARBUNCLE_TYPE, 0xFFB233,0xFFE633,defaultItemProperties()).setRegistryName(LibItemNames.STARBUNCLE_SE),
                    new SpawnEggItem(ModEntities.WHIRLISPRIG_TYPE, 0x77FF33,0xFFFB00,defaultItemProperties()).setRegistryName(LibItemNames.SYLPH_SE),
                    new SpawnEggItem(ModEntities.WILDEN_HUNTER, 0xFDFDFD,0xCAA97F,defaultItemProperties()).setRegistryName(LibItemNames.WILDEN_HUNTER_SE),
                    new SpawnEggItem(ModEntities.WILDEN_GUARDIAN, 0xFFFFFF,0xFF9E00,defaultItemProperties()).setRegistryName(LibItemNames.WILDEN_GUARDIAN_SE),
                    new SpawnEggItem(ModEntities.WILDEN_STALKER, 0x9B650C,0xEF1818,defaultItemProperties()).setRegistryName(LibItemNames.WILDEN_STALKER_SE),
                    new CasterTome(defaultItemProperties().stacksTo(1), LibItemNames.CASTER_TOME),
                    new DrygmyCharm(LibItemNames.DRYGMY_CHARM),
                    new ModItem(LibItemNames.DRYGMY_SHARD).withTooltip(new TranslatableComponent("tooltip.ars_nouveau.drygmy_shard")),
                    new ModItem(defaultItemProperties().fireResistant(), LibItemNames.WILDEN_TRIBUTE).withRarity(Rarity.EPIC)
                            .withTooltip(new TranslatableComponent("tooltip.ars_nouveau.wilden_tribute")
                            .withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE))),
                    new SummoningFocus(defaultItemProperties().stacksTo(1), LibItemNames.SUMMON_FOCUS),
                    new ModItem(defaultItemProperties().food(SOURCE_PIE_FOOD), LibItemNames.SOURCE_BERRY_PIE).withTooltip(new TranslatableComponent("tooltip.ars_nouveau.source_food")),
                    new ModItem(defaultItemProperties().food(SOURCE_ROLL_FOOD), LibItemNames.SOURCE_BERRY_ROLL).withTooltip(new TranslatableComponent("tooltip.ars_nouveau.source_food")),
                    new EnchantersMirror(defaultItemProperties().stacksTo(1), LibItemNames.ENCHANTERS_MIRROR)
            };

            final IForgeRegistry<Item> registry = event.getRegistry();
            for(Glyph glyph : ArsNouveauAPI.getInstance().getGlyphMap().values()){
                registry.register(glyph);
                ITEMS.add(glyph);
            }

            for(RitualTablet ritualParchment : ArsNouveauAPI.getInstance().getRitualItemMap().values()){
                registry.register(ritualParchment);
                ITEMS.add(ritualParchment);
            }

            for(FamiliarScript script : ArsNouveauAPI.getInstance().getFamiliarScriptMap().values()){
                registry.register(script);
                ITEMS.add(script);
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

