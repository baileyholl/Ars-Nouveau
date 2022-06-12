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
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
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

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

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
    @ObjectHolder("debug")public static Debug debug;
    @ObjectHolder(LibItemNames.STARBUNCLE_SHARDS)public static ModItem STARBUNCLE_SHARD;
    @ObjectHolder(LibItemNames.STARBUNCLE_SHADES)public static StarbuncleShades STARBUNCLE_SHADES;

    @ObjectHolder(LibItemNames.WHIRLISPRIG_CHARM)public static WhirlisprigCharm WHIRLISPRIG_CHARM;
    @ObjectHolder(LibItemNames.WHIRLISPRIG_SHARDS)public static ModItem WHIRLISPRIG_SHARDS;
    @ObjectHolder(LibItemNames.SOURCE_GEM)public static ModItem SOURCE_GEM;
    @ObjectHolder(LibItemNames.ALLOW_ITEM_SCROLL)public static AllowItemScroll ALLOW_ITEM_SCROLL;
    @ObjectHolder(LibItemNames.DENY_ITEM_SCROLL)public static DenyItemScroll DENY_ITEM_SCROLL;
    @ObjectHolder(LibItemNames.MIMIC_ITEM_SCROLL)public static MimicItemScroll MIMIC_ITEM_SCROLL;
    @ObjectHolder(LibItemNames.BLANK_PARCHMENT)public static BlankParchmentItem BLANK_PARCHMENT;
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
    @ObjectHolder(LibItemNames.NOVICE_BOOTS)public static NoviceArmor NOVICE_BOOTS;
    @ObjectHolder(LibItemNames.NOVICE_LEGGINGS)public static NoviceArmor NOVICE_LEGGINGS;
    @ObjectHolder(LibItemNames.NOVICE_ROBES)public static NoviceArmor NOVICE_ROBES;
    @ObjectHolder(LibItemNames.NOVICE_HOOD)public static NoviceArmor NOVICE_HOOD;
    @ObjectHolder(LibItemNames.APPRENTICE_BOOTS)public static ApprenticeArmor APPRENTICE_BOOTS;
    @ObjectHolder(LibItemNames.APPRENTICE_LEGGINGS)public static ApprenticeArmor APPRENTICE_LEGGINGS;
    @ObjectHolder(LibItemNames.APPRENTICE_ROBES)public static ApprenticeArmor APPRENTICE_ROBES;
    @ObjectHolder(LibItemNames.APPRENTICE_HOOD)public static ApprenticeArmor APPRENTICE_HOOD;
    @ObjectHolder(LibItemNames.ARCHMAGE_BOOTS)public static MasterArmor ARCHMAGE_BOOTS;
    @ObjectHolder(LibItemNames.ARCHMAGE_LEGGINGS)public static MasterArmor ARCHMAGE_LEGGINGS;
    @ObjectHolder(LibItemNames.ARCHMAGE_ROBES)public static MasterArmor ARCHMAGE_ROBES;
    @ObjectHolder(LibItemNames.ARCHMAGE_HOOD)public static MasterArmor ARCHMAGE_HOOD;

    @ObjectHolder(LibItemNames.DOWSING_ROD)public static DowsingRod DOWSING_ROD;
    @ObjectHolder(LibItemNames.ABJURATION_ESSENCE)public static ModItem ABJURATION_ESSENCE;
    @ObjectHolder(LibItemNames.CONJURATION_ESSENCE)public static ModItem CONJURATION_ESSENCE;
    @ObjectHolder(LibItemNames.AIR_ESSENCE)public static ModItem AIR_ESSENCE;
    @ObjectHolder(LibItemNames.EARTH_ESSENCE)public static EarthEssence EARTH_ESSENCE;
    @ObjectHolder(LibItemNames.FIRE_ESSENCE)public static FireEssence FIRE_ESSENCE;
    @ObjectHolder(LibItemNames.MANIPULATION_ESSENCE)public static ModItem MANIPULATION_ESSENCE;
    @ObjectHolder(LibItemNames.WATER_ESSENCE)public static ModItem WATER_ESSENCE;
    @ObjectHolder(LibItemNames.AMETHYST_GOLEM_CHARM)public static AmethystGolemCharm AMETHYST_GOLEM_CHARM;
    @ObjectHolder(LibItemNames.ANNOTATED_CODEX)public static AnnotatedCodex ANNOTATED_CODEX;
    @ObjectHolder(LibItemNames.SCRYER_SCROLL)public static ScryerScroll SCRYER_SCROLL;

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
                    new ModItem(LibItemNames.DULL_TRINKET).withTooltip(Component.translatable("ars_nouveau.tooltip.dull")),
                    new ModItem(LibItemNames.BLAZE_FIBER),
                    new ModItem(LibItemNames.END_FIBER),
                    new ModItem(LibItemNames.MAGE_BLOOM).withTooltip(Component.translatable("ars_nouveau.tooltip.magebloom")),
                    new ModItem(LibItemNames.MAGE_FIBER),
                    new ModItem(LibItemNames.MUNDANE_BELT).withTooltip(Component.translatable("ars_nouveau.tooltip.dull")),
                    new ModItem(LibItemNames.RING_OF_POTENTIAL).withTooltip(Component.translatable("ars_nouveau.tooltip.dull")),
                    new BeltOfUnstableGifts(LibItemNames.BELT_OF_UNSTABLE_GIFTS),
                    new ModItem(defaultItemProperties().stacksTo(1), LibItemNames.BUCKET_OF_SOURCE),
                    new NoviceArmor(EquipmentSlot.FEET).setRegistryName(LibItemNames.NOVICE_BOOTS),
                    new NoviceArmor(EquipmentSlot.LEGS).setRegistryName(LibItemNames.NOVICE_LEGGINGS),
                    new NoviceArmor(EquipmentSlot.CHEST).setRegistryName(LibItemNames.NOVICE_ROBES),
                    new NoviceArmor(EquipmentSlot.HEAD).setRegistryName(LibItemNames.NOVICE_HOOD),
                    new ApprenticeArmor(EquipmentSlot.FEET).setRegistryName(LibItemNames.APPRENTICE_BOOTS),
                    new ApprenticeArmor(EquipmentSlot.LEGS).setRegistryName(LibItemNames.APPRENTICE_LEGGINGS),
                    new ApprenticeArmor(EquipmentSlot.CHEST).setRegistryName(LibItemNames.APPRENTICE_ROBES),
                    new ApprenticeArmor(EquipmentSlot.HEAD).setRegistryName(LibItemNames.APPRENTICE_HOOD),
                    new MasterArmor(EquipmentSlot.FEET).setRegistryName(LibItemNames.ARCHMAGE_BOOTS),
                    new MasterArmor(EquipmentSlot.LEGS).setRegistryName(LibItemNames.ARCHMAGE_LEGGINGS),
                    new MasterArmor(EquipmentSlot.CHEST).setRegistryName(LibItemNames.ARCHMAGE_ROBES),
                    new MasterArmor(EquipmentSlot.HEAD).setRegistryName(LibItemNames.ARCHMAGE_HOOD),
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
                    new AbstractManaCurio(LibItemNames.AMULET_OF_MANA_BOOST){
                        @Override
                        public int getMaxManaBoost(ItemStack i) {
                            return 50;
                        }
                    },
                    new AbstractManaCurio(LibItemNames.AMULET_OF_MANA_REGEN){

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
                    new SpellBow().setRegistryName(LibItemNames.SPELL_BOW),
                    new FormSpellArrow(LibItemNames.PIERCE_ARROW, AugmentPierce.INSTANCE, 2),
                    new FormSpellArrow(LibItemNames.SPLIT_ARROW, AugmentSplit.INSTANCE, 2),
                    new SpellArrow(LibItemNames.AMPLIFY_ARROW, AugmentAmplify.INSTANCE, 2),
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
                            return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration() + effectInstance.getDuration()/2, effectInstance.getAmplifier());
                        }
                    }.withTooltip(Component.translatable("tooltip.potion_flask_extend_time")),
                    new PotionFlask(LibItemNames.POTION_FLASK_AMPLIFY) {
                        @Override
                        public MobEffectInstance getEffectInstance(MobEffectInstance effectInstance) {
                            return new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration()/2, effectInstance.getAmplifier() + 1);
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
                    new EnchantersShield(),
                    new EnchantersSword(Tiers.NETHERITE, 3, -2.4F).setRegistryName(LibItemNames.ENCHANTERS_SWORD),
                    new ForgeSpawnEggItem(() -> ModEntities.STARBUNCLE_TYPE, 0xFFB233,0xFFE633,defaultItemProperties()).setRegistryName(LibItemNames.STARBUNCLE_SE),
                    new ForgeSpawnEggItem(() -> ModEntities.WHIRLISPRIG_TYPE, 0x77FF33,0xFFFB00,defaultItemProperties()).setRegistryName(LibItemNames.SYLPH_SE),
                    new ForgeSpawnEggItem(() -> ModEntities.WILDEN_HUNTER, 0xFDFDFD,0xCAA97F,defaultItemProperties()).setRegistryName(LibItemNames.WILDEN_HUNTER_SE),
                    new ForgeSpawnEggItem(() -> ModEntities.WILDEN_GUARDIAN, 0xFFFFFF,0xFF9E00,defaultItemProperties()).setRegistryName(LibItemNames.WILDEN_GUARDIAN_SE),
                    new ForgeSpawnEggItem(() -> ModEntities.WILDEN_STALKER, 0x9B650C,0xEF1818,defaultItemProperties()).setRegistryName(LibItemNames.WILDEN_STALKER_SE),
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
            };

            final IForgeRegistry<Item> registry = event.getRegistry();
            for(Supplier<Glyph> glyph : ArsNouveauAPI.getInstance().getGlyphItemMap().values()){
                registry.register(glyph.get());
                ITEMS.add(glyph.get());
            }

            for(AbstractRitual ritual : ArsNouveauAPI.getInstance().getRitualMap().values()){
                RitualTablet tablet = new RitualTablet(ArsNouveauAPI.getInstance().getRitualRegistryName(ritual.getID()), ritual);
                registry.register(tablet);
                ArsNouveauAPI.getInstance().getRitualItemMap().put(ritual.getID(), tablet);
                ITEMS.add(tablet);
            }

            for(AbstractFamiliarHolder holder : ArsNouveauAPI.getInstance().getFamiliarHolderMap().values()){
                FamiliarScript script = new FamiliarScript(holder);
                ArsNouveauAPI.getInstance().getFamiliarScriptMap().put(holder.id, script);
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

