package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;

public class PerkItem extends ModItem {

    public IPerk perk;

    public PerkItem(Properties properties) {
        super(properties);
    }

    public PerkItem(IPerk perk) {
        super(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, perk.getRegistryName())));
        this.perk = perk;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return Component.literal(perk.getName());
    }

        @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        if (perk == null)
            return;

        if (flagIn.hasShiftDown()) {
            tooltip2.accept(Component.translatable(perk.getDescriptionKey()));
        } else {
            tooltip2.accept(Component.translatable("tooltip.ars_nouveau.hold_shift", Component.keybind("key.sneak")));
        }
    }
}
