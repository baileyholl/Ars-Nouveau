package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.mixin.ExpInvokerMixin;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class RitualExpDrain extends AbstractRitual {


    @Override
    public void onStart() {
        super.onStart();
        if(tile == null)
            return;

    }

    @Override
    protected void tick() {
        World world = getWorld();
        if(world.isClientSide){
            BlockPos pos = getPos();

                for(int i =0; i< 100; i++){
                    Vector3d particlePos = new Vector3d(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
                    particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5,5,5));
                    world.addParticle(ParticleLineData.createData(getCenterColor()),
                            particlePos.x(), particlePos.y(), particlePos.z(),
                            pos.getX()  +0.5, pos.getY() + 1  , pos.getZ() +0.5);
                }
        }

        if(!world.isClientSide && world.getGameTime() % 60 == 0){
            world.getEntitiesOfClass(MonsterEntity.class, new AxisAlignedBB(getPos()).inflate(5.0), (m) -> true).forEach(m ->{
                m.remove();
                if(m.removed){
                    ExpInvokerMixin invoker = ((ExpInvokerMixin)m);
                    ParticleUtil.spawnPoof((ServerWorld) world, m.blockPosition());
                    if(invoker.an_shouldDropExperience()) {
                        world.addFreshEntity(new ExperienceOrbEntity(world, m.position.x, m.position.y, m.position.z,
                                invoker.an_getExperienceReward(new ANFakePlayer((ServerWorld) getWorld())) * 5));
                    }
                }
            });

        }
    }

    @Override
    public String getID() {
        return RitualLib.EXP;
    }
}
