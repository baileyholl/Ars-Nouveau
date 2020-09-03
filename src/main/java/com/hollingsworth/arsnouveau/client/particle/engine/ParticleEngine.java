package com.hollingsworth.arsnouveau.client.particle.engine;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ParticleEngine {
    List<TimedEffect> effects;

    public void tick(){
        if(effects == null || effects.size() == 0) {
            return;
        }
        ListIterator<TimedEffect> effectListIterator = effects.listIterator();
        TimedEffect effect;
        while(effectListIterator.hasNext()){
            effect = effectListIterator.next();
            if(effect.isDone) {
                effectListIterator.remove();

                continue;
            }
            effect.tick();

        }
    }

    public void addEffect(TimedEffect effect){
        effects.add(effect);
    }

    public static ParticleEngine getInstance(){
        if(particleEngine == null)
            particleEngine = new ParticleEngine();
        return particleEngine;
    }

    private static ParticleEngine particleEngine;
    private ParticleEngine(){
        effects = new ArrayList<>();
    }
}
