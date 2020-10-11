package com.hollingsworth.arsnouveau.common.entity.goal;

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
    public boolean shouldExecute() {
        return sylph.crystalPos != null && (sylph.timeUntilEvaluation <= 0 || sylph.genTable == null);
    }

    public static int getScore(BlockState state){
        if(state.getMaterial() == Material.AIR)
            return 0;

        if(state == Blocks.WATER.getDefaultState() || state == Blocks.GRASS.getDefaultState() || state == Blocks.PODZOL.getDefaultState() || state == Blocks.GRASS_PATH.getDefaultState())
            return 1;

        if(state.getBlock() instanceof BushBlock)
            return 2;

        if(state.getBlock().isIn(BlockTags.LOGS))
            return 2;

        if(state.getBlock().isIn(BlockTags.LEAVES) || state.getBlock() instanceof LeavesBlock)
            return 1;
        if(state.getMaterial() == Material.PLANTS || state.getMaterial() == Material.TALL_PLANTS)
            return 1;
        return 0;
    }

    @Override
    public void startExecuting() {
        World world = sylph.getEntityWorld();
        Map<Block, Integer> map = new HashMap<>();
        int score = 0;
        for(BlockPos b : BlockPos.getAllInBoxMutable(sylph.crystalPos.north(10).west(10).down(1),sylph.crystalPos.south(10).east(10).up(30))){
            if(b.getY() >= 256)
                continue;
            BlockState state = world.getBlockState(b);
            int points = getScore(state);
            if(points == 0)
                continue;
            if(!map.containsKey(state.getBlock()))
                map.put(state.getBlock(), 0);
            map.put(state.getBlock(), map.get(state.getBlock()) + 1);
            score += map.get(state.getBlock()) <= 50 ? getScore(state) : 0;
        }
        sylph.getDataManager().set(EntitySylph.MOOD_SCORE, score);
        sylph.timeUntilEvaluation = ticksToNextEval;
        sylph.genTable = map;
        sylph.diversityScore = map.keySet().size();
    }
}
