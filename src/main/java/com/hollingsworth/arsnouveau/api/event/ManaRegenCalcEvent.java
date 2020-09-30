package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class ManaRegenCalcEvent extends LivingEvent {

    private int regen;
    public ManaRegenCalcEvent(LivingEntity entity, int regen) {
        super(entity);
        this.regen = regen;
    }


    public void setRegen(int newRegen){
        this.regen = Math.max(newRegen, 0);
    }

    public int getRegen(){
        return this.regen;
    }

}