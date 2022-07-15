package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.world.biome.ArchwoodRegion;
import com.hollingsworth.arsnouveau.common.world.biome.ArchwoodSurfaceRuleData;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

public class Terrablender {

    public static void registerBiomes() {
        // Given we only add one biomes, we should keep our weight relatively low.
        Regions.register(new ArchwoodRegion(new ResourceLocation(ArsNouveau.MODID, "overworld"), 2));

        // Register our surface rules
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, ArsNouveau.MODID, ArchwoodSurfaceRuleData.makeRules());
    }

}
