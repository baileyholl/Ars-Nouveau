package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.armor.HeavyArmor;
import com.hollingsworth.arsnouveau.common.armor.LightArmor;
import com.hollingsworth.arsnouveau.common.armor.MediumArmor;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.items.*;
import com.hollingsworth.arsnouveau.common.items.curios.*;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.AllowItemScroll;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.DenyItemScroll;
import com.hollingsworth.arsnouveau.common.items.itemscrolls.MimicItemScroll;
import com.hollingsworth.arsnouveau.common.items.summon_charms.*;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.perk.EmptyPerk;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.util.RegistryWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
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

@SuppressWarnings("Convert2MethodRef") //IJ can't know we need the supplier (do we actually need it tho?)
public class ItemsRegistry {

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

    public static final RegistryWrapper<RunicChalk> RUNIC_CHALK = register(LibItemNames.RUNIC_CHALK, () -> new RunicChalk());

    public static RegistryWrapper<SpellBook> NOVICE_SPELLBOOK = register(LibItemNames.NOVICE_SPELL_BOOK, () -> new SpellBook(SpellTier.ONE));
    public static RegistryWrapper<SpellBook> APPRENTICE_SPELLBOOK = register(LibItemNames.APPRENTICE_SPELL_BOOK, () -> new SpellBook(SpellTier.TWO));
    public static RegistryWrapper<SpellBook> ARCHMAGE_SPELLBOOK = register(LibItemNames.ARCHMAGE_SPELL_BOOK, () -> new SpellBook(SpellTier.THREE));
    public static RegistryWrapper<SpellBook> CREATIVE_SPELLBOOK = register(LibItemNames.CREATIVE_SPELL_BOOK, () -> new SpellBook(SpellTier.CREATIVE));
    public static RegistryWrapper<Item> BLANK_GLYPH = register(LibItemNames.BLANK_GLYPH);
    public static RegistryWrapper<ModItem> BUCKET_OF_SOURCE = register(LibItemNames.BUCKET_OF_SOURCE);
    public static RegistryWrapper<ModItem> MAGE_BLOOM = register(LibItemNames.MAGE_BLOOM, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.magebloom")));
    public static RegistryWrapper<ModItem> MAGE_FIBER = register(LibItemNames.MAGE_FIBER);
    public static RegistryWrapper<ModItem> BLAZE_FIBER = register(LibItemNames.BLAZE_FIBER);
    public static RegistryWrapper<ModItem> END_FIBER = register(LibItemNames.END_FIBER);
    public static RegistryWrapper<ModItem> MUNDANE_BELT = register(LibItemNames.MUNDANE_BELT, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.dull")));
    public static RegistryWrapper<JarOfLight> JAR_OF_LIGHT = register(LibItemNames.JAR_OF_LIGHT, () -> new JarOfLight());
    public static RegistryWrapper<BeltOfLevitation> BELT_OF_LEVITATION = register(LibItemNames.BELT_OF_LEVITATION, () -> new BeltOfLevitation());

    public static RegistryWrapper<WornNotebook> WORN_NOTEBOOK = register(LibItemNames.WORN_NOTEBOOK, () -> new WornNotebook().withTooltip(Component.translatable("tooltip.worn_notebook")));
    public static RegistryWrapper<ModItem> RING_OF_POTENTIAL = register(LibItemNames.RING_OF_POTENTIAL, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.dull")));
    public static RegistryWrapper<DiscountRing> RING_OF_LESSER_DISCOUNT = register(LibItemNames.RING_OF_LESSER_DISCOUNT, () -> new DiscountRing() {
        @Override
        public int getManaDiscount() {
            return 10;
        }
    });

    public static RegistryWrapper<DiscountRing> RING_OF_GREATER_DISCOUNT = register(LibItemNames.RING_OF_GREATER_DISCOUNT, () -> new DiscountRing() {
        @Override
        public int getManaDiscount() {
            return 20;
        }
    });

    public static RegistryWrapper<BeltOfUnstableGifts> BELT_OF_UNSTABLE_GIFTS = register(LibItemNames.BELT_OF_UNSTABLE_GIFTS, () -> new BeltOfUnstableGifts());

    public static RegistryWrapper<WarpScroll> WARP_SCROLL = register(LibItemNames.WARP_SCROLL, () -> new WarpScroll());

    public static RegistryWrapper<SpellParchment> SPELL_PARCHMENT = register(LibItemNames.SPELL_PARCHMENT, () -> new SpellParchment());

    public static RegistryWrapper<BookwyrmCharm> BOOKWYRM_CHARM = register(LibItemNames.BOOKWYRM_CHARM, () -> new BookwyrmCharm().withTooltip("ars_nouveau.tooltip.bookwyrm"));

    public static RegistryWrapper<DominionWand> DOMINION_ROD = register(LibItemNames.DOMINION_WAND, () -> new DominionWand());

    public static RegistryWrapper<AbstractManaCurio> AMULET_OF_MANA_BOOST = register(LibItemNames.AMULET_OF_MANA_BOOST, () -> new AbstractManaCurio() {
        @Override
        public int getMaxManaBoost(ItemStack i) {
            return 50;
        }
    });

    public static RegistryWrapper<AbstractManaCurio> AMULET_OF_MANA_REGEN = register(LibItemNames.AMULET_OF_MANA_REGEN, () -> new AbstractManaCurio() {

        @Override
        public int getManaRegenBonus(ItemStack i) {
            return 3;
        }

    });

    public static RegistryWrapper<ModItem> DULL_TRINKET = register(LibItemNames.DULL_TRINKET, () -> new ModItem().withTooltip(Component.translatable("ars_nouveau.tooltip.dull")));

    public static RegistryWrapper<StarbuncleCharm> STARBUNCLE_CHARM = register(LibItemNames.STARBUNCLE_CHARM, () -> new StarbuncleCharm());

    public static RegistryWrapper<Debug> debug = register("debug", () -> new Debug());

    public static RegistryWrapper<StarbuncleShard> STARBUNCLE_SHARD = register(LibItemNames.STARBUNCLE_SHARDS, () -> new StarbuncleShard().withTooltip(Component.translatable("tooltip.starbuncle_shard")));

    public static RegistryWrapper<StarbuncleShades> STARBUNCLE_SHADES = register(LibItemNames.STARBUNCLE_SHADES, () -> new StarbuncleShades().withTooltip(Component.translatable("tooltip.starbuncle_shades")));


    public static RegistryWrapper<WhirlisprigCharm> WHIRLISPRIG_CHARM = register(LibItemNames.WHIRLISPRIG_CHARM, () -> new WhirlisprigCharm());

    public static RegistryWrapper<ModItem> WHIRLISPRIG_SHARDS = register(LibItemNames.WHIRLISPRIG_SHARDS, () -> new ModItem().withTooltip(Component.translatable("tooltip.whirlisprig_shard")));

    public static RegistryWrapper<ModItem> SOURCE_GEM = register(LibItemNames.SOURCE_GEM, () -> new ModItem().withTooltip(Component.translatable("tooltip.source_gem")));

    public static RegistryWrapper<AllowItemScroll> ALLOW_ITEM_SCROLL = register(LibItemNames.ALLOW_ITEM_SCROLL, () -> new AllowItemScroll());

    public static RegistryWrapper<DenyItemScroll> DENY_ITEM_SCROLL = register(LibItemNames.DENY_ITEM_SCROLL, () -> new DenyItemScroll());

    public static RegistryWrapper<MimicItemScroll> MIMIC_ITEM_SCROLL = register(LibItemNames.MIMIC_ITEM_SCROLL, () -> new MimicItemScroll());

    public static RegistryWrapper<BlankParchmentItem> BLANK_PARCHMENT = register(LibItemNames.BLANK_PARCHMENT, () -> new BlankParchmentItem());

    public static RegistryWrapper<Wand> WAND = register(LibItemNames.WAND, () -> new Wand());

    public static RegistryWrapper<VoidJar> VOID_JAR = register(LibItemNames.VOID_JAR, () -> new VoidJar());

    public static RegistryWrapper<WixieCharm> WIXIE_CHARM = register(LibItemNames.WIXIE_CHARM, () -> new WixieCharm());

    public static RegistryWrapper<ModItem> WIXIE_SHARD = register(LibItemNames.WIXIE_SHARD, () -> new ModItem().withTooltip(Component.translatable("tooltip.wixie_shard")));

    public static RegistryWrapper<SpellBow> SPELL_BOW = register(LibItemNames.SPELL_BOW, () -> new SpellBow());

    public static RegistryWrapper<SpellArrow> AMPLIFY_ARROW = register(LibItemNames.AMPLIFY_ARROW, () -> new SpellArrow(AugmentAmplify.INSTANCE, 2));

    public static RegistryWrapper<FormSpellArrow> SPLIT_ARROW = register(LibItemNames.SPLIT_ARROW, () -> new FormSpellArrow(AugmentSplit.INSTANCE, 2));

    public static RegistryWrapper<FormSpellArrow> PIERCE_ARROW = register(LibItemNames.PIERCE_ARROW, () -> new FormSpellArrow(AugmentPierce.INSTANCE, 2));

    public static RegistryWrapper<ModItem> WILDEN_HORN = register(LibItemNames.WILDEN_HORN, () -> new ModItem().withTooltip(Component.translatable("tooltip.wilden_horn")));

    public static RegistryWrapper<ModItem> WILDEN_SPIKE = register(LibItemNames.WILDEN_SPIKE, () -> new ModItem().withTooltip(Component.translatable("tooltip.wilden_spike")));

    public static RegistryWrapper<ModItem> WILDEN_WING = register(LibItemNames.WILDEN_WING, () -> new ModItem().withTooltip(Component.translatable("tooltip.wilden_wing")));

    public static RegistryWrapper<PotionFlask> POTION_FLASK = register(LibItemNames.POTION_FLASK, () -> new PotionFlask() {
       @NotNull
        @Override
        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
            return effectInstance;
        }
    }.withTooltip(Component.translatable("tooltip.potion_flask")));

    public static RegistryWrapper<PotionFlask> POTION_FLASK_AMPLIFY = register(LibItemNames.POTION_FLASK_AMPLIFY, () -> new PotionFlask() {
        @Override
        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {

            return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() / 2, Math.min(Config.ENCHANTED_FLASK_CAP.get(), effectInstance.getAmplifier() + 1));
        }
    }.withTooltip(Component.translatable("tooltip.potion_flask_amplify")));


    public static RegistryWrapper<PotionFlask> POTION_FLASK_EXTEND_TIME = register(LibItemNames.POTION_FLASK_EXTEND_TIME, () -> new PotionFlask() {
        @Override
        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
            return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() + effectInstance.getDuration() / 2, effectInstance.getAmplifier());
        }
    }.withTooltip(Component.translatable("tooltip.potion_flask_extend_time")));


    public static RegistryWrapper<ExperienceGem> EXPERIENCE_GEM = register(LibItemNames.EXP_GEM, () -> new ExperienceGem() {
        @Override
        public int getValue() {
            return 3;
        }
    }.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem")));

    public static RegistryWrapper<ExperienceGem> GREATER_EXPERIENCE_GEM = register(LibItemNames.GREATER_EXP_GEM, () -> new ExperienceGem() {
        @Override
        public int getValue() {
            return 12;
        }
    }.withTooltip(Component.translatable("ars_nouveau.tooltip.exp_gem")));

    public static RegistryWrapper<EnchantersSword> ENCHANTERS_SWORD = register(LibItemNames.ENCHANTERS_SWORD, () -> new EnchantersSword(Tiers.NETHERITE, 3, -2.4F));

    public static RegistryWrapper<EnchantersShield> ENCHANTERS_SHIELD = register(LibItemNames.ENCHANTERS_SHIELD, () -> new EnchantersShield());

    public static RegistryWrapper<CasterTome> CASTER_TOME = register(LibItemNames.CASTER_TOME, () -> new CasterTome());

    public static RegistryWrapper<DrygmyCharm> DRYGMY_CHARM = register(LibItemNames.DRYGMY_CHARM, () -> new DrygmyCharm());

    public static RegistryWrapper<ModItem> DRYGMY_SHARD = register(LibItemNames.DRYGMY_SHARD, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.drygmy_shard")));

    public static RegistryWrapper<ModItem> WILDEN_TRIBUTE = register(LibItemNames.WILDEN_TRIBUTE, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.wilden_tribute").withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE))).withRarity(Rarity.EPIC));

    public static RegistryWrapper<SummoningFocus> SUMMONING_FOCUS = register(LibItemNames.SUMMON_FOCUS, () -> new SummoningFocus());


    public static RegistryWrapper<ShapersFocus> SHAPERS_FOCUS = register(LibItemNames.SHAPERS_FOCUS, () -> new ShapersFocus(defaultItemProperties().stacksTo(1)).withTooltip(Component.translatable("tooltip.ars_nouveau.shapers_focus")));

    public static RegistryWrapper<ModItem> SOURCE_BERRY_PIE = register(LibItemNames.SOURCE_BERRY_PIE, () -> new ModItem(defaultItemProperties().food(SOURCE_PIE_FOOD)).withTooltip(Component.translatable("tooltip.ars_nouveau.source_food")));

    public static RegistryWrapper<ModItem> SOURCE_BERRY_ROLL = register(LibItemNames.SOURCE_BERRY_ROLL, () -> new ModItem(defaultItemProperties().food(SOURCE_ROLL_FOOD)).withTooltip(Component.translatable("tooltip.ars_nouveau.source_food")));

    public static RegistryWrapper<EnchantersMirror> ENCHANTERS_MIRROR = register(LibItemNames.ENCHANTERS_MIRROR, () -> new EnchantersMirror(defaultItemProperties().stacksTo(1)));

    public static RegistryWrapper<LightArmor> NOVICE_BOOTS = register(LibItemNames.NOVICE_BOOTS, () -> new LightArmor(EquipmentSlot.FEET));

    public static RegistryWrapper<LightArmor> NOVICE_LEGGINGS = register(LibItemNames.NOVICE_LEGGINGS, () -> new LightArmor(EquipmentSlot.LEGS));

    public static RegistryWrapper<LightArmor> NOVICE_ROBES = register(LibItemNames.NOVICE_ROBES, () -> new LightArmor(EquipmentSlot.CHEST));

    public static RegistryWrapper<LightArmor> NOVICE_HOOD = register(LibItemNames.NOVICE_HOOD, () -> new LightArmor(EquipmentSlot.HEAD));

    public static RegistryWrapper<MediumArmor> APPRENTICE_BOOTS = register(LibItemNames.APPRENTICE_BOOTS, () -> new MediumArmor(EquipmentSlot.FEET));
    public static RegistryWrapper<MediumArmor> APPRENTICE_LEGGINGS = register(LibItemNames.APPRENTICE_LEGGINGS, () -> new MediumArmor(EquipmentSlot.LEGS));

    public static RegistryWrapper<MediumArmor> APPRENTICE_ROBES = register(LibItemNames.APPRENTICE_ROBES, () -> new MediumArmor(EquipmentSlot.CHEST));

    public static RegistryWrapper<MediumArmor> APPRENTICE_HOOD = register(LibItemNames.APPRENTICE_HOOD, () -> new MediumArmor(EquipmentSlot.HEAD));

    public static RegistryWrapper<HeavyArmor> ARCHMAGE_BOOTS = register(LibItemNames.ARCHMAGE_BOOTS, () -> new HeavyArmor(EquipmentSlot.FEET));

    public static RegistryWrapper<HeavyArmor> ARCHMAGE_LEGGINGS = register(LibItemNames.ARCHMAGE_LEGGINGS, () -> new HeavyArmor(EquipmentSlot.LEGS));

    public static RegistryWrapper<HeavyArmor> ARCHMAGE_ROBES = register(LibItemNames.ARCHMAGE_ROBES, () -> new HeavyArmor(EquipmentSlot.CHEST));

    public static RegistryWrapper<HeavyArmor> ARCHMAGE_HOOD = register(LibItemNames.ARCHMAGE_HOOD, () -> new HeavyArmor(EquipmentSlot.HEAD));


    public static RegistryWrapper<DowsingRod> DOWSING_ROD = register(LibItemNames.DOWSING_ROD, () -> new DowsingRod().withTooltip(Component.translatable("tooltip.ars_nouveau.dowsing_rod")));

    public static RegistryWrapper<ModItem> ABJURATION_ESSENCE = register(LibItemNames.ABJURATION_ESSENCE, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.essences")));

    public static RegistryWrapper<ModItem> CONJURATION_ESSENCE = register(LibItemNames.CONJURATION_ESSENCE, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.essences")));

    public static RegistryWrapper<ModItem> AIR_ESSENCE = register(LibItemNames.AIR_ESSENCE, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.essences")));

    public static RegistryWrapper<EarthEssence> EARTH_ESSENCE = register(LibItemNames.EARTH_ESSENCE, () -> new EarthEssence().withTooltip(Component.translatable("tooltip.ars_nouveau.essences")));

    public static RegistryWrapper<FireEssence> FIRE_ESSENCE = register(LibItemNames.FIRE_ESSENCE, () -> new FireEssence().withTooltip(Component.translatable("tooltip.ars_nouveau.essences")));

    public static RegistryWrapper<ModItem> MANIPULATION_ESSENCE = register(LibItemNames.MANIPULATION_ESSENCE, () -> new ManipulationEssence().withTooltip(Component.translatable("tooltip.ars_nouveau.essences")));

    public static RegistryWrapper<ModItem> WATER_ESSENCE = register(LibItemNames.WATER_ESSENCE, () -> new ModItem().withTooltip(Component.translatable("tooltip.ars_nouveau.essences")));

    public static RegistryWrapper<AmethystGolemCharm> AMETHYST_GOLEM_CHARM = register(LibItemNames.AMETHYST_GOLEM_CHARM, () -> new AmethystGolemCharm().withTooltip(Component.translatable("tooltip.ars_nouveau.amethyst_charm")));
    public static RegistryWrapper<AnnotatedCodex> ANNOTATED_CODEX = register(LibItemNames.ANNOTATED_CODEX, () -> new AnnotatedCodex());
    public static RegistryWrapper<ScryerScroll> SCRYER_SCROLL = register(LibItemNames.SCRYER_SCROLL, () -> new ScryerScroll().withTooltip(Component.translatable("tooltip.ars_nouveau.scryer_scroll")));

    public static RegistryWrapper<ModItem> WIXIE_HAT = register(LibItemNames.WIXIE_HAT, () -> new WixieHat().withTooltip("tooltip.ars_nouveau.wixie_hat"));
    public static RegistryWrapper<ModItem> ALCHEMISTS_CROWN = register(LibItemNames.ALCHEMISTS_CROWN, () -> new AlchemistsCrown(defaultItemProperties().stacksTo(1)));

    public static RegistryWrapper<ModItem> SPLASH_LAUNCHER = register(LibItemNames.SPLASH_LAUNCHER, () -> new FlaskCannon.SplashLauncher(defaultItemProperties().stacksTo(1)));
    public static RegistryWrapper<ModItem> LINGERING_LAUNCHER = register(LibItemNames.LINGERING_LAUNCHER, () -> new FlaskCannon.LingeringLauncher(defaultItemProperties().stacksTo(1)));
    public static PerkItem BLANK_THREAD;

    public static RegistryWrapper<Item> FIREL_DISC = register(LibItemNames.FIREL_DISC, () -> new RecordItem(9, () -> SoundRegistry.ARIA_BIBLIO.get(), defaultItemProperties().stacksTo(1).rarity(Rarity.RARE), 20 * 240));
    public static RegistryWrapper<Item> SOUND_OF_GLASS = register(LibItemNames.SOUND_OF_GLASS, () -> new RecordItem(9, () -> SoundRegistry.SOUND_OF_GLASS.get(), defaultItemProperties().stacksTo(1).rarity(Rarity.RARE), 20 * 182));
    public static RegistryWrapper<Item> WILD_HUNT = register(LibItemNames.FIREL_WILD_HUNT, () -> new RecordItem(9, () -> SoundRegistry.WILD_HUNT.get(), defaultItemProperties().stacksTo(1).rarity(Rarity.RARE), 20 * 121));

    public static RegistryWrapper<Item> STARBY_GIFY = register(LibItemNames.STARBY_GIFT, () -> new Present(defaultItemProperties().rarity(Rarity.EPIC)));

    public static RegistryWrapper<Item> SPELL_CROSSBOW = register(LibItemNames.SPELL_CROSSBOW, () -> new SpellCrossbow(defaultItemProperties().stacksTo(1)));
    public static RegistryWrapper<Item> STABLE_WARP_SCROLL = register(LibItemNames.STABLE_WARP_SCROLL, () -> new StableWarpScroll(defaultItemProperties().stacksTo(1)));
    public static RegistryWrapper<ScryCaster> SCRY_CASTER = register(LibItemNames.SCRY_CASTER, () -> new ScryCaster(defaultItemProperties().stacksTo(1)));

    public static RegistryWrapper<JumpingRing> JUMP_RING = register(LibItemNames.JUMP_RING, () -> new JumpingRing());

    public static RegistryWrapper register(String name, Supplier<? extends Item> item) {
        return new RegistryWrapper<>(ITEMS.register(name, item));
    }

    public static RegistryWrapper register(String name) {
        return register(name, () -> new ModItem());
    }

    public static void onItemRegistry(IForgeRegistry<Item> registry) {
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        for (Map.Entry<ResourceLocation, Supplier<Glyph>> glyphEntry : api.getGlyphItemMap().entrySet()) {
            Glyph glyph = glyphEntry.getValue().get();
            registry.register(glyphEntry.getKey(), glyph);
            glyph.spellPart.glyphItem = glyph;
        }

        for (AbstractRitual ritual : api.getRitualMap().values()) {
            RitualTablet tablet = new RitualTablet(ritual);
            registry.register(ritual.getRegistryName(), tablet);
            api.getRitualItemMap().put(ritual.getRegistryName(), tablet);
        }

        for (AbstractFamiliarHolder holder : api.getFamiliarHolderMap().values()) {
            FamiliarScript script = new FamiliarScript(holder);
            api.getFamiliarScriptMap().put(holder.getRegistryName(), script);
            registry.register(holder.getRegistryName(), script);
        }

        for(IPerk perk : api.getPerkMap().values()) {
            PerkItem perkItem = new PerkItem(perk);
            api.getPerkItemMap().put(perk.getRegistryName(), perkItem);
            registry.register(perk.getRegistryName(), perkItem);
            if(perk instanceof EmptyPerk){
                BLANK_THREAD = perkItem;
            }
        }

        registry.register(LibItemNames.DRYGMY_SE, new ForgeSpawnEggItem(ModEntities.ENTITY_DRYGMY, 10051392, 0xFFE633, defaultItemProperties()));
        registry.register(LibItemNames.STARBUNCLE_SE, new ForgeSpawnEggItem(ModEntities.STARBUNCLE_TYPE, 0xFFB233, 0xFFE633, defaultItemProperties()));
        registry.register(LibItemNames.SYLPH_SE, new ForgeSpawnEggItem(ModEntities.WHIRLISPRIG_TYPE, 0x77FF33, 0xFFFB00, defaultItemProperties()));
        registry.register(LibItemNames.WILDEN_HUNTER_SE, new ForgeSpawnEggItem(ModEntities.WILDEN_HUNTER, 0xFDFDFD, 0xCAA97F, defaultItemProperties()));
        registry.register(LibItemNames.WILDEN_GUARDIAN_SE, new ForgeSpawnEggItem(ModEntities.WILDEN_GUARDIAN, 0xFFFFFF, 0xFF9E00, defaultItemProperties()));
        registry.register(LibItemNames.WILDEN_STALKER_SE, new ForgeSpawnEggItem(ModEntities.WILDEN_STALKER, 0x9B650C, 0xEF1818, defaultItemProperties()));
    }


    public static Item.Properties defaultItemProperties() {
        return new Item.Properties().tab(ArsNouveau.itemGroup);
    }
}

