package com.hollingsworth.arsnouveau.common.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Glyph extends ModItem{
    public AbstractSpellPart spellPart;
    public Glyph(String registryName, AbstractSpellPart part) {
        super(registryName);
        this.spellPart = part;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(worldIn.isClientSide)
            return super.use(worldIn, playerIn, handIn);

        if(!Config.isSpellEnabled(this.spellPart.tag)){
            playerIn.sendMessage(new TranslationTextComponent("ars_nouveau.spell.disabled"), Util.NIL_UUID);
            return super.use(worldIn, playerIn, handIn);
        }

        playerIn.inventory.items.forEach(itemStack -> {
            if(itemStack.getItem() instanceof SpellBook){
                if(SpellBook.getUnlockedSpells(itemStack.getTag()).contains(spellPart)){
                    playerIn.sendMessage(new StringTextComponent("You already know this spell!"),  Util.NIL_UUID);
                    return;
                }
                SpellBook.unlockSpell(itemStack.getTag(), this.spellPart.getTag());
                playerIn.getItemInHand(handIn).shrink(1);
                playerIn.sendMessage(new StringTextComponent("Unlocked " + this.spellPart.getName()), Util.NIL_UUID);
            }
        });
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip2, ITooltipFlag flagIn) {
        if(spellPart != null){
            if(!Config.isSpellEnabled(this.spellPart.tag)){
                tooltip2.add(new StringTextComponent("Disabled. Cannot be used."));
            }
        }
    }

    public JsonElement asRecipe(){
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:glyph_recipe");
        jsonobject.addProperty("tier", this.spellPart.getTier().toString());
        if(this.spellPart.getCraftingReagent() != null)
            jsonobject.addProperty("input", this.spellPart.getCraftingReagent().getRegistryName().toString());
        jsonobject.addProperty("output", this.getRegistryName().toString());
        return jsonobject;
    }
}
