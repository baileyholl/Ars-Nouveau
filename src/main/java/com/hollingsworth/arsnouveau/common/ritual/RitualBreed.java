package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class RitualBreed extends AbstractRitual {
    @Override
    protected void tick() {
        if(getWorld().isClientSide){
            World world = getWorld();
            BlockPos pos = getPos();


                Vector3d particlePos = new Vector3d(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5,5,5));
                int range = 5;
                BlockPos.betweenClosedStream(pos.offset(range, 0, range), pos.offset(-range, 0, -range)).forEach(blockPos -> {
                    if(rand.nextInt(10) == 0){
                        for(int i =0; i< rand.nextInt(10); i++) {
                            double x = blockPos.getX() + ParticleUtil.inRange(-0.5, 0.5);
                            double y = blockPos.getY() + ParticleUtil.inRange(-0.5, 0.5);
                            double z = blockPos.getZ() + ParticleUtil.inRange(-0.5, 0.5);
                            world.addParticle(ParticleLineData.createData(getCenterColor()),
                                    x, y, z,
                                    x, y  + ParticleUtil.inRange(0.5, 5), z);
                        }
                    }
                });
//                world.addParticle(ParticleLineData.createData(getCenterColor()),
//                        particlePos.x(), particlePos.y(), particlePos.z(),
//                        pos.getX()  +0.5, pos.getY() + 1  , pos.getZ() +0.5);

        }else{
            if(getWorld().getGameTime() % 200 == 0){
                List<AnimalEntity> animals = getWorld().getEntitiesOfClass(AnimalEntity.class, new AxisAlignedBB(getPos()).inflate(5));
                if(animals.size() >= 20)
                    return;
                boolean didWorkOnce = false;
                for(AnimalEntity a : animals){
                    if(a.getAge() == 0 && a.canFallInLove()){
                        didWorkOnce = true;
                        a.setInLove(null);
                    }
                }
                if(didWorkOnce)
                    setNeedsMana(true);
            }
        }
    }

    @Override
    public String getLangDescription() {
        return "Periodically causes nearby animals to breed if possible. This ritual requires mana to operate, and will have no effect if there are twenty or more animals nearby.";
    }

    @Override
    public String getLangName() {
        return "Fertility";
    }

    @Override
    public int getManaCost() {
        return 500;
    }

    @Override
    public String getID() {
        return RitualLib.FERTILITY;
    }
}
