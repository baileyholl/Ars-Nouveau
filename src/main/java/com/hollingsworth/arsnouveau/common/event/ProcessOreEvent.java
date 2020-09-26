package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.common.entity.EntityEarthElemental;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ProcessOreEvent implements ITimedEvent {

    EntityEarthElemental entityEarthElemental;
    int duration;

    public ProcessOreEvent(EntityEarthElemental entityEarthElemental, int duration){
        this.entityEarthElemental = entityEarthElemental;
        this.duration = duration;
    }

    @Override
    public void tick() {

        duration--;
        if(duration == 0){
            System.out.println("setting stack");
            entityEarthElemental.setHeldStack(new ItemStack(Items.IRON_INGOT));
            entityEarthElemental.world.addEntity(new ItemEntity(entityEarthElemental.getEntityWorld(), entityEarthElemental.getPosX(), entityEarthElemental.getPosY(),
                    entityEarthElemental.getPosZ(),
            new ItemStack(Items.IRON_INGOT)));
        }
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }
}
