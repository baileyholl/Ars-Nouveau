package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;

public class RitualNight extends AbstractRitual {
    @Override
    protected void tick() {

    }

    @Override
    public String getID() {
        return RitualLib.NIGHT;
    }
}
