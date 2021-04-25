package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;

import java.util.ArrayList;
import java.util.List;

public interface IRitualCaster {

    List<String> getUnlockedRitualIDs();

    default List<AbstractRitual> getUnlockedRituals(){
        List<AbstractRitual> list = new ArrayList<>();
        for(String s : getUnlockedRitualIDs()){
            list.add(ArsNouveauAPI.getInstance().getRitual(s));
        }
        return list;
    }

    void unlockRitual(String ritualID);

    String getSelectedRitual();

    void setRitual(AbstractRitual ritual);

    void setRitual(String ritualID);
}
