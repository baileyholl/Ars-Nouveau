package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

@Cancelable
public class SpellCastEvent extends LivingEvent {

    public List<AbstractSpellPart> spell;
    public SpellCastEvent(LivingEntity entity, List<AbstractSpellPart> spell){
        super(entity);
        this.spell = spell;
    }

}
