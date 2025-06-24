package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.registry.FamiliarRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import com.hollingsworth.arsnouveau.common.items.*;
import com.hollingsworth.arsnouveau.common.items.curios.*;
import com.hollingsworth.arsnouveau.common.items.data.PresentData;
import com.hollingsworth.arsnouveau.common.items.data.ScryCasterData;
import com.hollingsworth.arsnouveau.common.items.data.ScryPosData;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.AllowItemScroll;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.DenyItemScroll;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.MimicItemScroll;
import com.hollingsworth.arsnouveau.common.items.summon_charms.*;
import com.hollingsworth.arsnouveau.common.lib.LibBlockNames;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.perk.EmptyPerk;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;
import static com.hollingsworth.arsnouveau.ArsNouveau.prefix;

public class ItemsRegistry {

    public static Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true);
    public static PerkItem BLANK_THREAD;

    public static FoodProperties SOURCE_BERRY_FOOD = new FoodProperties.Builder().nutrition(2).saturationModifier(0.1F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 100), 1.0f).alwaysEdible().build();
    public static FoodProperties SOURCE_PIE_FOOD = new FoodProperties.Builder().nutrition(9).saturationModifier(0.9F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 60 * 20, 1), 1.0f).alwaysEdible().build();
    public static FoodProperties SOURCE_ROLL_FOOD = new FoodProperties.Builder().nutrition(8).saturationModifier(0.6F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT, 60 * 20), 1.0f).alwaysEdible().build();
    public static FoodProperties MENDOSTEEN_FOOD = new FoodProperties.Builder().nutrition(4).saturationModifier(0.6F).effect(() ->
            new MobEffectInstance(ModPotions.RECOVERY_EFFECT, 60 * 20), 1.0f).alwaysEdible().build();
    public static FoodProperties BLASTING_FOOD = new FoodProperties.Builder().nutrition(4).saturationModifier(0.6F)
            .effect(() -> new MobEffectInstance(ModPotions.BLAST_EFFECT, 10 * 20), 1.0f).alwaysEdible().build();
    public static FoodProperties BASTION_FOOD = new FoodProperties.Builder().nutrition(4).saturationModifier(0.6F)
            .effect(() -> new MobEffectInstance(ModPotions.DEFENCE_EFFECT, 60 * 20), 1.0f).alwaysEdible().build();
    public static FoodProperties FROSTAYA_FOOD = new FoodProperties.Builder().nutrition(4).saturationModifier(0.6F)
            .effect(() -> new MobEffectInstance(ModPotions.FREEZING_EFFECT, 30 * 20), 1.0f).alwaysEdible().build();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    public static final ItemRegistryWrapper<RunicChalk> RUNIC_CHALK = register(LibItemNames.RUNIC_CHALK, RunicChalk::new);

    public static final ItemRegistryWrapper<SpellBook> NOVICE_SPELLBOOK = register(LibItemNames.NOVICE_SPELL_BOOK, () -> new SpellBook(SpellTier.ONE));
    public static final ItemRegistryWrapper<SpellBook> APPRENTICE_SPELLBOOK = register(LibItemNames.APPRENTICE_SPELL_BOOK, () -> new SpellBook(SpellTier.TWO));
    public static final ItemRegistryWrapper<SpellBook> ARCHMAGE_SPELLBOOK = register(LibItemNames.ARCHMAGE_SPELL_BOOK, () -> new SpellBook(SpellTier.THREE));
    public static final ItemRegistryWrapper<SpellBook> CREATIVE_SPELLBOOK = register(LibItemNames.CREATIVE_SPELL_BOOK, () -> new SpellBook(SpellTier.CREATIVE));
    public static final ItemRegistryWrapper<ModItem> BLANK_GLYPH = register(LibItemNames.BLANK_GLYPH);
    public static final ItemRegistryWrapper<ModItem> MAGE_BLOOM = register(LibItemNames.MAGE_BLOOM, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.magebloom")));
    public static final ItemRegistryWrapper<ModItem> MAGE_FIBER = register(LibItemNames.MAGE_FIBER);
    public static final ItemRegistryWrapper<ModItem> MUNDANE_BELT = register(LibItemNames.MUNDANE_BELT, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.dull")));
    public static final ItemRegistryWrapper<JarOfLight> JAR_OF_LIGHT = register(LibItemNames.JAR_OF_LIGHT, JarOfLight::new);
    public static final ItemRegistryWrapper<BeltOfLevitation> BELT_OF_LEVITATION = register(LibItemNames.BELT_OF_LEVITATION, BeltOfLevitation::new);
    public static final ItemRegistryWrapper<WornNotebook> WORN_NOTEBOOK = register(LibItemNames.WORN_NOTEBOOK, WornNotebook::new);
    public static final ItemRegistryWrapper<ModItem> RING_OF_POTENTIAL = register(LibItemNames.RING_OF_POTENTIAL, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.dull")));
    public static final ItemRegistryWrapper<DiscountRing> RING_OF_LESSER_DISCOUNT = register(LibItemNames.RING_OF_LESSER_DISCOUNT, () -> new DiscountRing() {
        @Override
        public int getManaDiscount() {
            return 10;
        }
    });
    public static final ItemRegistryWrapper<DiscountRing> RING_OF_GREATER_DISCOUNT = register(LibItemNames.RING_OF_GREATER_DISCOUNT, () -> new DiscountRing() {
        @Override
        public int getManaDiscount() {
            return 20;
        }
    });
    public static final ItemRegistryWrapper<BeltOfUnstableGifts> BELT_OF_UNSTABLE_GIFTS = register(LibItemNames.BELT_OF_UNSTABLE_GIFTS, BeltOfUnstableGifts::new);
    public static final ItemRegistryWrapper<WarpScroll> WARP_SCROLL = register(LibItemNames.WARP_SCROLL, WarpScroll::new);
    public static final ItemRegistryWrapper<SpellParchment> SPELL_PARCHMENT = register(LibItemNames.SPELL_PARCHMENT, SpellParchment::new);
    public static final ItemRegistryWrapper<BookwyrmCharm> BOOKWYRM_CHARM = register(LibItemNames.BOOKWYRM_CHARM, BookwyrmCharm::new);
    public static final ItemRegistryWrapper<DominionWand> DOMINION_ROD = register(LibItemNames.DOMINION_WAND, DominionWand::new);
    public static final ItemRegistryWrapper<AbstractManaCurio> AMULET_OF_MANA_BOOST = register(LibItemNames.AMULET_OF_MANA_BOOST, () -> new AbstractManaCurio() {
        @Override
        public int getMaxManaBoost(ItemStack i) {
            return 50;
        }
    });
    public static final ItemRegistryWrapper<AbstractManaCurio> AMULET_OF_MANA_REGEN = register(LibItemNames.AMULET_OF_MANA_REGEN, () -> new AbstractManaCurio() {
        @Override
        public int getManaRegenBonus(ItemStack i) {
            return 3;
        }
    });
    public static final ItemRegistryWrapper<ModItem> DULL_TRINKET = register(LibItemNames.DULL_TRINKET, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.dull")));
    public static final ItemRegistryWrapper<StarbuncleCharm> STARBUNCLE_CHARM = register(LibItemNames.STARBUNCLE_CHARM, StarbuncleCharm::new);
    public static final ItemRegistryWrapper<Debug> debug = register("debug", Debug::new);
    public static final ItemRegistryWrapper<StarbuncleShard> STARBUNCLE_SHARD = register(LibItemNames.STARBUNCLE_SHARDS, StarbuncleShard::new);
    public static final ItemRegistryWrapper<StarbuncleShades> STARBUNCLE_SHADES = register(LibItemNames.STARBUNCLE_SHADES, StarbuncleShades::new);
    public static final ItemRegistryWrapper<WhirlisprigCharm> WHIRLISPRIG_CHARM = register(LibItemNames.WHIRLISPRIG_CHARM, WhirlisprigCharm::new);
    public static final ItemRegistryWrapper<ModItem> WHIRLISPRIG_SHARDS = register(LibItemNames.WHIRLISPRIG_SHARDS, () -> new ModItem().withTooltip("tooltip.whirlisprig_shard").withTooltip(Component.translatable("tooltip.whirlisprig_shard2").withStyle(LORE_STYLE)));
    public static final ItemRegistryWrapper<ModItem> SOURCE_GEM = register(LibItemNames.SOURCE_GEM, () -> new ModItem().withTooltip(Component.translatable("tooltip.source_gem")));
    public static final ItemRegistryWrapper<AllowItemScroll> ALLOW_ITEM_SCROLL = register(LibItemNames.ALLOW_ITEM_SCROLL, AllowItemScroll::new);
    public static final ItemRegistryWrapper<DenyItemScroll> DENY_ITEM_SCROLL = register(LibItemNames.DENY_ITEM_SCROLL, DenyItemScroll::new);
    public static final ItemRegistryWrapper<MimicItemScroll> MIMIC_ITEM_SCROLL = register(LibItemNames.MIMIC_ITEM_SCROLL, MimicItemScroll::new);
    public static final ItemRegistryWrapper<BlankParchmentItem> BLANK_PARCHMENT = register(LibItemNames.BLANK_PARCHMENT, BlankParchmentItem::new);
    public static final ItemRegistryWrapper<Wand> WAND = register(LibItemNames.WAND, Wand::new);
    public static final ItemRegistryWrapper<VoidJar> VOID_JAR = register(LibItemNames.VOID_JAR, VoidJar::new);
    public static final ItemRegistryWrapper<WixieCharm> WIXIE_CHARM = register(LibItemNames.WIXIE_CHARM, WixieCharm::new);
    public static final ItemRegistryWrapper<ModItem> WIXIE_SHARD = register(LibItemNames.WIXIE_SHARD, () -> new ModItem().withTooltip(Component.translatable("tooltip.wixie_shard")).withTooltip(Component.translatable("tooltip.wixie_shard2").withStyle(ItemsRegistry.LORE_STYLE)));
    public static final ItemRegistryWrapper<SpellBow> SPELL_BOW = register(LibItemNames.SPELL_BOW, SpellBow::new);
    public static final ItemRegistryWrapper<SpellArrow> AMPLIFY_ARROW = register(LibItemNames.AMPLIFY_ARROW, () -> new SpellArrow(AugmentAmplify.INSTANCE, 2));
    public static final ItemRegistryWrapper<FormSpellArrow> SPLIT_ARROW = register(LibItemNames.SPLIT_ARROW, () -> new FormSpellArrow(AugmentSplit.INSTANCE, 2));
    public static final ItemRegistryWrapper<FormSpellArrow> PIERCE_ARROW = register(LibItemNames.PIERCE_ARROW, () -> new FormSpellArrow(AugmentPierce.INSTANCE, 2));
    public static final ItemRegistryWrapper<ModItem> WILDEN_HORN = register(LibItemNames.WILDEN_HORN, () -> new ModItem().withTooltip(Component.translatable("tooltip.wilden_horn")));
    public static final ItemRegistryWrapper<ModItem> WILDEN_SPIKE = register(LibItemNames.WILDEN_SPIKE, () -> new ModItem().withTooltip(Component.translatable("tooltip.wilden_spike")));
    public static final ItemRegistryWrapper<ModItem> WILDEN_WING = register(LibItemNames.WILDEN_WING, () -> new ModItem().withTooltip(Component.translatable("tooltip.wilden_wing")));
    public static final ItemRegistryWrapper<PotionFlask> POTION_FLASK = register(LibItemNames.POTION_FLASK, () -> {
        PotionFlask flask = new PotionFlask() {
            @NotNull
            @Override
            public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                return effectInstance;
            }
        };
        flask.withTooltip(Component.translatable("tooltip.potion_flask"));
        return flask;
    });
    public static ItemRegistryWrapper<PotionFlask> POTION_FLASK_AMPLIFY = register(LibItemNames.POTION_FLASK_AMPLIFY, () -> {
        PotionFlask flask = new PotionFlask() {
            @Override
            public @NotNull MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() / 2, Math.min(Config.ENCHANTED_FLASK_CAP.get(), effectInstance.getAmplifier() + 1));
            }
        };
        flask.withTooltip(Component.translatable("tooltip.potion_flask_amplify"));
        return flask;
    });
    public static ItemRegistryWrapper<PotionFlask> POTION_FLASK_EXTEND_TIME = register(LibItemNames.POTION_FLASK_EXTEND_TIME, () -> {
        PotionFlask flask = new PotionFlask() {
            @Override
            public @NotNull MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() + effectInstance.getDuration() / 2, effectInstance.getAmplifier());
            }
        };
        flask.withTooltip(Component.translatable("tooltip.potion_flask_extend_time"));
        return flask;
    });
    public static ItemRegistryWrapper<ExperienceGem> EXPERIENCE_GEM = register(LibItemNames.EXP_GEM, () -> {
        ExperienceGem gem = new ExperienceGem() {
            @Override
            public int getValue() {
                return 3;
            }
        };
        gem.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem"));
        return gem;
    });
    public static ItemRegistryWrapper<ExperienceGem> GREATER_EXPERIENCE_GEM = register(LibItemNames.GREATER_EXP_GEM, () -> {
        ExperienceGem gem = new ExperienceGem() {
            @Override
            public int getValue() {
                return 12;
            }
        };
        gem.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem"));
        return gem;
    });
    public static final ItemRegistryWrapper<EnchantersSword> ENCHANTERS_SWORD = register(LibItemNames.ENCHANTERS_SWORD, () -> new EnchantersSword(Tiers.NETHERITE, 3, -2.4F));
    public static final ItemRegistryWrapper<EnchantersShield> ENCHANTERS_SHIELD = register(LibItemNames.ENCHANTERS_SHIELD, EnchantersShield::new);
    public static final ItemRegistryWrapper<CasterTome> CASTER_TOME = register(LibItemNames.CASTER_TOME, CasterTome::new);
    public static final ItemRegistryWrapper<DrygmyCharm> DRYGMY_CHARM = register(LibItemNames.DRYGMY_CHARM, DrygmyCharm::new);
    public static final ItemRegistryWrapper<ModItem> DRYGMY_SHARD = register(LibItemNames.DRYGMY_SHARD, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.drygmy_shard")).withTooltip(Component.translatable("tooltip.drygmy_shard2").withStyle(ItemsRegistry.LORE_STYLE)));
    public static final ItemRegistryWrapper<ModItem> WILDEN_TRIBUTE = register(LibItemNames.WILDEN_TRIBUTE, () -> new ModItem(defaultItemProperties().fireResistant()).withTooltip(Component.translatable("tooltip.ars_nouveau.wilden_tribute").withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE))).withRarity(Rarity.EPIC));
    public static final ItemRegistryWrapper<SummoningFocus> SUMMONING_FOCUS = register(LibItemNames.SUMMON_FOCUS, SummoningFocus::new);
    public static final ItemRegistryWrapper<ShapersFocus> SHAPERS_FOCUS = register(LibItemNames.SHAPERS_FOCUS, () -> new ShapersFocus(defaultItemProperties().stacksTo(1)));
    public static final ItemRegistryWrapper<ModItem> SOURCE_BERRY_PIE = register(LibItemNames.SOURCE_BERRY_PIE, () -> new ModItem(defaultItemProperties().food(SOURCE_PIE_FOOD)).withTooltip(Component.translatable("tooltip.ars_nouveau.source_food")));
    public static final ItemRegistryWrapper<ModItem> SOURCE_BERRY_ROLL = register(LibItemNames.SOURCE_BERRY_ROLL, () -> new ModItem(defaultItemProperties().food(SOURCE_ROLL_FOOD)).withTooltip(Component.translatable("tooltip.ars_nouveau.source_food")));
    public static final ItemRegistryWrapper<EnchantersMirror> ENCHANTERS_MIRROR = register(LibItemNames.ENCHANTERS_MIRROR, () -> new EnchantersMirror(defaultItemProperties().stacksTo(1).component(DataComponentRegistry.SPELL_CASTER, new SpellCaster())));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> SORCERER_BOOTS = register(LibItemNames.SORCERER_BOOTS, () -> AnimatedMagicArmor.light(ArmorItem.Type.BOOTS));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> SORCERER_LEGGINGS = register(LibItemNames.SORCERER_LEGGINGS, () -> AnimatedMagicArmor.light(ArmorItem.Type.LEGGINGS));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> SORCERER_ROBES = register(LibItemNames.SORCERER_ROBES, () -> AnimatedMagicArmor.light(ArmorItem.Type.CHESTPLATE));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> SORCERER_HOOD = register(LibItemNames.SORCERER_HOOD, () -> AnimatedMagicArmor.light(ArmorItem.Type.HELMET));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> ARCANIST_BOOTS = register(LibItemNames.ARCANIST_BOOTS, () -> AnimatedMagicArmor.medium(ArmorItem.Type.BOOTS));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> ARCANIST_LEGGINGS = register(LibItemNames.ARCANIST_LEGGINGS, () -> AnimatedMagicArmor.medium(ArmorItem.Type.LEGGINGS));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> ARCANIST_ROBES = register(LibItemNames.ARCANIST_ROBES, () -> AnimatedMagicArmor.medium(ArmorItem.Type.CHESTPLATE));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> ARCANIST_HOOD = register(LibItemNames.ARCANIST_HOOD, () -> AnimatedMagicArmor.medium(ArmorItem.Type.HELMET));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> BATTLEMAGE_BOOTS = register(LibItemNames.BATTLEMAGE_BOOTS, () -> AnimatedMagicArmor.heavy(ArmorItem.Type.BOOTS));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> BATTLEMAGE_LEGGINGS = register(LibItemNames.BATTLEMAGE_LEGGINGS, () -> AnimatedMagicArmor.heavy(ArmorItem.Type.LEGGINGS));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> BATTLEMAGE_ROBES = register(LibItemNames.BATTLEMAGE_ROBES, () -> AnimatedMagicArmor.heavy(ArmorItem.Type.CHESTPLATE));
    public static final ItemRegistryWrapper<AnimatedMagicArmor> BATTLEMAGE_HOOD = register(LibItemNames.BATTLEMAGE_HOOD, () -> AnimatedMagicArmor.heavy(ArmorItem.Type.HELMET));
    public static final ItemRegistryWrapper<DowsingRod> DOWSING_ROD = register(LibItemNames.DOWSING_ROD, DowsingRod::new);
    public static final ItemRegistryWrapper<ModItem> ABJURATION_ESSENCE = register(LibItemNames.ABJURATION_ESSENCE, () -> new AbstractEssence("abjuration"));
    public static final ItemRegistryWrapper<ModItem> CONJURATION_ESSENCE = register(LibItemNames.CONJURATION_ESSENCE, () -> new AbstractEssence("conjuration"));
    public static final ItemRegistryWrapper<ModItem> AIR_ESSENCE = register(LibItemNames.AIR_ESSENCE, () -> new AbstractEssence("air"));
    public static final ItemRegistryWrapper<EarthEssence> EARTH_ESSENCE = register(LibItemNames.EARTH_ESSENCE, EarthEssence::new);
    public static final ItemRegistryWrapper<FireEssence> FIRE_ESSENCE = register(LibItemNames.FIRE_ESSENCE, FireEssence::new);
    public static final ItemRegistryWrapper<ManipulationEssence> MANIPULATION_ESSENCE = register(LibItemNames.MANIPULATION_ESSENCE, ManipulationEssence::new);
    public static final ItemRegistryWrapper<ModItem> WATER_ESSENCE = register(LibItemNames.WATER_ESSENCE, () -> new AbstractEssence("water"));
    public static final ItemRegistryWrapper<AmethystGolemCharm> AMETHYST_GOLEM_CHARM = register(LibItemNames.AMETHYST_GOLEM_CHARM, AmethystGolemCharm::new);
    public static final ItemRegistryWrapper<AnnotatedCodex> ANNOTATED_CODEX = register(LibItemNames.ANNOTATED_CODEX, AnnotatedCodex::new);
    public static final ItemRegistryWrapper<ScryerScroll> SCRYER_SCROLL = register(LibItemNames.SCRYER_SCROLL, ScryerScroll::new);
    public static final ItemRegistryWrapper<ModItem> WIXIE_HAT = register(LibItemNames.WIXIE_HAT, () -> new WixieHat().withTooltip("tooltip.ars_nouveau.wixie_hat"));
    public static final ItemRegistryWrapper<ModItem> ALCHEMISTS_CROWN = register(LibItemNames.ALCHEMISTS_CROWN, () -> new AlchemistsCrown(defaultItemProperties().stacksTo(1)));
    public static final ItemRegistryWrapper<ModItem> SPLASH_LAUNCHER = register(LibItemNames.SPLASH_LAUNCHER, () -> new FlaskCannon.SplashLauncher(defaultItemProperties().stacksTo(1)));
    public static final ItemRegistryWrapper<ModItem> LINGERING_LAUNCHER = register(LibItemNames.LINGERING_LAUNCHER, () -> new FlaskCannon.LingeringLauncher(defaultItemProperties().stacksTo(1)));

    public static final ItemRegistryWrapper<Item> FIREL_DISC = register(LibItemNames.FIREL_DISC, () -> new Item(defaultItemProperties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxRegistry.ARIA_BIBLIO)));
    public static final ItemRegistryWrapper<Item> SOUND_OF_GLASS = register(LibItemNames.SOUND_OF_GLASS, () -> new Item(defaultItemProperties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxRegistry.SOUND_OF_GLASS)));
    public static final ItemRegistryWrapper<Item> WILD_HUNT = register(LibItemNames.FIREL_WILD_HUNT, () -> new Item(defaultItemProperties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(JukeboxRegistry.WILD_HUNT)));
    public static final ItemRegistryWrapper<Present> STARBY_GIFY = register(LibItemNames.STARBY_GIFT, () -> new Present(defaultItemProperties().rarity(Rarity.EPIC).component(DataComponentRegistry.PRESENT, new PresentData())));
    public static final ItemRegistryWrapper<SpellCrossbow> SPELL_CROSSBOW = register(LibItemNames.SPELL_CROSSBOW, () -> new SpellCrossbow(defaultItemProperties().stacksTo(1).component(DataComponentRegistry.SPELL_CASTER, new SpellCaster())));
    public static final ItemRegistryWrapper<StableWarpScroll> STABLE_WARP_SCROLL = register(LibItemNames.STABLE_WARP_SCROLL, () -> new StableWarpScroll(defaultItemProperties().stacksTo(1)));
    public static final ItemRegistryWrapper<ScryCaster> SCRY_CASTER = register(LibItemNames.SCRY_CASTER, () -> new ScryCaster(defaultItemProperties().stacksTo(1).component(DataComponentRegistry.SCRY_CASTER, new ScryCasterData()).component(DataComponentRegistry.SCRY_DATA, new ScryPosData(Optional.empty()))));
    public static final ItemRegistryWrapper<JumpingRing> JUMP_RING = register(LibItemNames.JUMP_RING, JumpingRing::new);
    public static final ItemRegistryWrapper<AlakarkinosCharm> ALAKARKINOS_CHARM = register(LibItemNames.ALAKARKINOS_CHARM, AlakarkinosCharm::new);
    public static final ItemRegistryWrapper<Item> ALAKARKINOS_SHARD = register(LibItemNames.ALAKARKINOS_SHARD, () -> new ModItem().withTooltip("tooltip.alakarkinos_shard1").withTooltip(Component.translatable("tooltip.alakarkinos_shard2").withStyle(LORE_STYLE)));

    public static final DeferredHolder<Item, BannerPatternItem> ARS_STENCIL = createPatternItem("ars_stencil", Rarity.UNCOMMON);
    public static final ItemRegistryWrapper<Item> ENCHANTERS_GAUNTLET = register(LibItemNames.ENCHANTERS_GAUNTLET, EnchantersGauntlet::new);
    public static final ItemRegistryWrapper<Item> ENCHANTERS_FISHING_ROD = register(LibItemNames.ENCHANTERS_ROD, EnchantersFishingRod::new);

    public static final ItemRegistryWrapper<SignItem> ARCHWOOD_SIGN = register(LibBlockNames.ARCHWOOD_SIGN, () -> new SignItem(defaultItemProperties().stacksTo(16), BlockRegistry.ARCHWOOD_SIGN.get(), BlockRegistry.ARCHWOOD_WALL_SIGN.get()));
    public static final ItemRegistryWrapper<HangingSignItem> ARCHWOOD_HANGING_SIGN = register(LibBlockNames.ARCHWOOD_HANGING_SIGN, () -> new HangingSignItem(BlockRegistry.ARCHWOOD_HANGING_SIGN.get(), BlockRegistry.ARCHWOOD_HANGING_WALL_SIGN.get(), defaultItemProperties().stacksTo(16)));

    public static <T extends Item> ItemRegistryWrapper<T> register(String name, Supplier<T> item) {
        return new ItemRegistryWrapper<>(ITEMS.register(name, item));
    }

    public static ItemRegistryWrapper<ModItem> register(String name) {
        return register(name, ModItem::new);
    }

    public static void onItemRegistry(RegisterEvent.RegisterHelper<Item> helper) {
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        for (Map.Entry<ResourceLocation, Supplier<Glyph>> glyphEntry : GlyphRegistry.getGlyphItemMap().entrySet()) {
            Glyph glyph = glyphEntry.getValue().get();
            helper.register(glyphEntry.getKey(), glyph);
            glyph.spellPart.glyphItem = glyph;
        }

        for (AbstractRitual ritual : RitualRegistry.getRitualMap().values()) {
            RitualTablet tablet = new RitualTablet(ritual);
            helper.register(ritual.getRegistryName(), tablet);
            RitualRegistry.getRitualItemMap().put(ritual.getRegistryName(), tablet);
        }

        for (AbstractFamiliarHolder holder : FamiliarRegistry.getFamiliarHolderMap().values()) {
            FamiliarScript script = new FamiliarScript(holder);
            FamiliarRegistry.getFamiliarScriptMap().put(holder.getRegistryName(), script);
            helper.register(holder.getRegistryName(), script);
        }

        for (IPerk perk : PerkRegistry.getPerkMap().values()) {
            PerkItem perkItem = new PerkItem(perk);
            PerkRegistry.getPerkItemMap().put(perk.getRegistryName(), perkItem);
            helper.register(perk.getRegistryName(), perkItem);
            if (perk instanceof EmptyPerk) {
                BLANK_THREAD = perkItem;
            }
        }

        ITEMS.register(LibItemNames.DRYGMY_SE, () -> new DeferredSpawnEggItem(ModEntities.ENTITY_DRYGMY, 10051392, 0xFFE633, defaultItemProperties()));
        ITEMS.register(LibItemNames.STARBUNCLE_SE, () -> new DeferredSpawnEggItem(ModEntities.STARBUNCLE_TYPE, 0xFFB233, 0xFFE633, defaultItemProperties()));
        ITEMS.register(LibItemNames.SYLPH_SE, () -> new DeferredSpawnEggItem(ModEntities.WHIRLISPRIG_TYPE, 0x77FF33, 0xFFFB00, defaultItemProperties()));
        ITEMS.register(LibItemNames.WILDEN_HUNTER_SE, () -> new DeferredSpawnEggItem(ModEntities.WILDEN_HUNTER, 0xFDFDFD, 0xCAA97F, defaultItemProperties()));
        ITEMS.register(LibItemNames.WILDEN_GUARDIAN_SE, () -> new DeferredSpawnEggItem(ModEntities.WILDEN_GUARDIAN, 0xFFFFFF, 0xFF9E00, defaultItemProperties()));
        ITEMS.register(LibItemNames.WILDEN_STALKER_SE, () -> new DeferredSpawnEggItem(ModEntities.WILDEN_STALKER, 0x9B650C, 0xEF1818, defaultItemProperties()));
        ITEMS.register(LibItemNames.ALAKARKINOS_SE, () -> new DeferredSpawnEggItem(ModEntities.ALAKARKINOS_TYPE, 16724530, 3289855, defaultItemProperties()));
    }

    private static DeferredHolder<Item, BannerPatternItem> createPatternItem(String name, Rarity rarity) {
        final TagKey<BannerPattern> bannerTag = TagKey.create(Registries.BANNER_PATTERN, prefix("pattern_item/" + name));
        return ITEMS.register(name, () -> new BannerPatternItem(bannerTag, new Item.Properties().stacksTo(1).rarity(rarity)));
    }

    public static Item.Properties defaultItemProperties() {
        return new Item.Properties();
    }
}

