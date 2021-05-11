package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class RitualBreed extends AbstractRitual {
    @Override
    protected void tick() {
        if(getWorld().isClientSide){
            ParticleUtil.spawnRitualAreaEffect(tile, rand, getCenterColor(), 5);
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
