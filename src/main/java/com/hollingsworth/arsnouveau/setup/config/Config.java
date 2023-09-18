package com.hollingsworth.arsnouveau.setup.config;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = ArsNouveau.MODID)
public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static final String CATEGORY_SPELLS = "spells";
    public static final String DRYGMY_CATEGORY = "drygmy_production";

    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    public static ModConfigSpec.BooleanValue SPAWN_BOOK;
    public static ModConfigSpec.BooleanValue INFORM_LIGHTS;
    public static Integer TREE_SPAWN_RATE = 100;


    public static ModConfigSpec.IntValue DRYGMY_MANA_COST;
    public static ModConfigSpec.IntValue SYLPH_MANA_COST;
    public static ModConfigSpec.IntValue WHIRLISPRIG_MAX_PROGRESS;
    public static ModConfigSpec.IntValue DRYGMY_MAX_PROGRESS;
    public static ModConfigSpec.IntValue DRYGMY_BASE_ITEM;
    public static ModConfigSpec.IntValue DRYGMY_UNIQUE_BONUS;
    public static ModConfigSpec.IntValue DRYGMY_QUANTITY_CAP;
    public static ModConfigSpec.IntValue JUMP_RING_COST;

    public static ModConfigSpec.IntValue MELDER_OUTPUT;
    public static ModConfigSpec.IntValue MELDER_INPUT_COST;
    public static ModConfigSpec.IntValue MELDER_SOURCE_COST;
    public static ModConfigSpec.IntValue ENCHANTED_FLASK_CAP;
    public static ModConfigSpec.BooleanValue HUNTER_ATTACK_ANIMALS;
    public static ModConfigSpec.BooleanValue STALKER_ATTACK_ANIMALS;
    public static ModConfigSpec.BooleanValue GUARDIAN_ATTACK_ANIMALS;
    public static ModConfigSpec.BooleanValue CHIMERA_DIVE_DESTRUCTIVE;
    public static ModConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;

    public static ModConfigSpec.IntValue ARCHWOOD_FOREST_WEIGHT;

    public static ModConfigSpec.BooleanValue DYNAMIC_LIGHTS_ENABLED;
    public static ModConfigSpec.BooleanValue SHOW_SUPPORTER_MESSAGE;
    public static ModConfigSpec.IntValue TOUCH_LIGHT_LUMINANCE;
    public static ModConfigSpec.IntValue TOUCH_LIGHT_DURATION;

    public static ModConfigSpec.BooleanValue SPAWN_TOMES;
    public static ModConfigSpec.BooleanValue ALTERNATE_PORTAL_RENDER;

    public static ModConfigSpec.BooleanValue DISABLE_SKY_SHADER;
    public static ModConfigSpec.BooleanValue DISABLE_TRANSLUCENT_PARTICLES;
    public static ModConfigSpec.BooleanValue SHOW_RECIPE_BOOK;
    public static ModConfigSpec.IntValue MAX_LOG_EVENTS;
    public static ModConfigSpec.IntValue TOOLTIP_X_OFFSET;
    public static ModConfigSpec.IntValue TOOLTIP_Y_OFFSET;
    public static ModConfigSpec.IntValue MANABAR_X_OFFSET;
    public static ModConfigSpec.IntValue MANABAR_Y_OFFSET;
    public static ModConfigSpec.BooleanValue TOGGLE_RADIAL_HUD;
    public static ModConfigSpec.IntValue BOOKWYRM_LIMIT;
    public static ModConfigSpec.BooleanValue GUI_TRANSPARENCY;
    public static ModConfigSpec.BooleanValue GLYPH_TOOLTIPS;

    private static ModConfigSpec.ConfigValue<List<? extends String>> ENTITY_LIGHT_CONFIG;
    private static ModConfigSpec.ConfigValue<List<? extends String>> ITEM_LIGHT_CONFIG;

    public static Map<ResourceLocation, Integer> ENTITY_LIGHT_MAP = new HashMap<>();
    public static Map<ResourceLocation, Integer> ITEM_LIGHTMAP = new HashMap<>();


    public static boolean isGlyphEnabled(ResourceLocation tag) {
        AbstractSpellPart spellPart = GlyphRegistry.getSpellpartMap().get(tag);
        if (spellPart == null) {
            throw new IllegalArgumentException("Spell Part with id " + tag + " does not exist in registry. Did you pass the right ID?");
        }

        return spellPart.isEnabled();
    }

    public static boolean isGlyphEnabled(AbstractSpellPart tag) {
        return isGlyphEnabled(tag.getRegistryName());
    }

    static {
        ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

        CLIENT_BUILDER.comment("Lighting").push("lights");
        SHOW_SUPPORTER_MESSAGE = CLIENT_BUILDER.comment("Show the supporter message. This is set to false after the first time.").define("showSupporterMessage", true);
        DYNAMIC_LIGHTS_ENABLED = CLIENT_BUILDER.comment("If dynamic lights are enabled").define("lightsEnabled", false);
        TOUCH_LIGHT_LUMINANCE = CLIENT_BUILDER.comment("How bright the touch light is").defineInRange("touchLightLuminance", 8, 0, 15);
        TOUCH_LIGHT_DURATION = CLIENT_BUILDER.comment("How long the touch light lasts in ticks").defineInRange("touchLightDuration", 8, 0, 40);
        ENTITY_LIGHT_CONFIG = CLIENT_BUILDER.comment("Light level an entity should emit when dynamic lights are on", "Example entry: minecraft:blaze=15")
                .defineList("entity_lights", ConfigUtil.writeConfig(getDefaultEntityLight()), ConfigUtil::validateMap);
        ITEM_LIGHT_CONFIG = CLIENT_BUILDER.comment("Light level an item should emit when held when dynamic lights are on", "Example entry: minecraft:stick=15")
                .defineList("item_lights", ConfigUtil.writeConfig(getDefaultItemLight()), ConfigUtil::validateMap);
        CLIENT_BUILDER.pop();
        CLIENT_BUILDER.comment("Overlay").push("overlays");
        TOOLTIP_X_OFFSET = CLIENT_BUILDER.comment("X offset for the tooltip").defineInRange("xTooltip", 20, Integer.MIN_VALUE, Integer.MAX_VALUE);
        TOOLTIP_Y_OFFSET = CLIENT_BUILDER.comment("Y offset for the tooltip").defineInRange("yTooltip", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        MANABAR_X_OFFSET = CLIENT_BUILDER.comment("X offset for the Mana Bar").defineInRange("xManaBar", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        MANABAR_Y_OFFSET = CLIENT_BUILDER.comment("Y offset for the Mana Bar").defineInRange("yManaBar", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        SHOW_RECIPE_BOOK = CLIENT_BUILDER.comment("If the Storage Lectern should show the recipe book icon").define("showRecipeBook", true);
<<<<<<< HEAD:src/main/java/com/hollingsworth/arsnouveau/setup/config/Config.java
        INFORM_LIGHTS = CLIENT_BUILDER.comment("Inform the player of Dynamic lights once.").define("informLights", true);
=======
        TOGGLE_RADIAL_HUD = CLIENT_BUILDER.comment("Whether the Selection HUD is toggled or held").define("toggleSelectionHUD", true);
>>>>>>> f64755dd3 (feat: hold config option for radial selection):src/main/java/com/hollingsworth/arsnouveau/setup/Config.java
        CLIENT_BUILDER.pop();
        CLIENT_BUILDER.comment("Misc").push("misc");
        ALTERNATE_PORTAL_RENDER = CLIENT_BUILDER.comment("Use simplified renderer for Warp Portals").define("no_end_portal_render", false);
        DISABLE_SKY_SHADER = CLIENT_BUILDER.comment("Disables the skyweave renderer. Disable if your sky is broken with shaders.").define("disable_skyweave", false);
        GLYPH_TOOLTIPS = CLIENT_BUILDER.comment("Show spell tooltips with glyphs instead of plain text").define("glyphTooltips", true);
        GUI_TRANSPARENCY = CLIENT_BUILDER.comment("Enables transparent/opaque rendering of elements in the book GUI. Disable if it leads to crash with Sodium derivatives").define("gui_transparency", true);
        DISABLE_TRANSLUCENT_PARTICLES = CLIENT_BUILDER.comment("Disables translucent particles. Disable if your particles are invisible with shaders.").define("opaque_particles", false);
        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        DIMENSION_BLACKLIST = SERVER_BUILDER.comment("Dimensions where hostile mobs will not spawn. Ex: [\"minecraft:overworld\", \"undergarden:undergarden\"]. . Run /forge dimensions for a list.").defineList("dimensionBlacklist", new ArrayList<>(), (o) -> true);
        SPAWN_BOOK = SERVER_BUILDER.comment("Spawn a book in the players inventory on login").define("spawnBook", true);
        SYLPH_MANA_COST = SERVER_BUILDER.comment("How much mana whirlisprigs consume per generation").defineInRange("sylphManaCost", 250, 0, 10000);
        WHIRLISPRIG_MAX_PROGRESS = SERVER_BUILDER.comment("How much progress whirlisprigs must accumulate before creating resources")
                .defineInRange("whirlisprigProgress", 250, 0, 10000);
        HUNTER_ATTACK_ANIMALS = SERVER_BUILDER.comment("Should the Wilden Hunter attack animals?").define("hunterHuntsAnimals", false);
        STALKER_ATTACK_ANIMALS = SERVER_BUILDER.comment("Should the Wilden Stalker attack animals?").define("stalkerHuntsAnimals", false);
        GUARDIAN_ATTACK_ANIMALS = SERVER_BUILDER.comment("Should the Wilden Defender attack animals?").define("defenderHuntsAnimals", false);
        CHIMERA_DIVE_DESTRUCTIVE = SERVER_BUILDER.comment("Should the Wilden Chimera dive bomb destroy blocks?").define("destructiveDiveBomb", true);

        ARCHWOOD_FOREST_WEIGHT = SERVER_BUILDER.comment("Archwood forest spawn weight").defineInRange("archwoodForest", 2, 0, Integer.MAX_VALUE);
        BOOKWYRM_LIMIT = SERVER_BUILDER.comment("How many inventories can lectern support per bookwyrm").defineInRange("bookwyrmLimit", 8, 1, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();
        SERVER_BUILDER.push(DRYGMY_CATEGORY);
        DRYGMY_MANA_COST = SERVER_BUILDER.comment("How much source drygmys consume per generation").defineInRange("drygmyManaCost", 1000, 0, 10000);
        DRYGMY_MAX_PROGRESS = SERVER_BUILDER.comment("How many channels must occur before a drygmy produces loot").defineInRange("drygmyMaxProgress", 20, 0, 300);
        DRYGMY_UNIQUE_BONUS = SERVER_BUILDER.comment("Bonus number of items a drygmy produces per unique mob").defineInRange("drygmyUniqueBonus", 2, 0, 300);
        DRYGMY_BASE_ITEM = SERVER_BUILDER.comment("Base number of items a drygmy produces per cycle before bonuses.").defineInRange("drygmyBaseItems", 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        DRYGMY_QUANTITY_CAP = SERVER_BUILDER.comment("Max Bonus number of items a drygmy produces from nearby entities. Each entity equals 1 item.").defineInRange("drygmyQuantityCap", 5, 0, 300);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Items").push("item");
        SPAWN_TOMES = SERVER_BUILDER.comment("Spawn Caster Tomes in Dungeon Loot?").define("spawnTomes", true);
        JUMP_RING_COST = SERVER_BUILDER.comment("How much mana the Ring of Jumping consumes per jump").defineInRange("jumpRingCost", 30, 0, 10000);
        SERVER_BUILDER.pop();
        SERVER_BUILDER.comment("Blocks").push("block");
        MELDER_INPUT_COST = SERVER_BUILDER.comment("How much potion a melder takes from each input jar. 100 = 1 potion").defineInRange("melderInputCost", 200, 100, Integer.MAX_VALUE);
        MELDER_OUTPUT = SERVER_BUILDER.comment("How much potion a melder outputs per cycle. 100 = 1 potion").defineInRange("melderOutput", 100, 100, Integer.MAX_VALUE);
        MELDER_SOURCE_COST = SERVER_BUILDER.comment("How much source a melder takes per cycle").defineInRange("melderSourceCost", 300, 0, Integer.MAX_VALUE);
        ENCHANTED_FLASK_CAP = SERVER_BUILDER.comment("The max potion level the enchanted flask can grant. This isnt needed unless you have an infinite potion leveling exploit.").defineInRange("enchantedFlaskCap", 255, 2, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Debug").push("debug");
        MAX_LOG_EVENTS = SERVER_BUILDER.comment("Max number of log events to keep on entities. Lowering this number may make it difficult to debug why your entities are stuck.").defineInRange("maxLogEvents", 100, 0, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();

        COMMON_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static boolean isStarterEnabled(AbstractSpellPart e) {
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
            ENTITY_LIGHT_MAP.put(ResourceLocation.tryParse(entry.getKey()), entry.getValue());
        }
        // Copy values from ITEM_LIGHT_CONFIG to ITEM_LIGHT_MAP
        for(Map.Entry<String, Integer> entry : ConfigUtil.parseMapConfig(ITEM_LIGHT_CONFIG).entrySet()){
            ITEM_LIGHTMAP.put(ResourceLocation.tryParse(entry.getKey()), entry.getValue());
        }
    }


    public static Map<String, Integer> getDefaultEntityLight(){
        Map<String, Integer> map = new HashMap<>();
        map.put(an(LibEntityNames.SPELL_PROJ), 15);
        map.put(an(LibEntityNames.ORBIT_PROJECTILE), 15);
        map.put(an(LibEntityNames.LINGER), 15);
        map.put(an(LibEntityNames.FLYING_ITEM), 10);
        map.put(an(LibEntityNames.FOLLOW_PROJ), 10);
        map.put("minecraft:blaze", 10);
        map.put("minecraft:spectral_arrow", 8);
        map.put("minecraft:magma_cube", 8);
        return map;
    }

    public static Map<String, Integer> getDefaultItemLight(){
        Map<String, Integer> map = new HashMap<>();
        map.put("minecraft:glowstone", 15);
        map.put("minecraft:torch", 14);
        map.put("minecraft:glowstone_dust", 8);
        map.put("minecraft:redstone_torch", 10);
        map.put("minecraft:soul_torch", 10);
        map.put("minecraft:blaze_rod", 10);
        map.put("minecraft:glow_berries", 8);
        map.put("minecraft:lava_bucket", 15);
        map.put("minecraft:lantern", 14);
        map.put("minecraft:soul_lantern", 12);
        map.put("minecraft:shroomlight", 10);
        map.put("minecraft:glow_ink_sac", 10);
        map.put("minecraft:nether_star", 14);
        map.put("minecraft:ochre_froglight", 15);
        map.put("minecraft:pearlescent_froglight", 15);
        map.put("minecraft:verdant_froglight", 15);
        return map;
    }

    public static String an(String s){
        return ArsNouveau.prefix( s).toString();
    }
}
