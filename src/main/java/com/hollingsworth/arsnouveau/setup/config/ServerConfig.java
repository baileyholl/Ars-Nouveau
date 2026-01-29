package com.hollingsworth.arsnouveau.setup.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {

    public static ModConfigSpec SERVER_CONFIG;
    public static ModConfigSpec.IntValue INIT_MAX_MANA;
    public static ModConfigSpec.IntValue INIT_MANA_REGEN;
    public static ModConfigSpec.IntValue GLYPH_MAX_BONUS;
    public static ModConfigSpec.IntValue TIER_MAX_BONUS;
    public static ModConfigSpec.IntValue TIER_REGEN_BONUS;
    public static ModConfigSpec.IntValue MANA_BOOST_BONUS;
    public static ModConfigSpec.IntValue MANA_REGEN_ENCHANT_BONUS;
    public static ModConfigSpec.IntValue REGEN_INTERVAL;
    public static ModConfigSpec.DoubleValue GLYPH_REGEN_BONUS;
    public static ModConfigSpec.BooleanValue ENFORCE_AUGMENT_CAP_ON_CAST;
    public static ModConfigSpec.BooleanValue ENFORCE_GLYPH_LIMIT_ON_CAST;
    public static ModConfigSpec.IntValue CODEX_COST_PER_GLYPH;
    public static ModConfigSpec.BooleanValue ENABLE_WARP_PORTALS;
    public static ModConfigSpec.IntValue SUMMON_COUNT_LIMIT_BASE;
    public static ModConfigSpec.IntValue SUMMON_COUNT_LIMIT_MAX;

    public static ModConfigSpec.BooleanValue INFINITE_SPELLS;
    public static ModConfigSpec.IntValue INF_SPELLS_LENGHT_MODIFIER;
    public static ModConfigSpec.IntValue LECTERN_LINK_RANGE;
    public static ModConfigSpec.IntValue DECOR_BLOSSOM_RANGE;

    static {
        ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
        SERVER_BUILDER.comment("Blocks").push("blocks");
        LECTERN_LINK_RANGE = SERVER_BUILDER.comment("Maximum storage lectern linking range").defineInRange("lecternLinkRange", 30, 1, Integer.MAX_VALUE);
        DECOR_BLOSSOM_RANGE = SERVER_BUILDER.comment("Maximum range of the decor blossom").defineInRange("decorBlossomRange", 30, 1, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();

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

        SERVER_BUILDER.pop().push("summon_configs");
        SUMMON_COUNT_LIMIT_BASE = SERVER_BUILDER.comment("Base max number of summons a player can have active at once, bonus effect may increase this limit. Set to -1 for unlimited, at your server own risk.").defineInRange("summonCountLimit", 20, -1, 1000);
        SUMMON_COUNT_LIMIT_MAX = SERVER_BUILDER.comment("Maximum number of summons a player can have active at once, including bonus. Set to -1 for unlimited, at your server own risk.").defineInRange("summonCountLimit", 100, -1, 1000);

        SERVER_BUILDER.pop().comment("Infinite Spells Mode").push("spell_length");
        INFINITE_SPELLS = SERVER_BUILDER.comment("If Enabled, the value below will be added to the base glyph limit for spellbooks.").define("infiniteSpells", false);
        INF_SPELLS_LENGHT_MODIFIER = SERVER_BUILDER.comment("Only used if infinite spells is true, increases or decreases the spell length limit in spellbooks..").defineInRange("infiniteSpellLimit", 30, -9, 1000);
        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
