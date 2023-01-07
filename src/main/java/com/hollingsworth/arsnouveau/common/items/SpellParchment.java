package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SpellParchment extends ModItem implements ICasterTool {

    public SpellParchment(Properties properties) {
        super(properties);
    }

    public SpellParchment() {
        super();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        ISpellCaster caster = getSpellCaster(stack);

        if (caster.getSpell().isEmpty()) {
            tooltip2.add(Component.translatable("ars_nouveau.tooltip.from_blank"));
            return;
        }
        getInformation(stack, worldIn, tooltip2, flagIn);
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}
