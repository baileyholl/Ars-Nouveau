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
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;
import static com.hollingsworth.arsnouveau.setup.InjectionUtil.Null;

public class ItemsRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    static final String ItemRegistryKey = "minecraft:item";


    public static final RegistryObject<RunicChalk> RUNIC_CHALK = ITEMS.register(LibItemNames.RUNIC_CHALK, () -> new RunicChalk());

    public static RegistryObject<SpellBook> NOVICE_SPELLBOOK = ITEMS.register(LibItemNames.NOVICE_SPELL_BOOK, () -> new SpellBook(SpellTier.ONE));
    public static RegistryObject<SpellBook> APPRENTICE_SPELLBOOK = ITEMS.register(LibItemNames.APPRENTICE_SPELL_BOOK, () -> new SpellBook(SpellTier.TWO));
    public static RegistryObject<SpellBook> ARCHMAGE_SPELLBOOK = ITEMS.register(LibItemNames.ARCHMAGE_SPELL_BOOK, () -> new SpellBook(SpellTier.THREE));
    public static RegistryObject<SpellBook> CREATIVE_SPELLBOOK = ITEMS.register(LibItemNames.CREATIVE_SPELL_BOOK, () -> new SpellBook(SpellTier.THREE));
    public static RegistryObject<Item> BLANK_GLYPH = register(LibItemNames.BLANK_GLYPH);
    public static RegistryObject<ModItem> BUCKET_OF_SOURCE = register(LibItemNames.BUCKET_OF_SOURCE);
    public static RegistryObject<ModItem> MAGE_BLOOM = register(LibItemNames.MAGE_BLOOM);
    public static RegistryObject<ModItem> MAGE_FIBER = register(LibItemNames.MAGE_FIBER);
    public static RegistryObject<ModItem> BLAZE_FIBER = register(LibItemNames.BLAZE_FIBER);
    public static RegistryObject<ModItem> END_FIBER = register(LibItemNames.END_FIBER);
    public static RegistryObject<ModItem> MUNDANE_BELT = register(LibItemNames.MUNDANE_BELT);
    public static RegistryObject<JarOfLight> JAR_OF_LIGHT = register(LibItemNames.JAR_OF_LIGHT, () -> new JarOfLight());
    public static RegistryObject<BeltOfLevitation> BELT_OF_LEVITATION = register(LibItemNames.BELT_OF_LEVITATION, () -> new BeltOfLevitation());
    @ObjectHolder(value = MODID + ":" + LibItemNames.WORN_NOTEBOOK, registryName = ItemRegistryKey)
    public static RegistryObject<WornNotebook> WORN_NOTEBOOK = Null();
    public static RegistryObject<ModItem> RING_OF_POTENTIAL = register(LibItemNames.RING_OF_POTENTIAL);
    public static RegistryObject<DiscountRing> RING_OF_LESSER_DISCOUNT = register(LibItemNames.RING_OF_LESSER_DISCOUNT, () -> new DiscountRing() {
        @Override
        public int getManaDiscount() {
            return 10;
        }
    });

    public static RegistryObject<DiscountRing> RING_OF_GREATER_DISCOUNT = register(LibItemNames.RING_OF_GREATER_DISCOUNT, () -> new DiscountRing() {
        @Override
        public int getManaDiscount() {
            return 20;
        }
    });

    public static RegistryObject<BeltOfUnstableGifts> BELT_OF_UNSTABLE_GIFTS = register(LibItemNames.BELT_OF_UNSTABLE_GIFTS, () -> new BeltOfUnstableGifts());

    public static RegistryObject<WarpScroll> WARP_SCROLL = register(LibItemNames.WARP_SCROLL, () -> new WarpScroll());

    public static RegistryObject<SpellParchment> SPELL_PARCHMENT = register(LibItemNames.SPELL_PARCHMENT, () -> new SpellParchment());

    public static RegistryObject<BookwyrmCharm> BOOKWYRM_CHARM = register(LibItemNames.BOOKWYRM_CHARM, () -> new BookwyrmCharm());

    public static RegistryObject<DominionWand> DOMINION_ROD = register(LibItemNames.DOMINION_WAND, () -> new DominionWand());
    @ObjectHolder(value = MODID + ":" + LibItemNames.AMULET_OF_MANA_BOOST, registryName = ItemRegistryKey)
    public static RegistryObject<AbstractManaCurio> AMULET_OF_MANA_BOOST = register(LibItemNames.AMULET_OF_MANA_BOOST, () -> new AbstractManaCurio() {
        @Override
        public int getMaxManaBoost(ItemStack i) {
            return 50;
        }
    });

    public static RegistryObject<AbstractManaCurio> AMULET_OF_MANA_REGEN;

    public static RegistryObject<ModItem> DULL_TRINKET  = register(LibItemNames.DULL_TRINKET);

    public static RegistryObject<StarbuncleCharm> STARBUNCLE_CHARM = register(LibItemNames.STARBUNCLE_CHARM, () -> new StarbuncleCharm());

    public static RegistryObject<Debug> debug = register("debug", () -> new Debug());

    public static RegistryObject<ModItem> STARBUNCLE_SHARD = register(LibItemNames.STARBUNCLE_SHARDS);

    public static RegistryObject<StarbuncleShades> STARBUNCLE_SHADES = register(LibItemNames.STARBUNCLE_SHADES, () -> new StarbuncleShades());


    public static RegistryObject<WhirlisprigCharm> WHIRLISPRIG_CHARM = register(LibItemNames.WHIRLISPRIG_CHARM, () -> new WhirlisprigCharm());

    public static RegistryObject<ModItem> WHIRLISPRIG_SHARDS = register(LibItemNames.WHIRLISPRIG_SHARDS);

    public static RegistryObject<ModItem> SOURCE_GEM = register(LibItemNames.SOURCE_GEM);

    public static RegistryObject<AllowItemScroll> ALLOW_ITEM_SCROLL = register(LibItemNames.ALLOW_ITEM_SCROLL, () -> new AllowItemScroll());

    public static RegistryObject<DenyItemScroll> DENY_ITEM_SCROLL = register(LibItemNames.DENY_ITEM_SCROLL, () -> new DenyItemScroll());

    public static RegistryObject<MimicItemScroll> MIMIC_ITEM_SCROLL = register(LibItemNames.MIMIC_ITEM_SCROLL, () -> new MimicItemScroll());

    public static RegistryObject<BlankParchmentItem> BLANK_PARCHMENT = register(LibItemNames.BLANK_PARCHMENT, () -> new BlankParchmentItem());

    public static RegistryObject<Wand> WAND = register(LibItemNames.WAND, () -> new Wand());

    public static RegistryObject<VoidJar> VOID_JAR = register(LibItemNames.VOID_JAR, () -> new VoidJar());

    public static RegistryObject<WixieCharm> WIXIE_CHARM = register(LibItemNames.WIXIE_CHARM, () -> new WixieCharm());

    public static RegistryObject<ModItem> WIXIE_SHARD = register(LibItemNames.WIXIE_SHARD);

    public static RegistryObject<SpellBow> SPELL_BOW = register(LibItemNames.SPELL_BOW, () -> new SpellBow());

    public static RegistryObject<SpellArrow> AMPLIFY_ARROW = register(LibItemNames.AMPLIFY_ARROW, () -> new SpellArrow(AugmentAmplify.INSTANCE, 2));

    public static RegistryObject<FormSpellArrow> SPLIT_ARROW = register(LibItemNames.SPLIT_ARROW, () -> new FormSpellArrow(AugmentSplit.INSTANCE, 2));

    public static RegistryObject<FormSpellArrow> PIERCE_ARROW = register(LibItemNames.PIERCE_ARROW, () -> new FormSpellArrow(AugmentPierce.INSTANCE, 2));

    public static RegistryObject<ModItem> WILDEN_HORN = register(LibItemNames.WILDEN_HORN);

    public static RegistryObject<ModItem> WILDEN_SPIKE = register(LibItemNames.WILDEN_SPIKE);

    public static RegistryObject<ModItem> WILDEN_WING = register(LibItemNames.WILDEN_WING);

    public static RegistryObject<PotionFlask> POTION_FLASK = register(LibItemNames.POTION_FLASK, () -> new PotionFlask(){
        @Nonnull
        @Override
        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
            return effectInstance;
        }
    }.withTooltip(Component.translatable("tooltip.potion_flask")));

    public static RegistryObject<PotionFlask> POTION_FLASK_AMPLIFY = register(LibItemNames.POTION_FLASK_AMPLIFY, () -> new PotionFlask(){
        @Override
        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
            return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() / 2, effectInstance.getAmplifier() + 1);
        }
    }.withTooltip(Component.translatable("tooltip.potion_flask_amplify")));


    public static RegistryObject<PotionFlask> POTION_FLASK_EXTEND_TIME = register(LibItemNames.POTION_FLASK_EXTEND_TIME, () -> new PotionFlask(){
        @Override
        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
            return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() + effectInstance.getDuration() / 2, effectInstance.getAmplifier());
        }
    }.withTooltip(Component.translatable("tooltip.potion_flask_extend_time")));

    @ObjectHolder(value = MODID + ":" + LibItemNames.EXP_GEM, registryName = ItemRegistryKey)
    public static RegistryObject<ExperienceGem> EXPERIENCE_GEM;
    @ObjectHolder(value = MODID + ":" + LibItemNames.GREATER_EXP_GEM, registryName = ItemRegistryKey)
    public static RegistryObject<ExperienceGem> GREATER_EXPERIENCE_GEM;
    @ObjectHolder(value = MODID + ":" + LibItemNames.ENCHANTERS_SWORD, registryName = ItemRegistryKey)
    public static EnchantersSword ENCHANTERS_SWORD;
    @ObjectHolder(value = MODID + ":" + LibItemNames.ENCHANTERS_SHIELD, registryName = ItemRegistryKey)
    public static EnchantersShield ENCHANTERS_SHIELD;
    @ObjectHolder(value = MODID + ":" + LibItemNames.CASTER_TOME, registryName = ItemRegistryKey)
    public static CasterTome CASTER_TOME;
    @ObjectHolder(value = MODID + ":" + LibItemNames.DRYGMY_CHARM, registryName = ItemRegistryKey)
    public static DrygmyCharm DRYGMY_CHARM;
    @ObjectHolder(value = MODID + ":" + LibItemNames.DRYGMY_SHARD, registryName = ItemRegistryKey)
    public static ModItem DRYGMY_SHARD;
    @ObjectHolder(value = MODID + ":" + LibItemNames.WILDEN_TRIBUTE, registryName = ItemRegistryKey)
    public static ModItem WILDEN_TRIBUTE;
    @ObjectHolder(value = MODID + ":" + LibItemNames.SUMMON_FOCUS, registryName = ItemRegistryKey)
    public static SummoningFocus SUMMONING_FOCUS;

    @ObjectHolder(value = MODID + ":" + LibItemNames.SHAPERS_FOCUS, registryName = ItemRegistryKey)
    public static ShapersFocus SHAPERS_FOCUS;
    @ObjectHolder(value = MODID + ":" + LibItemNames.SOURCE_BERRY_PIE, registryName = ItemRegistryKey)
    public static ModItem SOURCE_BERRY_PIE;
    @ObjectHolder(value = MODID + ":" + LibItemNames.SOURCE_BERRY_ROLL, registryName = ItemRegistryKey)
    public static ModItem SOURCE_BERRY_ROLL;
    @ObjectHolder(value = MODID + ":" + LibItemNames.ENCHANTERS_MIRROR, registryName = ItemRegistryKey)
    public static EnchantersMirror ENCHANTERS_MIRROR;
    @ObjectHolder(value = MODID + ":" + LibItemNames.NOVICE_BOOTS, registryName = ItemRegistryKey)
    public static NoviceArmor NOVICE_BOOTS;
    @ObjectHolder(value = MODID + ":" + LibItemNames.NOVICE_LEGGINGS, registryName = ItemRegistryKey)
    public static NoviceArmor NOVICE_LEGGINGS;
    @ObjectHolder(value = MODID + ":" + LibItemNames.NOVICE_ROBES, registryName = ItemRegistryKey)
    public static NoviceArmor NOVICE_ROBES;
    @ObjectHolder(value = MODID + ":" + LibItemNames.NOVICE_HOOD, registryName = ItemRegistryKey)
    public static NoviceArmor NOVICE_HOOD;
    @ObjectHolder(value = MODID + ":" + LibItemNames.APPRENTICE_BOOTS, registryName = ItemRegistryKey)
    public static ApprenticeArmor APPRENTICE_BOOTS;
    @ObjectHolder(value = MODID + ":" + LibItemNames.APPRENTICE_LEGGINGS, registryName = ItemRegistryKey)
    public static ApprenticeArmor APPRENTICE_LEGGINGS;
    @ObjectHolder(value = MODID + ":" + LibItemNames.APPRENTICE_ROBES, registryName = ItemRegistryKey)
    public static ApprenticeArmor APPRENTICE_ROBES;
    @ObjectHolder(value = MODID + ":" + LibItemNames.APPRENTICE_HOOD, registryName = ItemRegistryKey)
    public static ApprenticeArmor APPRENTICE_HOOD;
    @ObjectHolder(value = MODID + ":" + LibItemNames.ARCHMAGE_BOOTS, registryName = ItemRegistryKey)
    public static MasterArmor ARCHMAGE_BOOTS;
    @ObjectHolder(value = MODID + ":" + LibItemNames.ARCHMAGE_LEGGINGS, registryName = ItemRegistryKey)
    public static MasterArmor ARCHMAGE_LEGGINGS;
    @ObjectHolder(value = MODID + ":" + LibItemNames.ARCHMAGE_ROBES, registryName = ItemRegistryKey)
    public static MasterArmor ARCHMAGE_ROBES;
    @ObjectHolder(value = MODID + ":" + LibItemNames.ARCHMAGE_HOOD, registryName = ItemRegistryKey)
    public static MasterArmor ARCHMAGE_HOOD;

    @ObjectHolder(value = MODID + ":" + LibItemNames.DOWSING_ROD, registryName = ItemRegistryKey)
    public static DowsingRod DOWSING_ROD;
    @ObjectHolder(value = MODID + ":" + LibItemNames.ABJURATION_ESSENCE, registryName = ItemRegistryKey)
    public static ModItem ABJURATION_ESSENCE;
    @ObjectHolder(value = MODID + ":" + LibItemNames.CONJURATION_ESSENCE, registryName = ItemRegistryKey)
    public static ModItem CONJURATION_ESSENCE;
    @ObjectHolder(value = MODID + ":" + LibItemNames.AIR_ESSENCE, registryName = ItemRegistryKey)
    public static ModItem AIR_ESSENCE;
    @ObjectHolder(value = MODID + ":" + LibItemNames.EARTH_ESSENCE, registryName = ItemRegistryKey)
    public static EarthEssence EARTH_ESSENCE;
    @ObjectHolder(value = MODID + ":" + LibItemNames.FIRE_ESSENCE, registryName = ItemRegistryKey)
    public static FireEssence FIRE_ESSENCE;
    @ObjectHolder(value = MODID + ":" + LibItemNames.MANIPULATION_ESSENCE, registryName = ItemRegistryKey)
    public static ModItem MANIPULATION_ESSENCE;
    @ObjectHolder(value = MODID + ":" + LibItemNames.WATER_ESSENCE, registryName = ItemRegistryKey)
    public static ModItem WATER_ESSENCE;
    @ObjectHolder(value = MODID + ":" + LibItemNames.AMETHYST_GOLEM_CHARM, registryName = ItemRegistryKey)
    public static AmethystGolemCharm AMETHYST_GOLEM_CHARM;
    @ObjectHolder(value = MODID + ":" + LibItemNames.ANNOTATED_CODEX, registryName = ItemRegistryKey)
    public static AnnotatedCodex ANNOTATED_CODEX;
    @ObjectHolder(value = MODID + ":" + LibItemNames.SCRYER_SCROLL, registryName = ItemRegistryKey)
    public static ScryerScroll SCRYER_SCROLL;

    public static FoodProperties SOURCE_BERRY_FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.1F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 100), 1.0f).alwaysEat().build();
    public static FoodProperties SOURCE_PIE_FOOD = new FoodProperties.Builder().nutrition(9).saturationMod(0.9F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 60 * 20, 1), 1.0f).alwaysEat().build();
    public static FoodProperties SOURCE_ROLL_FOOD = new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 60 * 20), 1.0f).alwaysEat().build();

    public static RegistryObject register(String name, Supplier<? extends Item> item) {
        return ITEMS.register(name, item);
    }

    public static RegistryObject register(String name) {
        return register(name, () -> new ModItem());
    }
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
            // TODO: restore spawn eggs
//            registry.register(LibItemNames.STARBUNCLE_SE, new ForgeSpawnEggItem(ModEntities.STARBUNCLE_TYPE, 0xFFB233, 0xFFE633, defaultItemProperties()));
//            registry.register(LibItemNames.SYLPH_SE, new ForgeSpawnEggItem(ModEntities.WHIRLISPRIG_TYPE, 0x77FF33, 0xFFFB00, defaultItemProperties()));
//            registry.register(LibItemNames.WILDEN_HUNTER_SE, new ForgeSpawnEggItem(ModEntities.WILDEN_HUNTER, 0xFDFDFD, 0xCAA97F, defaultItemProperties()));
//            registry.register(LibItemNames.WILDEN_GUARDIAN_SE, new ForgeSpawnEggItem(ModEntities.WILDEN_GUARDIAN, 0xFFFFFF, 0xFF9E00, defaultItemProperties()));
//            registry.register(LibItemNames.WILDEN_STALKER_SE, new ForgeSpawnEggItem(ModEntities.WILDEN_STALKER, 0x9B650C, 0xEF1818, defaultItemProperties()));

        }
    }


    public static Item.Properties defaultItemProperties() {
        return new Item.Properties().tab(ArsNouveau.itemGroup);
    }
}

