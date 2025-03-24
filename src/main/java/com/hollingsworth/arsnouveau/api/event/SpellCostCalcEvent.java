package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.neoforged.bus.api.Event;

public class SpellCostCalcEvent extends Event {

    public SpellContext context;
    public int currentCost;

    /**
     * Use {@link Pre} or {@link Post} instead, becoming private in 1.22
     */
    @Deprecated
    public SpellCostCalcEvent(SpellContext context, int currentCost) {
        this.context = context;
        this.currentCost = currentCost;
    }

    public static class Pre extends SpellCostCalcEvent {
        public Pre(SpellContext context, int currentCost) {
            super(context, currentCost);
        }
    }

    public static class Post extends SpellCostCalcEvent {
        public Post(SpellContext context, int currentCost) {
            super(context, currentCost);
        }
    }
}
