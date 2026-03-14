package com.hollingsworth.arsnouveau.setup.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class StartupConfig {

    public static ModConfigSpec STARTUP_CONFIG;
    public static ModConfigSpec.IntValue MANA_REGEN_POTION;
    public static ModConfigSpec.BooleanValue SHADY_VILLAGER_ENABLED;


    static {
        ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
        SERVER_BUILDER.comment("Mana").push("mana");
        MANA_REGEN_POTION = SERVER_BUILDER.comment("Regen bonus per potion level").defineInRange("potionRegen", 10, 0, Integer.MAX_VALUE);
        SHADY_VILLAGER_ENABLED = SERVER_BUILDER.comment("If shady villagers can be converted by placing an Arcane Core near a villager. Does not remove or disable existing villagers.").define("shadyVillagerEnabled", true);
        SERVER_BUILDER.pop();

        STARTUP_CONFIG = SERVER_BUILDER.build();
    }
}
