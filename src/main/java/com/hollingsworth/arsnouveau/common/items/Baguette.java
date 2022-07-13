package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.item.Item;

public class Baguette extends ModItem {
    public Baguette() {
        this(ItemsRegistry.defaultItemProperties().stacksTo(64));
    }

    public Baguette(Item.Properties props) {
        super(props);
    }

    @Override
    public boolean isEdible() {
        return true;
    }
}
