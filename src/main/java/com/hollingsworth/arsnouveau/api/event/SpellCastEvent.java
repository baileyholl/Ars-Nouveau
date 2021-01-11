package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import java.util.List;

@Cancelable
public class SpellCastEvent extends LivingEvent {

    public Spell spell;
    @Deprecated
    public SpellCastEvent(LivingEntity entity, List<AbstractSpellPart> spell){
        super(entity);
        this.spell = new Spell(spell);
    }

    public SpellCastEvent(LivingEntity entity, Spell spell){
        super(entity);
        this.spell = spell;
    }


}
