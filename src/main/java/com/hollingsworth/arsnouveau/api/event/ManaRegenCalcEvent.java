package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class ManaRegenCalcEvent extends LivingEvent {

    private double regen;
    public ManaRegenCalcEvent(LivingEntity entity, double regen) {
        super(entity);
        this.regen = regen;
    }


    public void setRegen(double newRegen){
        this.regen = Math.max(newRegen, 0);
    }

    public double getRegen(){
        return this.regen;
    }

}