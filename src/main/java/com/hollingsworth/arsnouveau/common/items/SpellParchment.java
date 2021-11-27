package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SpellParchment extends ModItem implements ICasterTool {
    public SpellParchment() {
        super(LibItemNames.SPELL_PARCHMENT);
    }

    public static void setSpell(ItemStack stack, String spellRecipe){
        stack.getOrCreateTag().putString("spell", spellRecipe);
    }

    @Deprecated
    public static List<AbstractSpellPart> getSpellRecipe(ItemStack stack){
        return getSpell(stack).recipe;
    }

    public static @Nonnull Spell getSpell(ItemStack stack){
        return SpellCaster.deserialize(stack).getSpell();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        getInformation(stack, worldIn, tooltip2, flagIn);
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}
