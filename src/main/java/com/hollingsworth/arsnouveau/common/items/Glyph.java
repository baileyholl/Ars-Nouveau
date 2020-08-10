package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class Glyph extends ModItem{
    public AbstractSpellPart spellPart;
    public Glyph(String registryName, AbstractSpellPart part) {
        super(registryName);
        this.spellPart = part;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(worldIn.isRemote)
            return super.onItemRightClick(worldIn, playerIn, handIn);


        playerIn.inventory.mainInventory.forEach(itemStack -> {
            if(itemStack.getItem() instanceof SpellBook){
                if(SpellBook.getUnlockedSpells(itemStack.getTag()).contains(spellPart)){
                    playerIn.sendMessage(new StringTextComponent("You already know this spell!"));
                    return;
                }
                SpellBook.unlockSpell(itemStack.getTag(), this.spellPart.getTag());
                playerIn.getHeldItem(handIn).shrink(1);
                playerIn.sendMessage(new StringTextComponent("Unlocked " + this.spellPart.getName()));
            }
        });
    return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
