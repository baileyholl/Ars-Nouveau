package com.hollingsworth.arsnouveau.common.entity.goal.sylph;

import com.hollingsworth.arsnouveau.common.entity.EntitySylph;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class EvaluateGroveGoal extends Goal {

    private final EntitySylph sylph;
    private final int ticksToNextEval;

    public EvaluateGroveGoal(EntitySylph sylph, int tickFreq){
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

        if(state == Blocks.WATER.defaultBlockState() || state == Blocks.GRASS_BLOCK.defaultBlockState() || state == Blocks.PODZOL.defaultBlockState() || state == Blocks.GRASS_PATH.defaultBlockState())
            return 1;

        if(state.getBlock() instanceof BushBlock)
            return 2;


        if(state.getBlock() instanceof StemGrownBlock)
            return 2;

        if(state.getBlock().is(BlockTags.LOGS))
            return 2;

        if(state.getBlock().is(BlockTags.LEAVES) || state.getBlock() instanceof LeavesBlock)
            return 1;

        if(state.getMaterial() == Material.PLANT || state.getMaterial() == Material.REPLACEABLE_PLANT)
            return 1;

        if(state.getBlock() instanceof IGrowable)
            return 1;

        return 0;
    }

    @Override
    public void start() {
        World world = sylph.getCommandSenderWorld();
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
            if(!state.hasTileEntity())
                dropMap.put(state, dropMap.get(state) + 1);
            defaultMap.put(defaultState, defaultMap.get(defaultState) + 1);
            score += defaultMap.get(defaultState) <= 50 ? getScore(defaultState) : 0;
        }
        sylph.getEntityData().set(EntitySylph.MOOD_SCORE, score);
        sylph.timeUntilEvaluation = ticksToNextEval;
        sylph.genTable = dropMap;
        sylph.scoreMap = defaultMap;
        sylph.diversityScore = defaultMap.keySet().size();
    }
}
