package com.hollingsworth.arsnouveau.setup.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec.IntValue INIT_MAX_MANA;
    public static ForgeConfigSpec.IntValue INIT_MANA_REGEN;
    public static ForgeConfigSpec.IntValue GLYPH_MAX_BONUS;
    public static ForgeConfigSpec.IntValue TIER_MAX_BONUS;
    public static ForgeConfigSpec.IntValue TIER_REGEN_BONUS;
    public static ForgeConfigSpec.IntValue MANA_BOOST_BONUS;
    public static ForgeConfigSpec.IntValue MANA_REGEN_ENCHANT_BONUS;
    public static ForgeConfigSpec.IntValue MANA_REGEN_POTION;
    public static ForgeConfigSpec.IntValue REGEN_INTERVAL;
    public static ForgeConfigSpec.DoubleValue GLYPH_REGEN_BONUS;
    public static ForgeConfigSpec.BooleanValue ENFORCE_AUGMENT_CAP_ON_CAST;
    public static ForgeConfigSpec.BooleanValue ENFORCE_GLYPH_LIMIT_ON_CAST;
    public static ForgeConfigSpec.IntValue CODEX_COST_PER_GLYPH;
    public static ForgeConfigSpec.BooleanValue ENABLE_WARP_PORTALS;

    public static ForgeConfigSpec.BooleanValue INFINITE_SPELLS;
    public static ForgeConfigSpec.IntValue NOT_SO_INFINITE_SPELLS;


    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        SERVER_BUILDER.comment("Mana").push("mana");
        INIT_MANA_REGEN = SERVER_BUILDER.comment("Base mana regen in seconds").defineInRange("baseRegen", 5, 0, Integer.MAX_VALUE);
        INIT_MAX_MANA = SERVER_BUILDER.comment("Base max mana").defineInRange("baseMax", 100, 0, Integer.MAX_VALUE);
        REGEN_INTERVAL = SERVER_BUILDER.comment("How often max and regen will be calculated, in ticks. NOTE: Having the base mana regen AT LEAST this value is recommended.")
                .defineInRange("updateInterval", 5, 1, 20);
        GLYPH_MAX_BONUS = SERVER_BUILDER.comment("Max mana bonus per glyph").defineInRange("glyphmax", 15, 0, Integer.MAX_VALUE);
        TIER_MAX_BONUS = SERVER_BUILDER.comment("Max mana bonus for tier of book").defineInRange("tierMax", 50, 0, Integer.MAX_VALUE);
        TIER_REGEN_BONUS = SERVER_BUILDER.comment("Mana regen bonus for tier of book").defineInRange("tierRegen", 1, 0, Integer.MAX_VALUE);
        MANA_BOOST_BONUS = SERVER_BUILDER.comment("Mana Boost value per level").defineInRange("manaBoost", 25, 0, Integer.MAX_VALUE);
        MANA_REGEN_ENCHANT_BONUS = SERVER_BUILDER.comment("(enchantment) Mana regen per second per level").defineInRange("manaRegenEnchantment", 2, 0, Integer.MAX_VALUE);
        GLYPH_REGEN_BONUS = SERVER_BUILDER.comment("Regen bonus per glyph").defineInRange("glyphRegen", 0.33, 0.0, Integer.MAX_VALUE);
        MANA_REGEN_POTION = SERVER_BUILDER.comment("Regen bonus per potion level").defineInRange("potionRegen", 10, 0, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();
        SERVER_BUILDER.push("spell_casting");
        ENFORCE_AUGMENT_CAP_ON_CAST = SERVER_BUILDER.comment("Enforce augment cap on casting? Turn this off if you are a pack maker and want to create more powerful items than players.")
                .define("enforceCapOnCast", true);
        ENFORCE_GLYPH_LIMIT_ON_CAST = SERVER_BUILDER.comment("Enforce glyph per spell limit on casting? Turn this off if you are a pack maker and want to create more powerful items than players.")
                .define("enforceGlyphLimitOnCast", true);
        SERVER_BUILDER.pop().push("item");
        CODEX_COST_PER_GLYPH = SERVER_BUILDER.comment("Cost per glyph in a codex").defineInRange("codexCost", 10, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop().push("warp_portals");
        ENABLE_WARP_PORTALS = SERVER_BUILDER.comment("Enable warp portals?").define("enableWarpPortals", true);

        SERVER_BUILDER.pop().comment("Beta Features").push("beta");
        INFINITE_SPELLS = SERVER_BUILDER.comment("Allow crafting infinite spells. This is a beta feature and may cause crashes.").define("infiniteSpells", false);
        NOT_SO_INFINITE_SPELLS = SERVER_BUILDER.comment("Limits the crafting infinite spells beta, set a cap to the number of additional glyphs. This is a beta feature and may cause crashes.").defineInRange("infiniteSpellLimit", 30, 10, 1000);
        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
