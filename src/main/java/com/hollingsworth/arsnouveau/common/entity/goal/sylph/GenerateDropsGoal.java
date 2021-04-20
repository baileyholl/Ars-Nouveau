package com.hollingsworth.arsnouveau.common.entity.goal.sylph;

import com.hollingsworth.arsnouveau.api.util.DropDistribution;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntitySylph;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.common.entity.goal.sylph.EvaluateGroveGoal.getScore;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class GenerateDropsGoal extends Goal {
    EntitySylph sylph;
    public List<BlockPos> locList;
    int timeGathering;
    public GenerateDropsGoal(EntitySylph sylph){
        this.sylph = sylph;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public void stop() {
        timeGathering = 100;
        locList = null;
    }

    public int getDropsByDiversity(){
        return  sylph.diversityScore / 2;
    }

    public int getTimerByMood(){
        int mood = sylph.getEntityData().get(EntitySylph.MOOD_SCORE);
        if(mood >= 1000)
            return 20;

        if(mood >= 750)
            return 40;

        if(mood >= 500)
            return 60;

        if(mood >= 250)
            return 80;

        return 100;
    }

    @Override
    public void tick() {
        World world = sylph.getCommandSenderWorld();
        timeGathering--;

        if(locList == null || timeGathering <= 0)
            return;

        for(BlockPos growPos : locList) {
            ((ServerWorld) world).sendParticles(ParticleTypes.COMPOSTER, growPos.getX() + 0.5, growPos.getY() + 0.5, growPos.getZ() + 0.5, 1, ParticleUtil.inRange(-0.2, 0.2), 0, ParticleUtil.inRange(-0.2, 0.2), 0.01);
        }
        timeGathering--;

        if(timeGathering == 0 && sylph.removeManaForDrops()){
            sylph.timeUntilGather = getTimerByMood() * 20;
            DropDistribution<BlockState> blockDropDistribution = new DropDistribution<>(sylph.genTable);
            int numDrops = getDropsByDiversity() + 3;
            for(int i = 0; i < numDrops; i++){
                BlockState block = blockDropDistribution.nextDrop();
                if(block == null)
                    return;

                // Reroll 1 time if the drops are empty or dirt i.e. grass.
                for(ItemStack s : getDrops(blockDropDistribution)){
                    sylph.onPickup(s);
                }
            }

        }
    }
    // Keep rerolling on empty or dirt drops.
    public List<ItemStack> getDrops(DropDistribution<BlockState> blockDropDistribution){
        World world = sylph.getCommandSenderWorld();
        Supplier<List<ItemStack>> getDrops = () -> Block.getDrops(blockDropDistribution.nextDrop(), (ServerWorld) world, sylph.blockPosition(), null);

        List<ItemStack> drops = getDrops.get();
        int numRerolls = 0;
        boolean bonusReroll = false;
        while(numRerolls < (bonusReroll ? 7 : 4) && (drops.isEmpty() || drops.get(0).getItem() ==  Blocks.DIRT.asItem() || (!drops.isEmpty() && !sylph.isValidReward(drops.get(0))))){
            drops = getDrops.get();
            if(!drops.isEmpty() && !sylph.isValidReward(drops.get(0)))
                bonusReroll = true;
            numRerolls++;
        }
        return drops;
    }

    @Override
    public boolean canContinueToUse() {
        return sylph.timeUntilGather <= 0  && timeGathering >= 0 && locList != null && sylph.crystalPos != null;
    }

    @Override
    public boolean canUse() {
        return sylph.crystalPos != null  && sylph.genTable != null && sylph.timeUntilGather <= 0 && (sylph.drops == null || sylph.drops.isEmpty()) && sylph.enoughManaForTask();
    }

    @Override
    public void start() {
        World world = sylph.getCommandSenderWorld();
        if(locList == null){
            locList = new ArrayList<>();
            for(BlockPos b : BlockPos.betweenClosed(sylph.blockPosition().north(4).west(4).below(3),sylph.blockPosition().south(4).east(4).above(3))){
                if(b.getY() >= 256)
                    continue;
                BlockState state = world.getBlockState(b);
                int points = getScore(state);
                if(points == 0)
                    continue;
                locList.add(b.immutable());
            }
            Collections.shuffle(locList);
            if(locList.size() > 6)
                locList = locList.subList(0, 6);
        }
    }
}
