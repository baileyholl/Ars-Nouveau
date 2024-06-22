package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class SpellCastEvent extends LivingEvent implements ICancellableEvent {

    public Spell spell;
    public SpellContext context;

    public SpellCastEvent(Spell spell, SpellContext context) {
        super(context.getUnwrappedCaster());
        this.spell = spell;
        this.context = context;
    }

    public Level getWorld() {
        return this.getEntity().level;
    }
}
