package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ArsNouveau.MODID)
public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static final String CATEGORY_SPELLS = "spells";
    public static final String DRYGMY_CATEGORY = "drygmy_production";

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.BooleanValue SPAWN_BOOK;
    public static Integer TREE_SPAWN_RATE = 100;


    public static ForgeConfigSpec.IntValue DRYGMY_MANA_COST;
    public static ForgeConfigSpec.IntValue SYLPH_MANA_COST;
    public static ForgeConfigSpec.IntValue WHIRLISPRIG_MAX_PROGRESS;
    public static ForgeConfigSpec.IntValue DRYGMY_MAX_PROGRESS;
    public static ForgeConfigSpec.IntValue DRYGMY_BASE_ITEM;
    public static ForgeConfigSpec.IntValue DRYGMY_UNIQUE_BONUS;
    public static ForgeConfigSpec.IntValue DRYGMY_QUANTITY_CAP;

    public static ForgeConfigSpec.IntValue MELDER_OUTPUT;
    public static ForgeConfigSpec.IntValue MELDER_INPUT_COST;
    public static ForgeConfigSpec.IntValue MELDER_SOURCE_COST;
    public static ForgeConfigSpec.BooleanValue HUNTER_ATTACK_ANIMALS;
    public static ForgeConfigSpec.BooleanValue STALKER_ATTACK_ANIMALS;
    public static ForgeConfigSpec.BooleanValue GUARDIAN_ATTACK_ANIMALS;
    public static ForgeConfigSpec.BooleanValue CHIMERA_DIVE_DESTRUCTIVE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;

    public static ForgeConfigSpec.IntValue ARCHWOOD_FOREST_WEIGHT;

    public static ForgeConfigSpec.BooleanValue SHOW_SUPPORTER_MESSAGE;

    public static ForgeConfigSpec.BooleanValue SPAWN_TOMES;
    public static ForgeConfigSpec.BooleanValue ALTERNATE_PORTAL_RENDER;

    public static ForgeConfigSpec.BooleanValue DISABLE_SKY_SHADER;
    public static ForgeConfigSpec.BooleanValue SHOW_RECIPE_BOOK;
    public static ForgeConfigSpec.IntValue MAX_LOG_EVENTS;
    public static ForgeConfigSpec.IntValue TOOLTIP_X_OFFSET;
    public static ForgeConfigSpec.IntValue TOOLTIP_Y_OFFSET;
    public static ForgeConfigSpec.IntValue MANABAR_X_OFFSET;
    public static ForgeConfigSpec.IntValue MANABAR_Y_OFFSET;
    public static ForgeConfigSpec.IntValue BOOKWYRM_LIMIT;


    public static boolean isGlyphEnabled(ResourceLocation tag) {
        AbstractSpellPart spellPart = ArsNouveauAPI.getInstance().getSpellpartMap().get(tag);
        if (spellPart == null) {
            throw new IllegalArgumentException("Spell Part with id " + tag + " does not exist in registry. Did you pass the right ID?");
        }

        return spellPart.isEnabled();
    }

    public static boolean isGlyphEnabled(AbstractSpellPart tag) {
        return isGlyphEnabled(tag.getRegistryName());
    }

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        CLIENT_BUILDER.comment("Lighting").push("lights");
        SHOW_SUPPORTER_MESSAGE = CLIENT_BUILDER.comment("Show the supporter message. This is set to false after the first time.").define("showSupporterMessage", true);
        CLIENT_BUILDER.pop();
        CLIENT_BUILDER.comment("Overlay").push("overlays");
        TOOLTIP_X_OFFSET = CLIENT_BUILDER.comment("X offset for the tooltip").defineInRange("xTooltip", 20, Integer.MIN_VALUE, Integer.MAX_VALUE);
        TOOLTIP_Y_OFFSET = CLIENT_BUILDER.comment("Y offset for the tooltip").defineInRange("yTooltip", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        MANABAR_X_OFFSET = CLIENT_BUILDER.comment("X offset for the Mana Bar").defineInRange("xManaBar", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        MANABAR_Y_OFFSET = CLIENT_BUILDER.comment("Y offset for the Mana Bar").defineInRange("yManaBar", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        SHOW_RECIPE_BOOK = CLIENT_BUILDER.comment("If the Storage Lectern should show the recipe book icon").define("showRecipeBook", true);
        CLIENT_BUILDER.pop();
        CLIENT_BUILDER.comment("Misc").push("misc");
        ALTERNATE_PORTAL_RENDER = CLIENT_BUILDER.comment("Use simplified renderer for Warp Portals").define("no_end_portal_render", false);
        DISABLE_SKY_SHADER = CLIENT_BUILDER.comment("Disables the skyweave renderer. Disable if your sky is broken with shaders.").define("disable_skyweave", false);
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
        SERVER_BUILDER.pop();
        SERVER_BUILDER.comment("Blocks").push("block");
        MELDER_INPUT_COST = SERVER_BUILDER.comment("How much potion a melder takes from each input jar. 100 = 1 potion").defineInRange("melderInputCost", 200, 100, Integer.MAX_VALUE);
        MELDER_OUTPUT = SERVER_BUILDER.comment("How much potion a melder outputs per cycle. 100 = 1 potion").defineInRange("melderOutput", 100, 100, Integer.MAX_VALUE);
        MELDER_SOURCE_COST = SERVER_BUILDER.comment("How much source a melder takes per cycle").defineInRange("melderSourceCost", 300, 0, Integer.MAX_VALUE);
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

    public static String an(String s){
        return new ResourceLocation(ArsNouveau.MODID, s).toString();
    }
}
