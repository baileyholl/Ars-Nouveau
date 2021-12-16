package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * An interface for caster items that provides default behavior for scribing, displaying mana, and tooltips
 */
public interface ICasterTool extends IScribeable, IDisplayMana {
    @Override
    default boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack stack) {
        ItemStack heldStack = player.getItemInHand(handIn);
        ISpellCaster caster = getSpellCaster(stack);

        if(!((heldStack.getItem() instanceof SpellBook) || (heldStack.getItem() instanceof SpellParchment)) || heldStack.getTag() == null)
            return false;
        boolean success;
        Spell spell = new Spell();
        if(heldStack.getItem() instanceof ICasterTool){
            ISpellCaster heldCaster = getSpellCaster(heldStack);
            spell = heldCaster.getSpell();
            caster.setColor(heldCaster.getColor());
            caster.setFlavorText(heldCaster.getFlavorText());
        }
        if(isScribedSpellValid(caster, player, handIn, stack, spell)){
            success = setSpell(caster, player, handIn, stack, spell);
            if(success){
                sendSetMessage(player);
                return true;
            }
        }else{
            sendInvalidMessage(player);
        }
        return false;
    }

    default void sendSetMessage(Player player){
        PortUtil.sendMessageNoSpam(player, new TranslatableComponent("ars_nouveau.set_spell"));
    }

    default void sendInvalidMessage(Player player){
        PortUtil.sendMessageNoSpam(player, new TranslatableComponent("ars_nouveau.invalid_spell"));
    }

    default @Nonnull ISpellCaster getSpellCaster(ItemStack stack){
        return new SpellCaster(stack);
    }

    default boolean setSpell(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell){
        caster.setSpell(spell);
        return true;
    }

    default boolean isScribedSpellValid(ISpellCaster caster, Player player, InteractionHand hand, ItemStack stack, Spell spell){
        return spell.isValid();
    }

    @Override
    default boolean shouldDisplay(ItemStack stack) {
        return true;
    }

    default void getInformation(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if(worldIn == null)
            return;
        ISpellCaster caster = getSpellCaster(stack);

        if(caster.getSpell().isEmpty()){
            tooltip2.add(new TranslatableComponent("ars_nouveau.tooltip.can_inscribe"));
            return;
        }

        Spell spell = caster.getSpell();
        tooltip2.add(new TextComponent(spell.getDisplayString()));
        if(!caster.getFlavorText().isEmpty())
            tooltip2.add(new TextComponent(caster.getFlavorText()).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE)));
    }
}
