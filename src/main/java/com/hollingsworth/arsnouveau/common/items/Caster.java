package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.common.capability.CasterCapability;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Caster extends ModItem implements IScribeable {
    public Caster(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return CasterCapability.createProvider(new SpellCaster(stack));
    }

    @Override
    public boolean onScribe(World world, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack stack) {
        ItemStack heldStack = player.getHeldItem(handIn);
        if(!(heldStack.getItem() instanceof SpellBook) || !(heldStack.getItem() instanceof SpellParchment) || heldStack.getTag() == null)
            return false;
        if(heldStack.getItem() instanceof SpellBook) {
            List<AbstractSpellPart> spellParts = SpellBook.getUnlockedSpells(heldStack.getTag());
            int unlocked = 0;
            for (AbstractSpellPart spellPart : spellParts) {
                if (SpellBook.unlockSpell(stack.getTag(), spellPart))
                    unlocked++;
            }
            PortUtil.sendMessage(player, new StringTextComponent("Copied " + unlocked + " new spells to the book."));
            return true;
        }else if(heldStack.getItem() instanceof SpellParchment){

            return true;
        }
        return false;
    }

}
