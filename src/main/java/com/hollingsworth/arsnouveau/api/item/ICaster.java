package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public interface ICaster extends IScribeable, IDisplayMana {
    @Override
    default boolean onScribe(World world, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack stack) {
        ItemStack heldStack = player.getHeldItem(handIn);
        ISpellCaster caster = getCaster(stack);

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

    default ISpellCaster getCaster(ItemStack stack){
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
}
