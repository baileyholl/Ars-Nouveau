package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class SpellParchment extends ModItem implements ICasterTool {

    public SpellParchment(Properties properties) {
        super(properties);
    }

    public SpellParchment() {
        super();
    }

    @Override
    public Component getName(ItemStack pStack) {
        ISpellCaster caster = getSpellCaster(pStack);
        return caster.getSpellName().isEmpty() ? super.getName(pStack) : Component.literal(caster.getSpellName());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        ISpellCaster caster = getSpellCaster(stack);

        if (!Config.GLYPH_TOOLTIPS.get() || caster.isSpellHidden() || caster.getSpell().isEmpty())
            getInformation(stack, worldIn, tooltip2, flagIn);

        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        ISpellCaster caster = getSpellCaster(pStack);
        if (Config.GLYPH_TOOLTIPS.get() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }
}
