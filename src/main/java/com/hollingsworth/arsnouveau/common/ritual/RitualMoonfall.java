package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.world.server.ServerWorld;

public class RitualMoonfall extends AbstractRitual {
    @Override
    protected void tick() {
        ParticleUtil.spawnRitualSkyEffect(this, tile, rand, new ParticleColor.IntWrapper(50+  rand.nextInt(50), 50+ rand.nextInt(50), 200 + rand.nextInt(55)));
        if(getWorld().getGameTime() % 20 == 0 && !getWorld().isClientSide) {
            incrementProgress();
            if(getProgress() >= 18){
                ServerWorld world = (ServerWorld) getWorld();
                world.setDayTime(13000);
                setFinished();
            }
        }

    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public String getLangName() {
        return "Moonfall";
    }

    @Override
    public String getLangDescription() {
        return "Sets the time to night.";
    }

    @Override
    public String getID() {
        return RitualLib.MOONFALL;
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(
                rand.nextInt(25),
                rand.nextInt(25),
                rand.nextInt(255));
    }
}
