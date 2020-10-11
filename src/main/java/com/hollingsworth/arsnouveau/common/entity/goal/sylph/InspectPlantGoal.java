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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

public class InspectPlantGoal extends DistanceRestrictedGoal {
    MobEntity entity;
    BlockPos pos;
    int timeLooking;
    int timePerforming;
    public InspectPlantGoal(MobEntity entity, Supplier<BlockPos> from, int maxDistanceFrom){
        super(from, maxDistanceFrom);
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    public boolean hasVisibleSide(BlockPos pos){
        for(Direction d : Direction.values()){
            if(entity.world.getBlockState(pos.offset(d)).getMaterial() == Material.AIR)
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
        if(BlockUtil.distanceFrom(entity.getPosition(), pos) > 1.5){
            entity.getNavigator().tryMoveToXYZ(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 1.2);
        }else{
            ServerWorld world = (ServerWorld) entity.world;
            entity.lookAt(EntityAnchorArgument.Type.EYES,new Vec3d(this.pos.getX(), this.pos.getY(), this.pos.getZ()));
            if(world.rand.nextInt(20) == 0)
                world.spawnParticle(ParticleTypes.HEART, this.pos.getX() +0.5, this.pos.getY()+1.1, this.pos.getZ()+0.5, 1, ParticleUtil.inRange(-0.2, 0.2),0,ParticleUtil.inRange(-0.2, 0.2),0.01);
            this.timeLooking--;
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return pos != null && timeLooking > 0 && timePerforming > 0;
    }

    @Override
    public boolean shouldExecute() {
        return entity.getEntityWorld().rand.nextInt(100) <= 2;
    }

    @Override
    public void startExecuting() {
        int range = 4;
        List<BlockPos> list = new ArrayList<>();
        BlockPos.getAllInBox(entity.getPosition().add(range, range, range), entity.getPosition().add(-range, -range, -range)).forEach(bp ->{
            if(EvaluateGroveGoal.getScore(entity.world.getBlockState(bp)) > 0 && hasVisibleSide(bp) && isInRange(bp)){
                list.add(bp.toImmutable());
            }
        });
        if(list.isEmpty())
            return;
        pos = list.get(entity.world.rand.nextInt(list.size()));
        this.timeLooking = 60;
        this.timePerforming = 240;
    }
}
