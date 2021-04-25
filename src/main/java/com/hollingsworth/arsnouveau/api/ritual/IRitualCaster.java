package com.hollingsworth.arsnouveau.api.ritual;

import java.util.List;

public interface IRitualCaster {

    public List<AbstractRitual> getUnlockedRituals();

    public void unlockRitual();

    String getSelectedRitual();

    void setRitual(AbstractRitual spell);

}
