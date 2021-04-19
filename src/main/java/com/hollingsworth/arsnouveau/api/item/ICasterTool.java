package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public interface ICasterTool extends IScribeable, IDisplayMana {
    @Override
    default boolean onScribe(World world, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack stack) {
        ItemStack heldStack = player.getItemInHand(handIn);
        ISpellCaster caster = getSpellCaster(stack);

        if(caster == null)
            return false;

        if(!((heldStack.getItem() instanceof SpellBook) || (heldStack.getItem() instanceof SpellParchment)) || heldStack.getTag() == null)
            return false;
        boolean success = false;
        Spell spell = new Spell();
        if(heldStack.getItem() instanceof SpellBook) {
            spell = SpellBook.getRecipeFromTag(heldStack.getTag(), SpellBook.getMode(heldStack.getTag()));
        }else if(heldStack.getItem() instanceof SpellParchment){
            spell = new Spell(SpellParchment.getSpellRecipe(heldStack));
        }
        if(isScribedSpellValid(caster, player, handIn, stack, spell)){
            success = setSpell(caster, player, handIn, stack, spell);
            if(success){
                sendSetMessage(player);
                return success;
            }
        }else{
            sendInvalidMessage(player);
        }
        return success;
    }

    default void sendSetMessage(PlayerEntity player){
        PortUtil.sendMessage(player, new StringTextComponent("Set spell."));
    }

    default void sendInvalidMessage(PlayerEntity player){
        PortUtil.sendMessage(player, new StringTextComponent("Invalid spell."));
    }

    default ISpellCaster getSpellCaster(ItemStack stack){
        return SpellCaster.deserialize(stack);
    }

    default boolean setSpell(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell){
        caster.setSpell(spell);
        return true;
    }

    default boolean isScribedSpellValid(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell){
        return spell.isValid();
    }

    @Override
    default boolean shouldDisplay(ItemStack stack) {
        return true;
    }


    default void getInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        if(worldIn == null)
            return;
        ISpellCaster caster = getSpellCaster(stack);
        if(caster == null)
            return;
        if(caster.getSpell() == null)
            return;

        Spell spell = caster.getSpell();
        tooltip2.add(new StringTextComponent(spell.getDisplayString()));

    }
}
