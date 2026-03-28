package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;

public class SpellParchment extends ModItem implements ICasterTool {

    public SpellParchment(Properties properties) {
        super(properties);
    }

    public SpellParchment() {
        super(ItemsRegistry.defaultItemProperties().component(DataComponents.BASE_COLOR, DyeColor.PURPLE).component(DataComponentRegistry.SPELL_CASTER, new SpellCaster()));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        AbstractCaster<?> caster = getSpellCaster(pStack);
        return caster.getSpellName().isEmpty() ? super.getName(pStack) : Component.literal(caster.getSpellName());
    }

        @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        stack.addToTooltip(DataComponentRegistry.SPELL_CASTER, context, display, tooltip2, flagIn);
        super.appendHoverText(stack, context, display, tooltip2, flagIn);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        AbstractCaster<?> caster = getSpellCaster(pStack);
        if (caster != null && Config.GLYPH_TOOLTIPS.get() && !Minecraft.getInstance().hasShiftDown() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }
}
