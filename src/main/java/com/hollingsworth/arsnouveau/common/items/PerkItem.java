package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return Component.literal(perk.getName());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        if (perk == null)
            return;

        if (flagIn.hasShiftDown()) {
            tooltip2.add(Component.translatable(perk.getDescriptionKey()));
        } else {
            tooltip2.add(Component.translatable("tooltip.ars_nouveau.hold_shift", Component.keybind("key.sneak")));
        }
    }
}
