package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ModItem extends Item {
    public List<Component> tooltip = new ArrayList<>();
    public Rarity rarity;

    public ModItem(Properties properties) {
        super(properties);
    }

    public ModItem() {
        this(ItemsRegistry.defaultItemProperties());
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

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if (tooltip != null && !tooltip.isEmpty()) {
            tooltip2.addAll(tooltip);
        }
    }
}
