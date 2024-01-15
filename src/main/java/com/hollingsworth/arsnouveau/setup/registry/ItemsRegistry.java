package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.registry.FamiliarRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import com.hollingsworth.arsnouveau.common.items.*;
import com.hollingsworth.arsnouveau.common.items.curios.*;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.AllowItemScroll;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.DenyItemScroll;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.MimicItemScroll;
import com.hollingsworth.arsnouveau.common.items.summon_charms.*;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.perk.EmptyPerk;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.util.RegistryWrapper;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;


public class ItemsRegistry {
    public static PerkItem BLANK_THREAD;

    public static FoodProperties SOURCE_BERRY_FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.1F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 100), 1.0f).alwaysEat().build();
    public static FoodProperties SOURCE_PIE_FOOD = new FoodProperties.Builder().nutrition(9).saturationMod(0.9F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 60 * 20, 1), 1.0f).alwaysEat().build();
    public static FoodProperties SOURCE_ROLL_FOOD = new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).effect(() -> new MobEffectInstance(ModPotions.MANA_REGEN_EFFECT.get(), 60 * 20), 1.0f).alwaysEat().build();
    public static FoodProperties MENDOSTEEN_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).effect(() ->
            new MobEffectInstance(ModPotions.RECOVERY_EFFECT.get(), 60 * 20), 1.0f).alwaysEat().build();
    public static FoodProperties BLASTING_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(0.6F)
            .effect(() -> new MobEffectInstance(ModPotions.BLAST_EFFECT.get(), 10 * 20), 1.0f).alwaysEat().build();
    public static FoodProperties BASTION_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(0.6F)
            .effect(() -> new MobEffectInstance(ModPotions.DEFENCE_EFFECT.get(), 60 * 20), 1.0f).alwaysEat().build();
    public static FoodProperties FROSTAYA_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(0.6F)
            .effect(() -> new MobEffectInstance(ModPotions.FREEZING_EFFECT.get(), 30 * 20), 1.0f).alwaysEat().build();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryWrapper<RunicChalk> RUNIC_CHALK = register(LibItemNames.RUNIC_CHALK, RunicChalk::new);

    public static final RegistryWrapper<SpellBook> NOVICE_SPELLBOOK = register(LibItemNames.NOVICE_SPELL_BOOK, () -> new SpellBook(SpellTier.ONE));
    public static final RegistryWrapper<SpellBook> APPRENTICE_SPELLBOOK = register(LibItemNames.APPRENTICE_SPELL_BOOK, () -> new SpellBook(SpellTier.TWO));
    public static final RegistryWrapper<SpellBook> ARCHMAGE_SPELLBOOK = register(LibItemNames.ARCHMAGE_SPELL_BOOK, () -> new SpellBook(SpellTier.THREE));
    public static final RegistryWrapper<SpellBook> CREATIVE_SPELLBOOK = register(LibItemNames.CREATIVE_SPELL_BOOK, () -> new SpellBook(SpellTier.CREATIVE));
    public static final RegistryWrapper<ModItem> BLANK_GLYPH = register(LibItemNames.BLANK_GLYPH);
    public static final RegistryWrapper<ModItem> MAGE_BLOOM = register(LibItemNames.MAGE_BLOOM, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.magebloom")));
    public static final RegistryWrapper<ModItem> MAGE_FIBER = register(LibItemNames.MAGE_FIBER);
    public static final RegistryWrapper<ModItem> MUNDANE_BELT = register(LibItemNames.MUNDANE_BELT, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.dull")));
    public static final RegistryWrapper<JarOfLight> JAR_OF_LIGHT = register(LibItemNames.JAR_OF_LIGHT, JarOfLight::new);
    public static final RegistryWrapper<BeltOfLevitation> BELT_OF_LEVITATION = register(LibItemNames.BELT_OF_LEVITATION, BeltOfLevitation::new);
    public static final RegistryWrapper<WornNotebook> WORN_NOTEBOOK = register(LibItemNames.WORN_NOTEBOOK, WornNotebook::new);
    public static final RegistryWrapper<ModItem> RING_OF_POTENTIAL = register(LibItemNames.RING_OF_POTENTIAL, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.dull")));
    public static final RegistryWrapper<DiscountRing> RING_OF_LESSER_DISCOUNT = register(LibItemNames.RING_OF_LESSER_DISCOUNT, () -> new DiscountRing() {
        @Override
        public int getManaDiscount() {
            return 10;
        }
    });
    public static final RegistryWrapper<DiscountRing> RING_OF_GREATER_DISCOUNT = register(LibItemNames.RING_OF_GREATER_DISCOUNT, () -> new DiscountRing() {
        @Override
        public int getManaDiscount() {
            return 20;
        }
    });
    public static final RegistryWrapper<BeltOfUnstableGifts> BELT_OF_UNSTABLE_GIFTS = register(LibItemNames.BELT_OF_UNSTABLE_GIFTS, BeltOfUnstableGifts::new);
    public static final RegistryWrapper<WarpScroll> WARP_SCROLL = register(LibItemNames.WARP_SCROLL, WarpScroll::new);
    public static final RegistryWrapper<SpellParchment> SPELL_PARCHMENT = register(LibItemNames.SPELL_PARCHMENT, SpellParchment::new);
    public static final RegistryWrapper<BookwyrmCharm> BOOKWYRM_CHARM = register(LibItemNames.BOOKWYRM_CHARM, BookwyrmCharm::new);
    public static final RegistryWrapper<DominionWand> DOMINION_ROD = register(LibItemNames.DOMINION_WAND, DominionWand::new);
    public static final RegistryWrapper<AbstractManaCurio> AMULET_OF_MANA_BOOST = register(LibItemNames.AMULET_OF_MANA_BOOST, () -> new AbstractManaCurio() {
        @Override
        public int getMaxManaBoost(ItemStack i) {
            return 50;
        }
    });
    public static final RegistryWrapper<AbstractManaCurio> AMULET_OF_MANA_REGEN = register(LibItemNames.AMULET_OF_MANA_REGEN, () -> new AbstractManaCurio() {
        @Override
        public int getManaRegenBonus(ItemStack i) {
            return 3;
        }
    });
    public static final RegistryWrapper<ModItem> DULL_TRINKET = register(LibItemNames.DULL_TRINKET, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.dull")));
    public static final RegistryWrapper<StarbuncleCharm> STARBUNCLE_CHARM = register(LibItemNames.STARBUNCLE_CHARM, StarbuncleCharm::new);
    public static final RegistryWrapper<Debug> debug = register("debug", Debug::new);
    public static final RegistryWrapper<StarbuncleShard> STARBUNCLE_SHARD = register(LibItemNames.STARBUNCLE_SHARDS, StarbuncleShard::new);
    public static final RegistryWrapper<StarbuncleShades> STARBUNCLE_SHADES = register(LibItemNames.STARBUNCLE_SHADES, StarbuncleShades::new);
    public static final RegistryWrapper<WhirlisprigCharm> WHIRLISPRIG_CHARM = register(LibItemNames.WHIRLISPRIG_CHARM, WhirlisprigCharm::new);
    public static final RegistryWrapper<ModItem> WHIRLISPRIG_SHARDS = register(LibItemNames.WHIRLISPRIG_SHARDS, () -> new ModItem().withTooltip(Component.translatable("tooltip.whirlisprig_shard")));
    public static final RegistryWrapper<ModItem> SOURCE_GEM = register(LibItemNames.SOURCE_GEM, () -> new ModItem().withTooltip(Component.translatable("tooltip.source_gem")));
    public static final RegistryWrapper<AllowItemScroll> ALLOW_ITEM_SCROLL = register(LibItemNames.ALLOW_ITEM_SCROLL, AllowItemScroll::new);
    public static final RegistryWrapper<DenyItemScroll> DENY_ITEM_SCROLL = register(LibItemNames.DENY_ITEM_SCROLL, DenyItemScroll::new);
    public static final RegistryWrapper<MimicItemScroll> MIMIC_ITEM_SCROLL = register(LibItemNames.MIMIC_ITEM_SCROLL, MimicItemScroll::new);
    public static final RegistryWrapper<BlankParchmentItem> BLANK_PARCHMENT = register(LibItemNames.BLANK_PARCHMENT, BlankParchmentItem::new);
    public static final RegistryWrapper<Wand> WAND = register(LibItemNames.WAND, Wand::new);
    public static final RegistryWrapper<VoidJar> VOID_JAR = register(LibItemNames.VOID_JAR, VoidJar::new);
    public static final RegistryWrapper<WixieCharm> WIXIE_CHARM = register(LibItemNames.WIXIE_CHARM, WixieCharm::new);
    public static final RegistryWrapper<ModItem> WIXIE_SHARD = register(LibItemNames.WIXIE_SHARD, () -> new ModItem().withTooltip(Component.translatable("tooltip.wixie_shard")));
    public static final RegistryWrapper<SpellBow> SPELL_BOW = register(LibItemNames.SPELL_BOW, SpellBow::new);
    public static final RegistryWrapper<SpellArrow> AMPLIFY_ARROW = register(LibItemNames.AMPLIFY_ARROW, () -> new SpellArrow(AugmentAmplify.INSTANCE, 2));
    public static final RegistryWrapper<FormSpellArrow> SPLIT_ARROW = register(LibItemNames.SPLIT_ARROW, () -> new FormSpellArrow(AugmentSplit.INSTANCE, 2));
    public static final RegistryWrapper<FormSpellArrow> PIERCE_ARROW = register(LibItemNames.PIERCE_ARROW, () -> new FormSpellArrow(AugmentPierce.INSTANCE, 2));
    public static final RegistryWrapper<ModItem> WILDEN_HORN = register(LibItemNames.WILDEN_HORN, () -> new ModItem().withTooltip(Component.translatable("tooltip.wilden_horn")));
    public static final RegistryWrapper<ModItem> WILDEN_SPIKE = register(LibItemNames.WILDEN_SPIKE, () -> new ModItem().withTooltip(Component.translatable("tooltip.wilden_spike")));
    public static final RegistryWrapper<ModItem> WILDEN_WING = register(LibItemNames.WILDEN_WING, () -> new ModItem().withTooltip(Component.translatable("tooltip.wilden_wing")));
    public static final RegistryWrapper<PotionFlask> POTION_FLASK = register(LibItemNames.POTION_FLASK, () -> {
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
    public static RegistryWrapper<PotionFlask> POTION_FLASK_AMPLIFY = register(LibItemNames.POTION_FLASK_AMPLIFY, () -> {
        PotionFlask flask = new PotionFlask() {
            @Override
            public @NotNull MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() / 2, Math.min(Config.ENCHANTED_FLASK_CAP.get(), effectInstance.getAmplifier() + 1));
            }
        };
        flask.withTooltip(Component.translatable("tooltip.potion_flask_amplify"));
        return flask;
    });
    public static RegistryWrapper<PotionFlask> POTION_FLASK_EXTEND_TIME = register(LibItemNames.POTION_FLASK_EXTEND_TIME, () -> {
        PotionFlask flask = new PotionFlask() {
            @Override
            public @NotNull MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() + effectInstance.getDuration() / 2, effectInstance.getAmplifier());
            }
        };
        flask.withTooltip(Component.translatable("tooltip.potion_flask_extend_time"));
        return flask;
    });
    public static RegistryWrapper<ExperienceGem> EXPERIENCE_GEM = register(LibItemNames.EXP_GEM, () ->{
        ExperienceGem gem = new ExperienceGem() {
            @Override
            public int getValue() {
                return 3;
            }
        };
        gem.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem"));
        return gem;
    });
    public static RegistryWrapper<ExperienceGem> GREATER_EXPERIENCE_GEM = register(LibItemNames.GREATER_EXP_GEM, () ->{
        ExperienceGem gem = new ExperienceGem() {
            @Override
            public int getValue() {
                return 12;
            }
        };
        gem.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem"));
        return gem;
    });
    public static final RegistryWrapper<EnchantersSword> ENCHANTERS_SWORD = register(LibItemNames.ENCHANTERS_SWORD, () -> new EnchantersSword(Tiers.NETHERITE, 3, -2.4F));
    public static final RegistryWrapper<EnchantersShield> ENCHANTERS_SHIELD = register(LibItemNames.ENCHANTERS_SHIELD, EnchantersShield::new);
    public static final RegistryWrapper<CasterTome> CASTER_TOME = register(LibItemNames.CASTER_TOME, CasterTome::new);
    public static final RegistryWrapper<DrygmyCharm> DRYGMY_CHARM = register(LibItemNames.DRYGMY_CHARM, DrygmyCharm::new);
    public static final RegistryWrapper<ModItem> DRYGMY_SHARD = register(LibItemNames.DRYGMY_SHARD, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.drygmy_shard")));
    public static final RegistryWrapper<ModItem> WILDEN_TRIBUTE = register(LibItemNames.WILDEN_TRIBUTE, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.wilden_tribute").withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE))).withRarity(Rarity.EPIC));
    public static final RegistryWrapper<SummoningFocus> SUMMONING_FOCUS = register(LibItemNames.SUMMON_FOCUS, SummoningFocus::new);
    public static final RegistryWrapper<ShapersFocus> SHAPERS_FOCUS = register(LibItemNames.SHAPERS_FOCUS, () -> new ShapersFocus(defaultItemProperties().stacksTo(1)));
    public static final RegistryWrapper<ModItem> SOURCE_BERRY_PIE = register(LibItemNames.SOURCE_BERRY_PIE, () -> new ModItem(defaultItemProperties().food(SOURCE_PIE_FOOD)).withTooltip(Component.translatable("tooltip.ars_nouveau.source_food")));
    public static final RegistryWrapper<ModItem> SOURCE_BERRY_ROLL = register(LibItemNames.SOURCE_BERRY_ROLL, () -> new ModItem(defaultItemProperties().food(SOURCE_ROLL_FOOD)).withTooltip(Component.translatable("tooltip.ars_nouveau.source_food")));
    public static final RegistryWrapper<EnchantersMirror> ENCHANTERS_MIRROR = register(LibItemNames.ENCHANTERS_MIRROR, () -> new EnchantersMirror(defaultItemProperties().stacksTo(1)));
    public static final RegistryWrapper<AnimatedMagicArmor> SORCERER_BOOTS = register(LibItemNames.SORCERER_BOOTS, () -> AnimatedMagicArmor.light(ArmorItem.Type.BOOTS));
    public static final RegistryWrapper<AnimatedMagicArmor> SORCERER_LEGGINGS = register(LibItemNames.SORCERER_LEGGINGS, () -> AnimatedMagicArmor.light(ArmorItem.Type.LEGGINGS));
    public static final RegistryWrapper<AnimatedMagicArmor> SORCERER_ROBES = register(LibItemNames.SORCERER_ROBES, () -> AnimatedMagicArmor.light(ArmorItem.Type.CHESTPLATE));
    public static final RegistryWrapper<AnimatedMagicArmor> SORCERER_HOOD = register(LibItemNames.SORCERER_HOOD, () -> AnimatedMagicArmor.light(ArmorItem.Type.HELMET));
    public static final RegistryWrapper<AnimatedMagicArmor> ARCANIST_BOOTS = register(LibItemNames.ARCANIST_BOOTS, () -> AnimatedMagicArmor.medium(ArmorItem.Type.BOOTS));
    public static final RegistryWrapper<AnimatedMagicArmor> ARCANIST_LEGGINGS = register(LibItemNames.ARCANIST_LEGGINGS, () -> AnimatedMagicArmor.medium(ArmorItem.Type.LEGGINGS));
    public static final RegistryWrapper<AnimatedMagicArmor> ARCANIST_ROBES = register(LibItemNames.ARCANIST_ROBES, () -> AnimatedMagicArmor.medium(ArmorItem.Type.CHESTPLATE));
    public static final RegistryWrapper<AnimatedMagicArmor> ARCANIST_HOOD = register(LibItemNames.ARCANIST_HOOD, () -> AnimatedMagicArmor.medium(ArmorItem.Type.HELMET));
    public static final RegistryWrapper<AnimatedMagicArmor> BATTLEMAGE_BOOTS = register(LibItemNames.BATTLEMAGE_BOOTS, () -> AnimatedMagicArmor.heavy(ArmorItem.Type.BOOTS));
    public static final RegistryWrapper<AnimatedMagicArmor> BATTLEMAGE_LEGGINGS = register(LibItemNames.BATTLEMAGE_LEGGINGS, () -> AnimatedMagicArmor.heavy(ArmorItem.Type.LEGGINGS));
    public static final RegistryWrapper<AnimatedMagicArmor> BATTLEMAGE_ROBES = register(LibItemNames.BATTLEMAGE_ROBES, () -> AnimatedMagicArmor.heavy(ArmorItem.Type.CHESTPLATE));
    public static final RegistryWrapper<AnimatedMagicArmor> BATTLEMAGE_HOOD = register(LibItemNames.BATTLEMAGE_HOOD, () -> AnimatedMagicArmor.heavy(ArmorItem.Type.HELMET));
    public static final RegistryWrapper<DowsingRod> DOWSING_ROD = register(LibItemNames.DOWSING_ROD, DowsingRod::new);
    public static final RegistryWrapper<ModItem> ABJURATION_ESSENCE = register(LibItemNames.ABJURATION_ESSENCE, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.essences")));
    public static final RegistryWrapper<ModItem> CONJURATION_ESSENCE = register(LibItemNames.CONJURATION_ESSENCE, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.essences")));
    public static final RegistryWrapper<ModItem> AIR_ESSENCE = register(LibItemNames.AIR_ESSENCE, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.essences")));
    public static final RegistryWrapper<EarthEssence> EARTH_ESSENCE = register(LibItemNames.EARTH_ESSENCE, EarthEssence::new);
    public static final RegistryWrapper<FireEssence> FIRE_ESSENCE = register(LibItemNames.FIRE_ESSENCE, FireEssence::new);
    public static final RegistryWrapper<ManipulationEssence> MANIPULATION_ESSENCE = register(LibItemNames.MANIPULATION_ESSENCE, ManipulationEssence::new);
    public static final RegistryWrapper<ModItem> WATER_ESSENCE = register(LibItemNames.WATER_ESSENCE, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.essences")));
    public static final RegistryWrapper<AmethystGolemCharm> AMETHYST_GOLEM_CHARM = register(LibItemNames.AMETHYST_GOLEM_CHARM, AmethystGolemCharm::new);
    public static final RegistryWrapper<AnnotatedCodex> ANNOTATED_CODEX = register(LibItemNames.ANNOTATED_CODEX, AnnotatedCodex::new);
    public static final RegistryWrapper<ScryerScroll> SCRYER_SCROLL = register(LibItemNames.SCRYER_SCROLL, ScryerScroll::new);
    public static final RegistryWrapper<ModItem> WIXIE_HAT = register(LibItemNames.WIXIE_HAT, () -> new WixieHat().withTooltip("tooltip.ars_nouveau.wixie_hat"));
    public static final RegistryWrapper<ModItem> ALCHEMISTS_CROWN = register(LibItemNames.ALCHEMISTS_CROWN, () -> new AlchemistsCrown(defaultItemProperties().stacksTo(1)));
    public static final RegistryWrapper<ModItem> SPLASH_LAUNCHER = register(LibItemNames.SPLASH_LAUNCHER, () -> new FlaskCannon.SplashLauncher(defaultItemProperties().stacksTo(1)));
    public static final RegistryWrapper<ModItem> LINGERING_LAUNCHER = register(LibItemNames.LINGERING_LAUNCHER, () -> new FlaskCannon.LingeringLauncher(defaultItemProperties().stacksTo(1)));
    public static final RegistryWrapper<RecordItem> FIREL_DISC = register(LibItemNames.FIREL_DISC, () -> new RecordItem(9, () -> SoundRegistry.ARIA_BIBLIO.get(), defaultItemProperties().stacksTo(1).rarity(Rarity.RARE), 20 * 240));
    public static final RegistryWrapper<RecordItem> SOUND_OF_GLASS = register(LibItemNames.SOUND_OF_GLASS, () -> new RecordItem(9, () -> SoundRegistry.SOUND_OF_GLASS.get(), defaultItemProperties().stacksTo(1).rarity(Rarity.RARE), 20 * 182));
    public static final RegistryWrapper<RecordItem> WILD_HUNT = register(LibItemNames.FIREL_WILD_HUNT, () -> new RecordItem(9, () -> SoundRegistry.WILD_HUNT.get(), defaultItemProperties().stacksTo(1).rarity(Rarity.RARE), 20 * 121));
    public static final RegistryWrapper<Present> STARBY_GIFY = register(LibItemNames.STARBY_GIFT, () -> new Present(defaultItemProperties().rarity(Rarity.EPIC)));
    public static final RegistryWrapper<SpellCrossbow> SPELL_CROSSBOW = register(LibItemNames.SPELL_CROSSBOW, () -> new SpellCrossbow(defaultItemProperties().stacksTo(1)));
    public static final RegistryWrapper<StableWarpScroll> STABLE_WARP_SCROLL = register(LibItemNames.STABLE_WARP_SCROLL, () -> new StableWarpScroll(defaultItemProperties().stacksTo(1)));
    public static final RegistryWrapper<ScryCaster> SCRY_CASTER = register(LibItemNames.SCRY_CASTER, () -> new ScryCaster(defaultItemProperties().stacksTo(1)));
    public static final RegistryWrapper<JumpingRing> JUMP_RING = register(LibItemNames.JUMP_RING, JumpingRing::new);

    public static <T extends Item> RegistryWrapper<T> register(String name, Supplier<T> item) {
        return new RegistryWrapper<>(ITEMS.register(name, item));
    }

    public static RegistryWrapper<ModItem> register(String name) {
        return register(name, ModItem::new);
    }

    public static void onItemRegistry(IForgeRegistry<Item> registry) {
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        for (Map.Entry<ResourceLocation, Supplier<Glyph>> glyphEntry : GlyphRegistry.getGlyphItemMap().entrySet()) {
            Glyph glyph = glyphEntry.getValue().get();
            registry.register(glyphEntry.getKey(), glyph);
            glyph.spellPart.glyphItem = glyph;
        }

        for (AbstractRitual ritual : RitualRegistry.getRitualMap().values()) {
            RitualTablet tablet = new RitualTablet(ritual);
            registry.register(ritual.getRegistryName(), tablet);
            RitualRegistry.getRitualItemMap().put(ritual.getRegistryName(), tablet);
        }

        for (AbstractFamiliarHolder holder : FamiliarRegistry.getFamiliarHolderMap().values()) {
            FamiliarScript script = new FamiliarScript(holder);
            FamiliarRegistry.getFamiliarScriptMap().put(holder.getRegistryName(), script);
            registry.register(holder.getRegistryName(), script);
        }

        for(IPerk perk : PerkRegistry.getPerkMap().values()) {
            PerkItem perkItem = new PerkItem(perk);
            PerkRegistry.getPerkItemMap().put(perk.getRegistryName(), perkItem);
            registry.register(perk.getRegistryName(), perkItem);
            if(perk instanceof EmptyPerk){
                BLANK_THREAD = perkItem;
            }
        }

        ITEMS.register(LibItemNames.DRYGMY_SE, () -> new ForgeSpawnEggItem(ModEntities.ENTITY_DRYGMY, 10051392, 0xFFE633, defaultItemProperties()));
        ITEMS.register(LibItemNames.STARBUNCLE_SE, () -> new ForgeSpawnEggItem(ModEntities.STARBUNCLE_TYPE, 0xFFB233, 0xFFE633, defaultItemProperties()));
        ITEMS.register(LibItemNames.SYLPH_SE, () -> new ForgeSpawnEggItem(ModEntities.WHIRLISPRIG_TYPE, 0x77FF33, 0xFFFB00, defaultItemProperties()));
        ITEMS.register(LibItemNames.WILDEN_HUNTER_SE, () -> new ForgeSpawnEggItem(ModEntities.WILDEN_HUNTER, 0xFDFDFD, 0xCAA97F, defaultItemProperties()));
        ITEMS.register(LibItemNames.WILDEN_GUARDIAN_SE, () -> new ForgeSpawnEggItem(ModEntities.WILDEN_GUARDIAN, 0xFFFFFF, 0xFF9E00, defaultItemProperties()));
        ITEMS.register(LibItemNames.WILDEN_STALKER_SE, () -> new ForgeSpawnEggItem(ModEntities.WILDEN_STALKER, 0x9B650C, 0xEF1818, defaultItemProperties()));
    }


    public static Item.Properties defaultItemProperties() {
        return new Item.Properties();
    }
}

