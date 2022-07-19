package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.RegistryHelper;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ArsNouveau.MODID)
public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static final String CATEGORY_SPELLS = "spells";
    public static final String DRYGMY_CATEGORY = "drygmy_production";

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.BooleanValue SPAWN_ORE;
    public static ForgeConfigSpec.BooleanValue SPAWN_BERRIES;
    public static ForgeConfigSpec.BooleanValue SPAWN_BOOK;
    public static ForgeConfigSpec.IntValue INIT_MAX_MANA;
    public static ForgeConfigSpec.IntValue INIT_MANA_REGEN;

    public static ForgeConfigSpec.IntValue GLYPH_MAX_BONUS;
    public static ForgeConfigSpec.DoubleValue GLYPH_REGEN_BONUS;
    public static ForgeConfigSpec.IntValue TREE_SPAWN_RATE;


    public static ForgeConfigSpec.IntValue TIER_MAX_BONUS;
    public static ForgeConfigSpec.IntValue MANA_BOOST_BONUS;
    public static ForgeConfigSpec.IntValue MANA_REGEN_ENCHANT_BONUS;
    public static ForgeConfigSpec.IntValue MANA_REGEN_POTION;


    public static ForgeConfigSpec.IntValue REGEN_INTERVAL;
    public static ForgeConfigSpec.IntValue CARBUNCLE_WEIGHT;
    public static ForgeConfigSpec.IntValue SYLPH_WEIGHT; // TODO: rename sylph to whirlisprig
    public static ForgeConfigSpec.IntValue DRYGMY_WEIGHT;

    public static ForgeConfigSpec.IntValue DRYGMY_MANA_COST;
    public static ForgeConfigSpec.IntValue SYLPH_MANA_COST;
    public static ForgeConfigSpec.IntValue WHIRLISPRIG_MAX_PROGRESS;
    public static ForgeConfigSpec.IntValue DRYGMY_MAX_PROGRESS;
    public static ForgeConfigSpec.IntValue DRYGMY_BASE_ITEM;
    public static ForgeConfigSpec.IntValue DRYGMY_UNIQUE_BONUS;
    public static ForgeConfigSpec.IntValue DRYGMY_QUANTITY_CAP;

    public static ForgeConfigSpec.IntValue WGUARDIAN_WEIGHT;
    public static ForgeConfigSpec.IntValue WSTALKER_WEIGHT;
    public static ForgeConfigSpec.IntValue WHUNTER_WEIGHT;
    public static ForgeConfigSpec.BooleanValue HUNTER_ATTACK_ANIMALS;
    public static ForgeConfigSpec.BooleanValue STALKER_ATTACK_ANIMALS;
    public static ForgeConfigSpec.BooleanValue GUARDIAN_ATTACK_ANIMALS;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;

    public static ForgeConfigSpec.IntValue ARCHWOOD_FOREST_WEIGHT;
    public static ForgeConfigSpec.BooleanValue ENFORCE_AUGMENT_CAP_ON_CAST;
    public static ForgeConfigSpec.IntValue CODEX_COST_PER_GLYPH;

    public static ForgeConfigSpec.BooleanValue DYNAMIC_LIGHTS_ENABLED;
    public static ForgeConfigSpec.IntValue TOUCH_LIGHT_LUMINANCE;
    public static ForgeConfigSpec.IntValue TOUCH_LIGHT_DURATION;
    public static ForgeConfigSpec.BooleanValue ENFORCE_GLYPH_LIMIT_ON_CAST;

    public static ForgeConfigSpec.BooleanValue SPAWN_TOMES;
    public static ForgeConfigSpec.IntValue TOOLTIP_X_OFFSET;
    public static ForgeConfigSpec.IntValue TOOLTIP_Y_OFFSET;
    public static ForgeConfigSpec.IntValue MANABAR_X_OFFSET;
    public static ForgeConfigSpec.IntValue MANABAR_Y_OFFSET;
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> ENTITY_LIGHT_CONFIG;
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_LIGHT_CONFIG;
    // Convert to ResourceLocations so we dont need to create so many objects over strings
    public static Map<ResourceLocation, Integer> ENTITY_LIGHT_MAP = new HashMap<>();
    public static Map<ResourceLocation, Integer> ITEM_LIGHTMAP = new HashMap<>();

    public static boolean isSpellEnabled(String tag){
        AbstractSpellPart spellPart = ArsNouveauAPI.getInstance().getSpellpartMap().get(tag);
        if(spellPart == null){
            throw new IllegalArgumentException("Spell Part with id " + tag + " does not exist in registry. Did you pass the right ID?");
        }

        return spellPart.ENABLED == null || spellPart.ENABLED.get();
    }

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        CLIENT_BUILDER.comment("Lighting").push("lights");
        DYNAMIC_LIGHTS_ENABLED = CLIENT_BUILDER.comment("If dynamic lights are enabled").define("lightsEnabled", false);
        TOUCH_LIGHT_LUMINANCE = CLIENT_BUILDER.comment("How bright the touch light is").defineInRange("touchLightLuminance", 8, 0, 15);
        TOUCH_LIGHT_DURATION = CLIENT_BUILDER.comment("How long the touch light lasts in ticks").defineInRange("touchLightDuration", 8, 0, 40);
        ENTITY_LIGHT_CONFIG = CLIENT_BUILDER.comment("Light level an entity should emit when dynamic lights are on", "Example entry: minecraft:blaze=15")
                .defineList("entity_lights",ConfigUtil.writeConfig(getDefaultEntityLight()), ConfigUtil::validateMap);
        ITEM_LIGHT_CONFIG = CLIENT_BUILDER.comment("Light level an item should emit when held when dynamic lights are on", "Example entry: minecraft:stick=15")
                .defineList("item_lights", ConfigUtil.writeConfig(getDefaultItemLight()), ConfigUtil::validateMap);
        TOOLTIP_X_OFFSET = CLIENT_BUILDER.comment("X offset for the tooltip").defineInRange("xTooltip", 20, Integer.MIN_VALUE, Integer.MAX_VALUE);
        TOOLTIP_Y_OFFSET = CLIENT_BUILDER.comment("Y offset for the tooltip").defineInRange("yTooltip", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        MANABAR_X_OFFSET = CLIENT_BUILDER.comment("X offset for the Mana Bar").defineInRange("xManaBar", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        MANABAR_Y_OFFSET = CLIENT_BUILDER.comment("Y offset for the Mana Bar").defineInRange("yManaBar", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        DIMENSION_BLACKLIST = SERVER_BUILDER.comment("Dimensions where hostile mobs will not spawn. Ex: [\"minecraft:overworld\", \"undergarden:undergarden\"]. . Run /forge dimensions for a list.").defineList("dimensionBlacklist", new ArrayList<>(),(o) -> true);
        TREE_SPAWN_RATE = SERVER_BUILDER.comment("Rate of tree spawn per chunk").defineInRange("treeWeight", 100, 0, Integer.MAX_VALUE);
        SPAWN_BERRIES = SERVER_BUILDER.comment("Spawn Mana Berry Bushes in the world").define("genBerries", true);
        SPAWN_BOOK = SERVER_BUILDER.comment("Spawn a book in the players inventory on login").define("spawnBook", true);
        CARBUNCLE_WEIGHT = SERVER_BUILDER.comment("How often Carbuncles spawn").defineInRange("carbuncleWeight",5,0,100);
        SYLPH_WEIGHT = SERVER_BUILDER.comment("How often Whirlisprigs spawn").defineInRange("sylphWeight",5,0,100);
        DRYGMY_WEIGHT = SERVER_BUILDER.comment("How often Drygmys spawn").defineInRange("drygmyWeight",3,0,100);
        SYLPH_MANA_COST = SERVER_BUILDER.comment("How much mana whirlisprigs consume per generation").defineInRange("sylphManaCost",250,0,10000);
        WHIRLISPRIG_MAX_PROGRESS = SERVER_BUILDER.comment("How much progress whirlisprigs must accumulate before creating resources")
                .defineInRange("whirlisprigProgress",250,0,10000);
        WGUARDIAN_WEIGHT = SERVER_BUILDER.comment("How often Wilden Guardians spawn").defineInRange("wguardianWeight",50,0,200);
        WSTALKER_WEIGHT = SERVER_BUILDER.comment("How often Wilden Stalkers spawn").defineInRange("wstalkerWeight",50,0,200);
        WHUNTER_WEIGHT = SERVER_BUILDER.comment("How often Wilden Hunter spawn").defineInRange("whunterWeight",50,0,200);
        HUNTER_ATTACK_ANIMALS = SERVER_BUILDER.comment("Should the Wilden Hunter attack animals?").define("hunterHuntsAnimals", true);
        STALKER_ATTACK_ANIMALS = SERVER_BUILDER.comment("Should the Wilden Stalker attack animals?").define("stalkerHuntsAnimals", false);
        GUARDIAN_ATTACK_ANIMALS = SERVER_BUILDER.comment("Should the Wilden Defender attack animals?").define("defenderHuntsAnimals", false);
//        ARCHWOOD_FOREST_WEIGHT = SERVER_BUILDER.comment("Archwood forest spawn weight").defineInRange("archwoodForest", 3, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
        SERVER_BUILDER.push(DRYGMY_CATEGORY);
        DRYGMY_MANA_COST = SERVER_BUILDER.comment("How much source drygmys consume per generation").defineInRange("drygmyManaCost",1000,0,10000);
        DRYGMY_MAX_PROGRESS = SERVER_BUILDER.comment("How many channels must occur before a drygmy produces loot").defineInRange("drygmyMaxProgress",20,0,300);
        DRYGMY_UNIQUE_BONUS = SERVER_BUILDER.comment("Bonus number of items a drygmy produces per unique mob").defineInRange("drygmyUniqueBonus",2,0,300);
        DRYGMY_BASE_ITEM = SERVER_BUILDER.comment("Base number of items a drygmy produces per cycle before bonuses.").defineInRange("drygmyBaseItems",1,Integer.MIN_VALUE,Integer.MAX_VALUE);
        DRYGMY_QUANTITY_CAP = SERVER_BUILDER.comment("Max Bonus number of items a drygmy produces from nearby entities. Each entity equals 1 item.").defineInRange("drygmyQuantityCap",5,0,300);

        SERVER_BUILDER.pop();
        SERVER_BUILDER.comment("Mana").push("mana");
        INIT_MANA_REGEN = SERVER_BUILDER.comment("Base mana regen in seconds").defineInRange("baseRegen", 5, 0, Integer.MAX_VALUE);
        INIT_MAX_MANA = SERVER_BUILDER.comment("Base max mana").defineInRange("baseMax", 100, 0, Integer.MAX_VALUE);
        REGEN_INTERVAL = SERVER_BUILDER.comment("How often max and regen will be calculated, in ticks. NOTE: Having the base mana regen AT LEAST this value is recommended.")
                .defineInRange("updateInterval", 5, 1, 20);
        GLYPH_MAX_BONUS = SERVER_BUILDER.comment("Max mana bonus per glyph").defineInRange("glyphmax", 15, 0, Integer.MAX_VALUE);
        TIER_MAX_BONUS = SERVER_BUILDER.comment("Max mana bonus for tier of book").defineInRange("tierMax", 50, 0, Integer.MAX_VALUE);
        MANA_BOOST_BONUS = SERVER_BUILDER.comment("Mana Boost value per level").defineInRange("manaBoost", 25, 0, Integer.MAX_VALUE);
        MANA_REGEN_ENCHANT_BONUS = SERVER_BUILDER.comment("(enchantment) Mana regen per second per level").defineInRange("manaRegenEnchantment", 2, 0, Integer.MAX_VALUE);
        GLYPH_REGEN_BONUS = SERVER_BUILDER.comment("Regen bonus per glyph").defineInRange("glyphRegen", 0.33, 0.0, Integer.MAX_VALUE);
        MANA_REGEN_POTION = SERVER_BUILDER.comment("Regen bonus per potion level").defineInRange("potionRegen", 10, 0, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();
        SERVER_BUILDER.push("Spells");
        ENFORCE_AUGMENT_CAP_ON_CAST = SERVER_BUILDER.comment("Enforce augment cap on casting? Turn this off if you are a pack maker and want to create more powerful items than players.")
                .define("enforceCapOnCast", true);
        ENFORCE_GLYPH_LIMIT_ON_CAST = SERVER_BUILDER.comment("Enforce glyph per spell limit on casting? Turn this off if you are a pack maker and want to create more powerful items than players.")
                .define("enforceGlyphLimitOnCast", true);

        SERVER_BUILDER.comment("Items").push("item");
        CODEX_COST_PER_GLYPH = SERVER_BUILDER.comment("Cost per glyph in a codex").defineInRange("codexCost", 10, 0, Integer.MAX_VALUE);
        SPAWN_TOMES = SERVER_BUILDER.comment("Spawn Caster Tomes in Dungeon Loot?").define("spawnTomes", true);

        SERVER_BUILDER.pop();
        SERVER_BUILDER.push("lights");
        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
        RegistryHelper.generateConfig(ArsNouveau.MODID, new ArrayList<>(ArsNouveauAPI.getInstance().getSpellpartMap().values()));
    }

    public static boolean isStarterEnabled(AbstractSpellPart e){
        return e.STARTER_SPELL != null && e.STARTER_SPELL.get();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        if(configEvent.getConfig().getSpec() == CLIENT_CONFIG){
            resetLightMaps();
        }
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
        if(configEvent.getConfig().getSpec() == CLIENT_CONFIG){
           resetLightMaps();
        }
    }

    public static void resetLightMaps(){
        ENTITY_LIGHT_MAP = new HashMap<>();
        ITEM_LIGHTMAP = new HashMap<>();
        // Copy values from ENTITY_LIGHT_CONFIG to ENTITY_LIGHT_MAP
        for(Map.Entry<String, Integer> entry : ConfigUtil.parseMapConfig(ENTITY_LIGHT_CONFIG).entrySet()){
            ENTITY_LIGHT_MAP.put(new ResourceLocation(entry.getKey()), entry.getValue());
        }
        // Copy values from ITEM_LIGHT_CONFIG to ITEM_LIGHT_MAP
        for(Map.Entry<String, Integer> entry : ConfigUtil.parseMapConfig(ITEM_LIGHT_CONFIG).entrySet()){
            ITEM_LIGHTMAP.put(new ResourceLocation(entry.getKey()), entry.getValue());
        }
    }


    public static Map<String, Integer> getDefaultEntityLight(){
        Map<String, Integer> map = new HashMap<>();
        map.put(an(LibEntityNames.SPELL_PROJ), 15);
        map.put(an(LibEntityNames.ORBIT_PROJECTILE), 15);
        map.put(an(LibEntityNames.LINGER), 15);
        map.put(an(LibEntityNames.FLYING_ITEM), 10);
        map.put(an(LibEntityNames.FOLLOW_PROJ), 10);
        map.put("minecraft:blaze", 15);

        return map;
    }

    public static Map<String, Integer> getDefaultItemLight(){
        Map<String, Integer> map = new HashMap<>();
        map.put("minecraft:glowstone", 15);
        return map;
    }

    public static String an(String s){
        return new ResourceLocation(ArsNouveau.MODID, s).toString();
    }
}
