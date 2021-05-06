package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.world.server.ServerWorld;

public class RitualStorm extends AbstractRitual {
    @Override
    protected void tick() {
        ParticleUtil.spawnRitualSkyEffect(this, tile, rand, new ParticleColor.IntWrapper(50 + rand.nextInt(100), 50 + rand.nextInt(100), 200 + rand.nextInt(255)));
        if (getWorld().getGameTime() % 20 == 0 && !getWorld().isClientSide) {
            incrementProgress();
            if (getProgress() >= 18) {
                ServerWorld world = (ServerWorld) getWorld();
                world.setWeatherParameters(0, 6000, true, true);
                setFinished();
            }
        }

    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public String getID() {
        return RitualLib.STORM;
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(
                rand.nextInt(100),
                rand.nextInt(100),
                rand.nextInt(255));
    }
}