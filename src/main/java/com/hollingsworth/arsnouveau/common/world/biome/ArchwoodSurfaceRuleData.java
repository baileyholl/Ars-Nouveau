package com.hollingsworth.arsnouveau.common.world.biome;


import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class ArchwoodSurfaceRuleData {

    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);


    public static SurfaceRules.RuleSource makeRules() {
        SurfaceRules.ConditionSource shouldBeGrass = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(20), 0);
        SurfaceRules.RuleSource grassSurface = SurfaceRules.ifTrue(shouldBeGrass, SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, GRASS_BLOCK),
                SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, DIRT)));

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.isBiome(ModBiomes.ARCHWOOD_FOREST), grassSurface),
                // Default to classic surface
                SurfaceRuleData.overworld()
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

}