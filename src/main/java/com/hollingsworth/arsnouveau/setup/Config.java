package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static final String CATEGORY_SPELLS = "spells";

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.BooleanValue SPAWN_ORE;
    public static ForgeConfigSpec.BooleanValue SPAWN_BERRIES;
    public static ForgeConfigSpec.BooleanValue SPAWN_BOOK;
    public static ForgeConfigSpec.IntValue INIT_MAX_MANA;
    public static ForgeConfigSpec.IntValue INIT_MANA_REGEN;

    public static ForgeConfigSpec.IntValue GLYPH_MAX_BONUS;
    public static ForgeConfigSpec.DoubleValue GLYPH_REGEN_BONUS;
    public static ForgeConfigSpec.DoubleValue TREE_SPAWN_RATE;


    public static ForgeConfigSpec.IntValue TIER_MAX_BONUS;
    public static ForgeConfigSpec.IntValue MANA_BOOST_BONUS;
    public static ForgeConfigSpec.IntValue MANA_REGEN_ENCHANT_BONUS;
    public static ForgeConfigSpec.IntValue MANA_REGEN_POTION;


    public static ForgeConfigSpec.IntValue REGEN_INTERVAL;
    public static ForgeConfigSpec.IntValue CARBUNCLE_WEIGHT;
    public static ForgeConfigSpec.IntValue SYLPH_WEIGHT;

    public static ForgeConfigSpec.IntValue WGUARDIAN_WEIGHT;
    public static ForgeConfigSpec.IntValue WSTALKER_WEIGHT;
    public static ForgeConfigSpec.IntValue WHUNTER_WEIGHT;
    public static ForgeConfigSpec.BooleanValue HUNTER_ATTACK_ANIMALS;
    public static ForgeConfigSpec.BooleanValue STALKER_ATTACK_ANIMALS;
    public static ForgeConfigSpec.BooleanValue GUARDIAN_ATTACK_ANIMALS;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> DIMENSION_BLACKLIST;
    private static Map<String, ForgeConfigSpec.BooleanValue> enabledSpells = new HashMap<>();
    private static Map<String, ForgeConfigSpec.BooleanValue> startingSpells = new HashMap<>();
    private static Map<String, ForgeConfigSpec.IntValue> spellCost = new HashMap<>();

    public static boolean isSpellEnabled(String tag){
        return enabledSpells.containsKey(tag) ? enabledSpells.get(tag).get() : true;
    }

    public static int getSpellCost(String tag){
        return spellCost.get(tag+"_cost").get();
    }
    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        DIMENSION_BLACKLIST = SERVER_BUILDER.comment("Dimensions where hostile mobs will not spawn. Ex: minecraft:overworld. Run /forge dimensions for a list.").defineList("dimensionBlacklist", new ArrayList<>(),(o) -> true);

        SPAWN_ORE = SERVER_BUILDER.comment("Spawn Arcane Ore in the world").define("genOre", true);
        TREE_SPAWN_RATE = SERVER_BUILDER.comment("Rate of tree spawn per chunk").defineInRange("genTrees", 0.002, 0.0d, 1.0d);
        SPAWN_BERRIES = SERVER_BUILDER.comment("Spawn Mana Berry Bushes in the world").define("genBerries", true);
        SPAWN_BOOK = SERVER_BUILDER.comment("Spawn a book in the players inventory on login").define("spawnBook", true);
        CARBUNCLE_WEIGHT = SERVER_BUILDER.comment("How often Carbuncles spawn").defineInRange("carbuncleWeight",5,0,100);
        SYLPH_WEIGHT = SERVER_BUILDER.comment("How often Sylphs spawn").defineInRange("sylphWeight",5,0,100);
        WGUARDIAN_WEIGHT = SERVER_BUILDER.comment("How often Wilden Guardians spawn").defineInRange("wguardianWeight",50,0,200);
        WSTALKER_WEIGHT = SERVER_BUILDER.comment("How often Wilden Stalkers spawn").defineInRange("wstalkerWeight",50,0,200);
        WHUNTER_WEIGHT = SERVER_BUILDER.comment("How often Wilden Hunter spawn").defineInRange("whunterWeight",50,0,200);
        HUNTER_ATTACK_ANIMALS = SERVER_BUILDER.comment("Should the Wilden Hunter attack animals?").define("hunterHuntsAnimals", true);
        STALKER_ATTACK_ANIMALS = SERVER_BUILDER.comment("Should the Wilden Stalker attack animals?").define("stalkerHuntsAnimals", false);
        GUARDIAN_ATTACK_ANIMALS = SERVER_BUILDER.comment("Should the Wilden Defender attack animals?").define("defenderHuntsAnimals", false);

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
        SERVER_BUILDER.comment("Enabled Spells").push(CATEGORY_SPELLS);
        for(AbstractSpellPart spellPart : ArsNouveauAPI.getInstance().getSpell_map().values()){
            enabledSpells.put(spellPart.tag, SERVER_BUILDER.comment(spellPart.name + " enabled?").define(spellPart.tag, true));
        }
        SERVER_BUILDER.pop();
        SERVER_BUILDER.comment("Spell Cost").push("spell_cost");
        for(AbstractSpellPart spellPart : ArsNouveauAPI.getInstance().getSpell_map().values()){
            spellCost.put(spellPart.tag + "_cost", SERVER_BUILDER.comment(spellPart.name + " cost").defineInRange(spellPart.tag+ "_cost", spellPart.getManaCost(), Integer.MIN_VALUE, Integer.MAX_VALUE));
        }
        SERVER_BUILDER.pop();
        SERVER_BUILDER.comment("Starting Spells").push("Starter Spells");
        for(AbstractSpellPart spellPart : ArsNouveauAPI.getInstance().getDefaultStartingSpells()){
            startingSpells.put(spellPart.tag + "_starter", SERVER_BUILDER.define(spellPart.tag+ "_starter", true));
        }
        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static boolean isStarterEnabled(AbstractSpellPart e){
        return startingSpells.entrySet().stream().noneMatch(entry -> entry.getValue().get() == false && entry.getKey().replace("_starter", "").equals(e.tag));
    }

//    public static List<AbstractSpellPart> getStarterSpells(){
//        return startingSpells.entrySet().stream()
//                .filter(entry -> entry.getValue().get())
//                .map(entry -> ArsNouveauAPI.getInstance().getSpell_map().get(entry.getKey().replace("_starter", "")))
//                .collect(Collectors.toList());
//    }


    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
//        startingSpells.entrySet().forEach(entry -> {
//            System.out.println(entry);
//            if(!entry.getValue().get()){
//                ArsNouveauAPI.getInstance().getDefaultStartingSpells().removeIf(a -> a.tag.equals(entry.getKey().replace("_starter", "")));
//            }
//        });
//        ArsNouveauAPI.getInstance().getDefaultStartingSpells() = ArsNouveauAPI.getInstance().getDefaultStartingSpells().stream().filter(a -> startingSpells.get(a.tag));
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
//        startingSpells.entrySet().forEach(entry -> {
//            System.out.println(entry);
//            if(!entry.getValue().get()){
//                System.out.println(entry.getKey());
//                ArsNouveauAPI.getInstance().getDefaultStartingSpells().removeIf(a -> a.tag.equals(entry.getKey().replace("_starter", "")));
//            }
//        });
    }
}
