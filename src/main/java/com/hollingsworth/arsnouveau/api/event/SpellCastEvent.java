package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import java.util.List;

@Cancelable
public class SpellCastEvent extends LivingEvent {

    public Spell spell;
    public SpellStats.Builder statsBuilder;

    @Deprecated // Marked for removal. Use stats sensitive version.
    public SpellCastEvent(LivingEntity entity, List<AbstractSpellPart> spell){
        super(entity);
        this.spell = new Spell(spell);
        this.statsBuilder = new SpellStats.Builder();
    }

    @Deprecated // Marked for removal. Use stats sensitive version.
    public SpellCastEvent(LivingEntity entity, Spell spell){
        super(entity);
        this.spell = spell;
        this.statsBuilder = new SpellStats.Builder();
    }

    public SpellCastEvent(LivingEntity entity, Spell spell, SpellStats.Builder spellStats){
        super(entity);
        this.spell = spell;
        this.statsBuilder = spellStats;
    }

}
