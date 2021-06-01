package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SpellParchment extends ModItem implements IScribeable {
    public SpellParchment() {
        super(LibItemNames.SPELL_PARCHMENT);
    }

    @Override
    public void inventoryTick(ItemStack stack, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        if(!stack.hasTag())
            stack.setTag(new CompoundNBT());
    }

    public static void setSpell(ItemStack stack, String spellRecipe){
        stack.getTag().putString("spell", spellRecipe);
    }

    public static List<AbstractSpellPart> getSpellRecipe(ItemStack stack){
        if(!stack.hasTag())
            return null;
        return SpellRecipeUtil.getSpellsFromTagString(stack.getTag().getString("spell"));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag p_77624_4_) {
        if(!stack.hasTag() || stack.getTag().getString("spell").equals(""))
            return;

        List<AbstractSpellPart> spellsFromTagString = SpellRecipeUtil.getSpellsFromTagString(stack.getTag().getString("spell"));
        Spell spell = new Spell(spellsFromTagString);
        tooltip.add(new StringTextComponent(spell.getDisplayString()));
    }

    @Override
    public boolean onScribe(World world, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack thisStack) {

        if(!(player.getItemInHand(handIn).getItem() instanceof SpellBook))
            return false;

        if(SpellBook.getMode(player.getItemInHand(handIn).getTag()) == 0){
            PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.spell_parchment.no_spell"));
            return false;
        }

        SpellParchment.setSpell(thisStack, SpellBook.getRecipeString(player.getItemInHand(handIn).getTag(), SpellBook.getMode(player.getItemInHand(handIn).getTag())));
        PortUtil.sendMessage(player,new TranslationTextComponent("ars_nouveau.spell_parchment.inscribed"));
        return false;
    }
}
