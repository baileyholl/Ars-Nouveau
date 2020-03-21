package com.hollingsworth.arsnouveau.api;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import java.util.HashMap;

public class ArsNouveauAPI {


    /**
     * Map of all spells to be registered in the spell system
     *
     * key: Unique spell ID
     * value: Associated spell
     */
    private HashMap<String, AbstractSpellPart> spell_map;



    public AbstractSpellPart registerSpell(String id, AbstractSpellPart part){
        return spell_map.put(id, part);
    }

    public HashMap<String, AbstractSpellPart> getSpell_map() {
        return spell_map;
    }



    private ArsNouveauAPI(){
        spell_map = new HashMap<>();

    }

    public static ArsNouveauAPI getInstance(){
        if(arsNouveauAPI == null)
            arsNouveauAPI = new ArsNouveauAPI();
        return arsNouveauAPI;
    }

    private static ArsNouveauAPI arsNouveauAPI = null;
}
