package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.neoforged.bus.api.Event;

public class SpellCostCalcEvent extends Event {

    public SpellContext context;
    public int currentCost;

    public SpellCostCalcEvent(SpellContext context, int currentCost) {
        this.context = context;
        this.currentCost = currentCost;
    }
}
