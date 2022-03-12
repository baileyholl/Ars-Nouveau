package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.server.level.ServerLevel;

public class RitualMoonfall extends AbstractRitual {
    @Override
    protected void tick() {
        ParticleUtil.spawnRitualSkyEffect(this, tile, rand, new ParticleColor.IntWrapper(50+  rand.nextInt(50), 50+ rand.nextInt(50), 200 + rand.nextInt(55)));
        if(getWorld().getGameTime() % 20 == 0 && !getWorld().isClientSide) {
            incrementProgress();
            if(getProgress() >= 18){
                ServerLevel world = (ServerLevel) getWorld();
                world.setDayTime(MathUtil.getNextDaysTime(world, MathUtil.NIGHT_TIME));
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
