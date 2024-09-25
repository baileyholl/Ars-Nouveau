package com.hollingsworth.arsnouveau.setup.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class StartupConfig {

    public static ModConfigSpec STARTUP_CONFIG;
    public static ModConfigSpec.IntValue MANA_REGEN_POTION;


    static {
        ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
        SERVER_BUILDER.comment("Mana").push("mana");
        MANA_REGEN_POTION = SERVER_BUILDER.comment("Regen bonus per potion level").defineInRange("potionRegen", 10, 0, Integer.MAX_VALUE);
        SERVER_BUILDER.pop();

        STARTUP_CONFIG = SERVER_BUILDER.build();
    }
}
