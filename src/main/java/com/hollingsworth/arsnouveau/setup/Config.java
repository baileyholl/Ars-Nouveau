package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static final String CATEGORY_SPELLS = "spells";

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.BooleanValue SPAWN_ORE;
    public static ForgeConfigSpec.BooleanValue SPAWN_BOOK;
    public static ForgeConfigSpec.IntValue CARBUNCLE_WEIGHT;
    public static ForgeConfigSpec.IntValue SYLPH_WEIGHT;

    public static Map<String, ForgeConfigSpec.BooleanValue> enabledSpells = new HashMap<>();
    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        SPAWN_ORE = SERVER_BUILDER.comment("Spawn Arcane Ore in the world").define("genOre", true);
        SPAWN_BOOK = SERVER_BUILDER.comment("Spawn a book in the players inventory on login").define("spawnBook", true);
        CARBUNCLE_WEIGHT = SERVER_BUILDER.comment("How often Carbuncles spawn").defineInRange("carbuncleWeight",5,0,100);
        SYLPH_WEIGHT = SERVER_BUILDER.comment("How often Sylphs spawn").defineInRange("sylphWeight",5,0,100);
        SERVER_BUILDER.pop();
        SERVER_BUILDER.comment("Spells").push(CATEGORY_SPELLS);
        for(AbstractSpellPart spellPart : ArsNouveauAPI.getInstance().getSpell_map().values()){
            enabledSpells.put(spellPart.tag, SERVER_BUILDER.comment(spellPart.name + " enabled?").define(spellPart.tag, true));
        }
        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }


    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
    }
}
