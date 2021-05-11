package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class RitualHealing extends AbstractRitual {
    @Override
    protected void tick() {
        if(getWorld().isClientSide){
            ParticleUtil.spawnRitualAreaEffect(tile, rand, getCenterColor(), 5);
        }else{
            if(getWorld().getGameTime() % 100 == 0){
                List<LivingEntity> entities = getWorld().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(getPos()).inflate(5));
                boolean didWorkOnce = false;
                for(LivingEntity a : entities){
                    if(a instanceof ZombieVillagerEntity){
                        ((ZombieVillagerEntity) a).startConverting(null, 0);
                        didWorkOnce = true;
                        continue;
                    }

                    if(a.getHealth() < a.getMaxHealth() || a.isInvertedHealAndHarm()) {
                        a.heal(10.0f);
                        didWorkOnce = true;
                    }
                }
                if(didWorkOnce)
                    setNeedsMana(true);
            }
        }
    }

    @Override
    public String getID() {
        return RitualLib.HEALING;
    }

    @Override
    public int getManaCost() {
        return 200;
    }
}
