package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class PerkItem extends ModItem {

    public IPerk perk;

    public PerkItem(Properties properties) {
        super(properties);
    }

    public PerkItem(IPerk perk) {
        super(ItemsRegistry.defaultItemProperties());
        this.perk = perk;
    }

    @Override
    public Component getName(ItemStack pStack) {
        return Component.literal(perk.getName());
    }
}
