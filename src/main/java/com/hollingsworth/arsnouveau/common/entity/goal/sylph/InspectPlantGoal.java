package com.hollingsworth.arsnouveau.common.entity.goal.sylph;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.goal.DistanceRestrictedGoal;
import net.minecraft.block.material.Material;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.MobEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class InspectPlantGoal extends DistanceRestrictedGoal {
    MobEntity entity;
    BlockPos pos;
    int timeLooking;
    int timePerforming;
    public InspectPlantGoal(MobEntity entity, Supplier<BlockPos> from, int maxDistanceFrom){
        super(from, maxDistanceFrom);
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    public boolean hasVisibleSide(BlockPos pos){
        for(Direction d : Direction.values()){
            if(entity.level.getBlockState(pos.relative(d)).getMaterial() == Material.AIR)
                return true;
        }
        return false;
    }

    @Override
    public void tick() {
        if(this.pos == null) {
            return;
        }
        timePerforming--;
        if(BlockUtil.distanceFrom(entity.blockPosition(), pos) > 1.5){
            entity.getNavigation().moveTo(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 1.2);
        }else{
            ServerWorld world = (ServerWorld) entity.level;
            entity.lookAt(EntityAnchorArgument.Type.EYES,new Vector3d(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
            if(world.random.nextInt(20) == 0)
                world.sendParticles(ParticleTypes.HEART, this.pos.getX() +0.5, this.pos.getY()+1.1, this.pos.getZ()+0.5, 1, ParticleUtil.inRange(-0.2, 0.2),0,ParticleUtil.inRange(-0.2, 0.2),0.01);
            this.timeLooking--;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return pos != null && timeLooking > 0 && timePerforming > 0;
    }

    @Override
    public boolean canUse() {
        return entity.getCommandSenderWorld().random.nextInt(100) <= 2;
    }

    @Override
    public void start() {
        int range = 4;
        List<BlockPos> list = new ArrayList<>();
        BlockPos.betweenClosedStream(entity.blockPosition().offset(range, range, range), entity.blockPosition().offset(-range, -range, -range)).forEach(bp ->{
            if(EvaluateGroveGoal.getScore(entity.level.getBlockState(bp)) > 0 && hasVisibleSide(bp) && isInRange(bp)){
                list.add(bp.immutable());
            }
        });
        if(list.isEmpty())
            return;
        pos = list.get(entity.level.random.nextInt(list.size()));
        this.timeLooking = 60;
        this.timePerforming = 240;
    }
}
