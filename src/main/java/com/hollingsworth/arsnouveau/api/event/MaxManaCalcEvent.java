package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * An event that fires after Ars Nouveau has calculated the preliminary Max Mana provided by gear, glyphs, and book tiers.
 */
public class MaxManaCalcEvent extends LivingEvent {

    private int max;

    public MaxManaCalcEvent(LivingEntity entity, int max) {
        super(entity);
        this.max = max;
    }


    public void setMax(int newMax) {
        this.max = Math.max(newMax, 0);
    }

    public int getMax() {
        return this.max;
    }

}
