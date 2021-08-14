package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;

public class FamiliarScript extends ModItem{
    public AbstractFamiliarHolder familiar;


    public FamiliarScript(AbstractFamiliarHolder familiar){
        super("familiar_" + familiar.id);
        this.familiar = familiar;
    }
    public FamiliarScript(Properties properties) {
        super(properties);
    }

    public FamiliarScript(Properties properties, String registryName) {
        super(properties, registryName);
    }

    public FamiliarScript(String registryName) {
        super(registryName);
    }

}
