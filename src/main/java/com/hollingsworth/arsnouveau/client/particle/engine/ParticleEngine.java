package com.hollingsworth.arsnouveau.client.particle.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ParticleEngine {
    List<TimedEffect> effects;
    // Allows effects to add other effects without concurrent modification
    List<TimedEffect> scheduled;
    public void tick(){
        if(!scheduled.isEmpty()){
            if(effects == null)
                effects = new ArrayList<>();
            effects.addAll(scheduled);
            System.out.println("adding effect");
            scheduled = new ArrayList<>();
        }

        if(effects.isEmpty()) {
            return;
        }

        ListIterator<TimedEffect> effectListIterator = effects.listIterator();
        List<TimedEffect> stale = new ArrayList<>();

        for(TimedEffect effect1 : effects){
            if(effect1.isDone){
                stale.add(effect1);
                continue;
            }
            effect1.tick();
        }
        for(TimedEffect effect : stale){
            effects.remove(effect);
        }


    }

    public void addEffect(TimedEffect effect){
        effects.add(effect);
    }

    // Schedule for next tick.
    public void scheduleEffect(TimedEffect effect){
        scheduled.add(effect);
    }

    public static ParticleEngine getInstance(){
        if(particleEngine == null)
            particleEngine = new ParticleEngine();
        return particleEngine;
    }

    private static ParticleEngine particleEngine;
    private ParticleEngine(){
        effects = new ArrayList<>();
        scheduled = new ArrayList<>();
    }
}
