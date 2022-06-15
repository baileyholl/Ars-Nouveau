package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.setup.InjectionUtil.Null;

public class ItemsRegistry {

    static final String ItemRegistryKey = "minecraft:item";

    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.RUNIC_CHALK, registryName = ItemRegistryKey)
    public static RunicChalk RUNIC_CHALK;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.NOVICE_SPELL_BOOK, registryName = ItemRegistryKey)
    public static SpellBook NOVICE_SPELLBOOK;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.APPRENTICE_SPELL_BOOK, registryName = ItemRegistryKey)
    public static SpellBook APPRENTICE_SPELLBOOK;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.ARCHMAGE_SPELL_BOOK, registryName = ItemRegistryKey)
    public static SpellBook ARCHMAGE_SPELLBOOK;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.CREATIVE_SPELL_BOOK, registryName = ItemRegistryKey)
    public static SpellBook CREATIVE_SPELLBOOK;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.BLANK_GLYPH, registryName = ItemRegistryKey)
    public static Item BLANK_GLYPH;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.BUCKET_OF_SOURCE, registryName = ItemRegistryKey)
    public static ModItem BUCKET_OF_SOURCE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.MAGE_BLOOM, registryName = ItemRegistryKey)
    public static ModItem MAGE_BLOOM;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.MAGE_FIBER, registryName = ItemRegistryKey)
    public static ModItem MAGE_FIBER;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.BLAZE_FIBER, registryName = ItemRegistryKey)
    public static ModItem BLAZE_FIBER;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.END_FIBER, registryName = ItemRegistryKey)
    public static ModItem END_FIBER;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.MUNDANE_BELT, registryName = ItemRegistryKey)
    public static ModItem MUNDANE_BELT;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.JAR_OF_LIGHT, registryName = ItemRegistryKey)
    public static JarOfLight JAR_OF_LIGHT;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.BELT_OF_LEVITATION, registryName = ItemRegistryKey)
    public static BeltOfLevitation BELT_OF_LEVITATION;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WORN_NOTEBOOK, registryName = ItemRegistryKey)
    public static WornNotebook WORN_NOTEBOOK = Null();
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.RING_OF_POTENTIAL, registryName = ItemRegistryKey)
    public static ModItem RING_OF_POTENTIAL;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.RING_OF_LESSER_DISCOUNT, registryName = ItemRegistryKey)
    public static DiscountRing RING_OF_LESSER_DISCOUNT;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.RING_OF_GREATER_DISCOUNT, registryName = ItemRegistryKey)
    public static DiscountRing RING_OF_GREATER_DISCOUNT;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.BELT_OF_UNSTABLE_GIFTS, registryName = ItemRegistryKey)
    public static BeltOfUnstableGifts BELT_OF_UNSTABLE_GIFTS;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WARP_SCROLL, registryName = ItemRegistryKey)
    public static WarpScroll WARP_SCROLL;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.SPELL_PARCHMENT, registryName = ItemRegistryKey)
    public static SpellParchment SPELL_PARCHMENT;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.BOOKWYRM_CHARM, registryName = ItemRegistryKey)
    public static BookwyrmCharm BOOKWYRM_CHARM;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.DOMINION_WAND, registryName = ItemRegistryKey)
    public static DominionWand DOMINION_ROD;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.AMULET_OF_MANA_BOOST, registryName = ItemRegistryKey)
    public static AbstractManaCurio AMULET_OF_MANA_BOOST;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.AMULET_OF_MANA_REGEN, registryName = ItemRegistryKey)
    public static AbstractManaCurio AMULET_OF_MANA_REGEN;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.DULL_TRINKET, registryName = ItemRegistryKey)
    public static ModItem DULL_TRINKET;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.STARBUNCLE_CHARM, registryName = ItemRegistryKey)
    public static StarbuncleCharm STARBUNCLE_CHARM;
    @ObjectHolder(value = ArsNouveau.MODID + ":debug", registryName = ItemRegistryKey)
    public static Debug debug;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.STARBUNCLE_SHARDS, registryName = ItemRegistryKey)
    public static ModItem STARBUNCLE_SHARD;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.STARBUNCLE_SHADES, registryName = ItemRegistryKey)
    public static StarbuncleShades STARBUNCLE_SHADES;

    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WHIRLISPRIG_CHARM, registryName = ItemRegistryKey)
    public static WhirlisprigCharm WHIRLISPRIG_CHARM;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WHIRLISPRIG_SHARDS, registryName = ItemRegistryKey)
    public static ModItem WHIRLISPRIG_SHARDS;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.SOURCE_GEM, registryName = ItemRegistryKey)
    public static ModItem SOURCE_GEM;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.ALLOW_ITEM_SCROLL, registryName = ItemRegistryKey)
    public static AllowItemScroll ALLOW_ITEM_SCROLL;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.DENY_ITEM_SCROLL, registryName = ItemRegistryKey)
    public static DenyItemScroll DENY_ITEM_SCROLL;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.MIMIC_ITEM_SCROLL, registryName = ItemRegistryKey)
    public static MimicItemScroll MIMIC_ITEM_SCROLL;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.BLANK_PARCHMENT, registryName = ItemRegistryKey)
    public static BlankParchmentItem BLANK_PARCHMENT;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WAND, registryName = ItemRegistryKey)
    public static Wand WAND;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.VOID_JAR, registryName = ItemRegistryKey)
    public static VoidJar VOID_JAR;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WIXIE_CHARM, registryName = ItemRegistryKey)
    public static WixieCharm WIXIE_CHARM;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WIXIE_SHARD, registryName = ItemRegistryKey)
    public static ModItem WIXIE_SHARD;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.SPELL_BOW, registryName = ItemRegistryKey)
    public static SpellBow SPELL_BOW;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.AMPLIFY_ARROW, registryName = ItemRegistryKey)
    public static SpellArrow AMPLIFY_ARROW;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.SPLIT_ARROW, registryName = ItemRegistryKey)
    public static SpellArrow SPLIT_ARROW;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.PIERCE_ARROW, registryName = ItemRegistryKey)
    public static SpellArrow PIERCE_ARROW;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WILDEN_HORN, registryName = ItemRegistryKey)
    public static ModItem WILDEN_HORN;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WILDEN_SPIKE, registryName = ItemRegistryKey)
    public static ModItem WILDEN_SPIKE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WILDEN_WING, registryName = ItemRegistryKey)
    public static ModItem WILDEN_WING;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.POTION_FLASK, registryName = ItemRegistryKey)
    public static PotionFlask POTION_FLASK;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.POTION_FLASK_AMPLIFY, registryName = ItemRegistryKey)
    public static PotionFlask POTION_FLASK_AMPLIFY;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.POTION_FLASK_EXTEND_TIME, registryName = ItemRegistryKey)
    public static PotionFlask POTION_FLASK_EXTEND_TIME;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.EXP_GEM, registryName = ItemRegistryKey)
    public static ExperienceGem EXPERIENCE_GEM;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.GREATER_EXP_GEM, registryName = ItemRegistryKey)
    public static ExperienceGem GREATER_EXPERIENCE_GEM;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.ENCHANTERS_SWORD, registryName = ItemRegistryKey)
    public static EnchantersSword ENCHANTERS_SWORD;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.ENCHANTERS_SHIELD, registryName = ItemRegistryKey)
    public static EnchantersShield ENCHANTERS_SHIELD;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.CASTER_TOME, registryName = ItemRegistryKey)
    public static CasterTome CASTER_TOME;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.DRYGMY_CHARM, registryName = ItemRegistryKey)
    public static DrygmyCharm DRYGMY_CHARM;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.DRYGMY_SHARD, registryName = ItemRegistryKey)
    public static ModItem DRYGMY_SHARD;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WILDEN_TRIBUTE, registryName = ItemRegistryKey)
    public static ModItem WILDEN_TRIBUTE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.SUMMON_FOCUS, registryName = ItemRegistryKey)
    public static SummoningFocus SUMMONING_FOCUS;

    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.SHAPERS_FOCUS, registryName = ItemRegistryKey)
    public static ShapersFocus SHAPERS_FOCUS;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.SOURCE_BERRY_PIE, registryName = ItemRegistryKey)
    public static ModItem SOURCE_BERRY_PIE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.SOURCE_BERRY_ROLL, registryName = ItemRegistryKey)
    public static ModItem SOURCE_BERRY_ROLL;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.ENCHANTERS_MIRROR, registryName = ItemRegistryKey)
    public static EnchantersMirror ENCHANTERS_MIRROR;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.NOVICE_BOOTS, registryName = ItemRegistryKey)
    public static NoviceArmor NOVICE_BOOTS;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.NOVICE_LEGGINGS, registryName = ItemRegistryKey)
    public static NoviceArmor NOVICE_LEGGINGS;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.NOVICE_ROBES, registryName = ItemRegistryKey)
    public static NoviceArmor NOVICE_ROBES;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.NOVICE_HOOD, registryName = ItemRegistryKey)
    public static NoviceArmor NOVICE_HOOD;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.APPRENTICE_BOOTS, registryName = ItemRegistryKey)
    public static ApprenticeArmor APPRENTICE_BOOTS;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.APPRENTICE_LEGGINGS, registryName = ItemRegistryKey)
    public static ApprenticeArmor APPRENTICE_LEGGINGS;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.APPRENTICE_ROBES, registryName = ItemRegistryKey)
    public static ApprenticeArmor APPRENTICE_ROBES;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.APPRENTICE_HOOD, registryName = ItemRegistryKey)
    public static ApprenticeArmor APPRENTICE_HOOD;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.ARCHMAGE_BOOTS, registryName = ItemRegistryKey)
    public static MasterArmor ARCHMAGE_BOOTS;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.ARCHMAGE_LEGGINGS, registryName = ItemRegistryKey)
    public static MasterArmor ARCHMAGE_LEGGINGS;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.ARCHMAGE_ROBES, registryName = ItemRegistryKey)
    public static MasterArmor ARCHMAGE_ROBES;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.ARCHMAGE_HOOD, registryName = ItemRegistryKey)
    public static MasterArmor ARCHMAGE_HOOD;

    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.DOWSING_ROD, registryName = ItemRegistryKey)
    public static DowsingRod DOWSING_ROD;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.ABJURATION_ESSENCE, registryName = ItemRegistryKey)
    public static ModItem ABJURATION_ESSENCE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.CONJURATION_ESSENCE, registryName = ItemRegistryKey)
    public static ModItem CONJURATION_ESSENCE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.AIR_ESSENCE, registryName = ItemRegistryKey)
    public static ModItem AIR_ESSENCE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.EARTH_ESSENCE, registryName = ItemRegistryKey)
    public static EarthEssence EARTH_ESSENCE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.FIRE_ESSENCE, registryName = ItemRegistryKey)
    public static FireEssence FIRE_ESSENCE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.MANIPULATION_ESSENCE, registryName = ItemRegistryKey)
    public static ModItem MANIPULATION_ESSENCE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.WATER_ESSENCE, registryName = ItemRegistryKey)
    public static ModItem WATER_ESSENCE;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.AMETHYST_GOLEM_CHARM, registryName = ItemRegistryKey)
    public static AmethystGolemCharm AMETHYST_GOLEM_CHARM;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.ANNOTATED_CODEX, registryName = ItemRegistryKey)
    public static AnnotatedCodex ANNOTATED_CODEX;
    @ObjectHolder(value = ArsNouveau.MODID + ":" + LibItemNames.SCRYER_SCROLL, registryName = ItemRegistryKey)
    public static ScryerScroll SCRYER_SCROLL;

    public static FoodProperties SOURCE_BERRY_FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.1F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 100), 1.0f).alwaysEat().build();
    public static FoodProperties SOURCE_PIE_FOOD = new FoodProperties.Builder().nutrition(9).saturationMod(0.9F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 60 * 20, 1), 1.0f).alwaysEat().build();
    public static FoodProperties SOURCE_ROLL_FOOD = new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 60 * 20), 1.0f).alwaysEat().build();

    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        public static final Set<Item> ITEMS = new HashSet<>();

        @SubscribeEvent
        public static void registerItems(final RegisterEvent event) {
            final IForgeRegistry<Item> registry = event.getForgeRegistry();

            ModItem[] items = {
                    new Debug(),
                    new BookwyrmCharm(),
                    new DominionWand(),
                    new RunicChalk(),
                    new ModItem(LibItemNames.BLANK_GLYPH),
                    new ModItem(LibItemNames.DULL_TRINKET).withTooltip(Component.translatable("ars_nouveau.tooltip.dull")),
                    new ModItem(LibItemNames.BLAZE_FIBER),
                    new ModItem(LibItemNames.END_FIBER),
                    new ModItem(LibItemNames.MAGE_BLOOM).withTooltip(Component.translatable("ars_nouveau.tooltip.magebloom")),
                    new ModItem(LibItemNames.MAGE_FIBER),
                    new ModItem(LibItemNames.MUNDANE_BELT).withTooltip(Component.translatable("ars_nouveau.tooltip.dull")),
                    new ModItem(LibItemNames.RING_OF_POTENTIAL).withTooltip(Component.translatable("ars_nouveau.tooltip.dull")),
                    new BeltOfUnstableGifts(LibItemNames.BELT_OF_UNSTABLE_GIFTS),
                    new ModItem(defaultItemProperties().stacksTo(1), LibItemNames.BUCKET_OF_SOURCE),
                    new SpellBook(SpellTier.ONE).setRegistryName(LibItemNames.NOVICE_SPELL_BOOK),
                    new SpellBook(SpellTier.TWO).setRegistryName(LibItemNames.APPRENTICE_SPELL_BOOK),
                    new SpellBook(SpellTier.THREE).setRegistryName(LibItemNames.ARCHMAGE_SPELL_BOOK),
                    new SpellBook(SpellTier.THREE).setRegistryName(LibItemNames.CREATIVE_SPELL_BOOK),
                    new BeltOfLevitation(),
                    new WarpScroll(),
                    new JarOfLight(),
                    new WornNotebook().withTooltip(Component.translatable("tooltip.worn_notebook")),
                    new StarbuncleCharm(),
                    new ModItem(LibItemNames.STARBUNCLE_SHARDS).withTooltip(Component.translatable("tooltip.starbuncle_shard")),
                    new StarbuncleShades(LibItemNames.STARBUNCLE_SHADES).withTooltip(Component.translatable("tooltip.starbuncle_shades")),
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
                    new AbstractManaCurio(LibItemNames.AMULET_OF_MANA_BOOST) {
                        @Override
                        public int getMaxManaBoost(ItemStack i) {
                            return 50;
                        }
                    },
                    new AbstractManaCurio(LibItemNames.AMULET_OF_MANA_REGEN) {

                        @Override
                        public int getManaRegenBonus(ItemStack i) {
                            return 3;
                        }

                    },
                    new ModItem(LibItemNames.WHIRLISPRIG_SHARDS).withTooltip(Component.translatable("tooltip.whirlisprig_shard")),
                    new WhirlisprigCharm(),
                    new ModItem(LibItemNames.SOURCE_GEM).withTooltip(Component.translatable("tooltip.source_gem")),
                    new AllowItemScroll(LibItemNames.ALLOW_ITEM_SCROLL),
                    new DenyItemScroll(LibItemNames.DENY_ITEM_SCROLL),
                    new MimicItemScroll(LibItemNames.MIMIC_ITEM_SCROLL),
                    new BlankParchmentItem(LibItemNames.BLANK_PARCHMENT),
                    new ModItem(LibItemNames.WIXIE_SHARD).withTooltip(Component.translatable("tooltip.wixie_shard")),
                    new Wand(),
                    new VoidJar(),
                    new ModItem(LibItemNames.WILDEN_HORN).withTooltip(Component.translatable("tooltip.wilden_horn")),
                    new ModItem(LibItemNames.WILDEN_WING).withTooltip(Component.translatable("tooltip.wilden_wing")),
                    new ModItem(LibItemNames.WILDEN_SPIKE).withTooltip(Component.translatable("tooltip.wilden_spike")),
                    new PotionFlask() {
                        @Nonnull
                        @Override
                        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                            return effectInstance;
                        }
                    }.withTooltip(Component.translatable("tooltip.potion_flask")),
                    new PotionFlask(LibItemNames.POTION_FLASK_EXTEND_TIME) {
                        @Override
                        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                            return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() + effectInstance.getDuration() / 2, effectInstance.getAmplifier());
                        }
                    }.withTooltip(Component.translatable("tooltip.potion_flask_extend_time")),
                    new PotionFlask(LibItemNames.POTION_FLASK_AMPLIFY) {
                        @Override
                        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                            return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() / 2, effectInstance.getAmplifier() + 1);
                        }
                    }.withTooltip(Component.translatable("tooltip.potion_flask_amplify")),
                    new ExperienceGem(defaultItemProperties(), LibItemNames.EXP_GEM) {
                        @Override
                        public int getValue() {
                            return 3;
                        }
                    }.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem")),
                    new ExperienceGem(defaultItemProperties(), LibItemNames.GREATER_EXP_GEM) {
                        @Override
                        public int getValue() {
                            return 12;
                        }
                    }.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem")),
                    new CasterTome(defaultItemProperties().stacksTo(1), LibItemNames.CASTER_TOME),
                    new DrygmyCharm(LibItemNames.DRYGMY_CHARM),
                    new ModItem(LibItemNames.DRYGMY_SHARD).withTooltip(Component.translatable("tooltip.ars_nouveau.drygmy_shard")),
                    new ModItem(defaultItemProperties().fireResistant(), LibItemNames.WILDEN_TRIBUTE).withRarity(Rarity.EPIC)
                            .withTooltip(Component.translatable("tooltip.ars_nouveau.wilden_tribute")
                            .withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE))),
                    new SummoningFocus(defaultItemProperties().stacksTo(1), LibItemNames.SUMMON_FOCUS),
                    new ModItem(defaultItemProperties().food(SOURCE_PIE_FOOD), LibItemNames.SOURCE_BERRY_PIE).withTooltip(Component.translatable("tooltip.ars_nouveau.source_food")),
                    new ModItem(defaultItemProperties().food(SOURCE_ROLL_FOOD), LibItemNames.SOURCE_BERRY_ROLL).withTooltip(Component.translatable("tooltip.ars_nouveau.source_food")),
                    new EnchantersMirror(defaultItemProperties().stacksTo(1), LibItemNames.ENCHANTERS_MIRROR),
                    new ModItem(LibItemNames.ABJURATION_ESSENCE).withTooltip(Component.translatable("tooltip.essences")),
                    new ModItem(LibItemNames.CONJURATION_ESSENCE).withTooltip(Component.translatable("tooltip.essences")),
                    new ModItem(LibItemNames.AIR_ESSENCE).withTooltip(Component.translatable("tooltip.essences")),
                    new EarthEssence(LibItemNames.EARTH_ESSENCE).withTooltip(Component.translatable("tooltip.essences")),
                    new FireEssence(LibItemNames.FIRE_ESSENCE).withTooltip(Component.translatable("tooltip.essences")),
                    new ModItem(LibItemNames.MANIPULATION_ESSENCE).withTooltip(Component.translatable("tooltip.essences")),
                    new ModItem(LibItemNames.WATER_ESSENCE).withTooltip(Component.translatable("tooltip.essences")),
                    new DowsingRod(LibItemNames.DOWSING_ROD).withTooltip(Component.translatable("tooltip.ars_nouveau.dowsing_rod")),
                    new AmethystGolemCharm().withTooltip(Component.translatable("tooltip.ars_nouveau.amethyst_charm")),
                    new AnnotatedCodex(LibItemNames.ANNOTATED_CODEX),
                    new ScryerScroll(LibItemNames.SCRYER_SCROLL).withTooltip(Component.translatable("tooltip.ars_nouveau.scryer_scroll")),
                    new ShapersFocus(LibItemNames.SHAPERS_FOCUS).withTooltip(Component.translatable("tooltip.ars_nouveau.shapers_focus"))
            };

            for (Map.Entry<String, Supplier<Glyph>> glyph : ArsNouveauAPI.getInstance().getGlyphItemMap().entrySet()) {
                registry.register(glyph.getKey(), glyph.getValue().get());
                ITEMS.add(glyph.getValue().get());
            }

            for (AbstractRitual ritual : ArsNouveauAPI.getInstance().getRitualMap().values()) {
                RitualTablet tablet = new RitualTablet(ArsNouveauAPI.getInstance().getRitualRegistryName(ritual.getID()), ritual);
                registry.register(tablet.registryName, tablet);
                ArsNouveauAPI.getInstance().getRitualItemMap().put(ritual.getID(), tablet);
                ITEMS.add(tablet);
            }

            for (AbstractFamiliarHolder holder : ArsNouveauAPI.getInstance().getFamiliarHolderMap().values()) {
                FamiliarScript script = new FamiliarScript(holder);
                ArsNouveauAPI.getInstance().getFamiliarScriptMap().put(holder.id, script);
                registry.register(script.registryName, script);
                ITEMS.add(script);
            }

            for (final ModItem item : items) {
                registry.register(item.registryName, item);
                ITEMS.add(item);
            }

            registry.register(LibItemNames.SPELL_BOW, new SpellBow());
            registry.register(LibItemNames.PIERCE_ARROW, new FormSpellArrow(AugmentPierce.INSTANCE, 2));
            registry.register(LibItemNames.SPLIT_ARROW, new FormSpellArrow(AugmentSplit.INSTANCE, 2));
            registry.register(LibItemNames.AMPLIFY_ARROW, new SpellArrow(AugmentAmplify.INSTANCE, 2));
            registry.register(LibItemNames.ENCHANTERS_SHIELD, new EnchantersShield());
            registry.register(LibItemNames.ENCHANTERS_SWORD, new EnchantersSword(Tiers.NETHERITE, 3, -2.4F));
            registry.register(LibItemNames.NOVICE_BOOTS, new NoviceArmor(EquipmentSlot.FEET));
            registry.register(LibItemNames.NOVICE_LEGGINGS, new NoviceArmor(EquipmentSlot.LEGS));
            registry.register(LibItemNames.NOVICE_ROBES, new NoviceArmor(EquipmentSlot.CHEST));
            registry.register(LibItemNames.NOVICE_HOOD, new NoviceArmor(EquipmentSlot.HEAD));
            registry.register(LibItemNames.APPRENTICE_BOOTS, new ApprenticeArmor(EquipmentSlot.FEET));
            registry.register(LibItemNames.APPRENTICE_LEGGINGS, new ApprenticeArmor(EquipmentSlot.LEGS));
            registry.register(LibItemNames.APPRENTICE_ROBES, new ApprenticeArmor(EquipmentSlot.CHEST));
            registry.register(LibItemNames.APPRENTICE_HOOD, new ApprenticeArmor(EquipmentSlot.HEAD));
            registry.register(LibItemNames.ARCHMAGE_BOOTS, new MasterArmor(EquipmentSlot.FEET));
            registry.register(LibItemNames.ARCHMAGE_LEGGINGS, new MasterArmor(EquipmentSlot.LEGS));
            registry.register(LibItemNames.ARCHMAGE_ROBES, new MasterArmor(EquipmentSlot.CHEST));
            registry.register(LibItemNames.ARCHMAGE_HOOD, new MasterArmor(EquipmentSlot.HEAD));
            registry.register(LibItemNames.STARBUNCLE_SE, new ForgeSpawnEggItem(ModEntities.STARBUNCLE_TYPE, 0xFFB233, 0xFFE633, defaultItemProperties()));
            registry.register(LibItemNames.SYLPH_SE, new ForgeSpawnEggItem(ModEntities.WHIRLISPRIG_TYPE, 0x77FF33, 0xFFFB00, defaultItemProperties()));
            registry.register(LibItemNames.WILDEN_HUNTER_SE, new ForgeSpawnEggItem(ModEntities.WILDEN_HUNTER, 0xFDFDFD, 0xCAA97F, defaultItemProperties()));
            registry.register(LibItemNames.WILDEN_GUARDIAN_SE, new ForgeSpawnEggItem(ModEntities.WILDEN_GUARDIAN, 0xFFFFFF, 0xFF9E00, defaultItemProperties()));
            registry.register(LibItemNames.WILDEN_STALKER_SE, new ForgeSpawnEggItem(ModEntities.WILDEN_STALKER, 0x9B650C, 0xEF1818, defaultItemProperties()));

        }
    }

    public static Item.Properties defaultItemProperties() {
        return new Item.Properties().tab(ArsNouveau.itemGroup);
    }
}

