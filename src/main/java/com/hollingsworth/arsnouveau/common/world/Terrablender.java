package com.hollingsworth.arsnouveau.common.world;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.world.biome.ArchwoodRegion;
import com.hollingsworth.arsnouveau.common.world.biome.ArchwoodSurfaceRuleData;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.resources.ResourceLocation;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

public class Terrablender {

    public static void registerBiomes() {
        Regions.register(new ArchwoodRegion(new ResourceLocation(ArsNouveau.MODID, "overworld"), Config.ARCHWOOD_FOREST_WEIGHT.get()));
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, ArsNouveau.MODID, ArchwoodSurfaceRuleData.makeRules());
    }

}
