package com.hollingsworth.arsnouveau.common.entity.goal.sylph;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.HashMap;
import java.util.Map;

public class EvaluateGroveGoal extends Goal {

    private final Whirlisprig sylph;
    private final int ticksToNextEval;
    public static Tag.Named<Block> KINDA_LIKES =  BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "sylph/kinda_likes"));
    public static Tag.Named<Block> GREATLY_LIKES =  BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "sylph/greatly_likes"));

    public EvaluateGroveGoal(Whirlisprig sylph, int tickFreq){
        this.sylph = sylph;
        this.ticksToNextEval = tickFreq;
    }

    @Override
    public boolean canUse() {
        return sylph.crystalPos != null && (sylph.timeUntilEvaluation <= 0 || sylph.genTable == null);
    }

    public static int getScore(BlockState state){

        if(state.getMaterial() == Material.AIR)
            return 0;

        if(state == Blocks.WATER.defaultBlockState() || state == Blocks.GRASS_BLOCK.defaultBlockState() || state == Blocks.PODZOL.defaultBlockState() || state == Blocks.DIRT_PATH.defaultBlockState())
            return 1;

        if(state.getBlock() instanceof BushBlock)
            return 2;


        if(state.getBlock() instanceof StemGrownBlock)
            return 2;

        if(state.is(BlockTags.LOGS))
            return 2;

        if(state.is(BlockTags.LEAVES) || state.getBlock() instanceof LeavesBlock)
            return 1;

        if(state.getMaterial() == Material.PLANT || state.getMaterial() == Material.REPLACEABLE_PLANT)
            return 1;

        if(state.getBlock() instanceof BonemealableBlock)
            return 1;
        
        if(state.is(KINDA_LIKES))
            return 1;
        if(state.is(GREATLY_LIKES))
            return 2;
        return 0;
    }

    @Override
    public void start() {
        Level world = sylph.getCommandSenderWorld();
        Map<BlockState, Integer> defaultMap = new HashMap<>();
        Map<BlockState, Integer> dropMap = new HashMap<>();
        int score = 0;
        for(BlockPos b : BlockPos.betweenClosed(sylph.crystalPos.north(10).west(10).below(1),sylph.crystalPos.south(10).east(10).above(30))){
            if(b.getY() >= 256)
                continue;
            BlockState state = world.getBlockState(b);
            BlockState defaultState = state.getBlock().defaultBlockState();
            int points = getScore(defaultState);
            if(points == 0)
                continue;
            if(!defaultMap.containsKey(defaultState)) {
                defaultMap.put(defaultState, 0);
            }
            if(!dropMap.containsKey(state)){
                dropMap.put(state, 0);
            }
            if(!state.hasBlockEntity())
                dropMap.put(state, dropMap.get(state) + 1);
            defaultMap.put(defaultState, defaultMap.get(defaultState) + 1);
            score += defaultMap.get(defaultState) <= 50 ? getScore(defaultState) : 0;
        }
        sylph.getEntityData().set(Whirlisprig.MOOD_SCORE, score);
        sylph.timeUntilEvaluation = ticksToNextEval;
        sylph.genTable = dropMap;
        sylph.scoreMap = defaultMap;
        sylph.diversityScore = defaultMap.keySet().size();
    }
}
