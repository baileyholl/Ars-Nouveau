package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;

public class PerkItem extends ModItem {

    public IPerk perk;

    public PerkItem(Properties properties) {
        super(properties);
    }

    public PerkItem(IPerk perk) {
        super(ItemsRegistry.defaultItemProperties());
        this.perk = perk;
    }

}
