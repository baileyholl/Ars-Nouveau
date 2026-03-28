package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModItem extends Item {
    public List<Component> tooltip = new ArrayList<>();
    public Rarity rarity;

    public ModItem(Properties properties) {
        super(properties);
    }

    public ModItem() {
        this(defaultProps());
    }

    public ModItem withTooltip(Component tip) {
        tooltip.add(tip);
        return this;
    }

    public ModItem withTooltip(String tip) {
        tooltip.add(Component.translatable(tip));
        return this;
    }

    public ModItem withRarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    protected static Properties defaultProps() {
        return ItemsRegistry.newItemProperties();
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, display, tooltip2, flagIn);
        if (tooltip != null && !tooltip.isEmpty()) {
            tooltip.forEach(tooltip2);
        }
    }
}
