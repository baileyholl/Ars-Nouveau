package com.hollingsworth.arsnouveau.common.entity.goal.amethyst_golem;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class ConvertBuddingGoal extends Goal {


    public AmethystGolem golem;
    public Supplier<Boolean> canUse;
    BlockPos targetCluster;
    int usingTicks;
    boolean isDone;
    public ConvertBuddingGoal(AmethystGolem golem, Supplier<Boolean> canUse){
        this.golem = golem;
        this.canUse = canUse;
    }

    @Override
    public boolean canContinueToUse() {
        return targetCluster != null && !isDone;
    }

    @Override
    public void tick() {
        super.tick();
        usingTicks--;
        if(golem.getNavigation().createPath(targetCluster, 2) != null){
            golem.getNavigation().moveTo(targetCluster.getX(), targetCluster.getY(), targetCluster.getZ(), 1.0f);

        }else {
            isDone = true;
            convert();
        }
        if(usingTicks <= 0){
            isDone = true;
            convert();
            return;
        }
        if(BlockUtil.distanceFrom(golem.position, targetCluster) <= 2){
            convert();
            isDone = true;
        }
    }

    public void convert(){
        if(targetCluster != null && golem.level.getBlockState(targetCluster).getBlock() == Blocks.AMETHYST_BLOCK){
            golem.level.setBlock(targetCluster, Blocks.BUDDING_AMETHYST.defaultBlockState(), 3);
            ParticleUtil.spawnTouchPacket(golem.level, targetCluster, ParticleUtil.defaultParticleColorWrapper());
        }
        golem.convertCooldown = 20 * 60 * 5;
    }

    @Override
    public void start() {
        this.isDone = false;
        this.usingTicks = 120;
        for(BlockPos pos : golem.convertables){
            if(golem.level.getBlockState(pos).getBlock() == Blocks.AMETHYST_BLOCK){
                targetCluster = pos;
                break;
            }
        }
    }

    @Override
    public boolean canUse() {
        return canUse.get() && golem.convertCooldown <= 0 && golem.convertables.size() > 0;
    }
}
