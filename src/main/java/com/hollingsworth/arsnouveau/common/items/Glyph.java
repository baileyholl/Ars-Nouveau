package com.hollingsworth.arsnouveau.common.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.Util;
import net.minecraft.util.text.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class Glyph extends ModItem{
    public AbstractSpellPart spellPart;
    public Glyph(String registryName, AbstractSpellPart part) {
        super(registryName);
        this.spellPart = part;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if(worldIn.isClientSide)
            return super.use(worldIn, playerIn, handIn);

        if(!Config.isSpellEnabled(this.spellPart.tag)){
            playerIn.sendMessage(new TranslatableComponent("ars_nouveau.spell.disabled"), Util.NIL_UUID);
            return super.use(worldIn, playerIn, handIn);
        }

        playerIn.inventory.items.forEach(itemStack -> {
            if(itemStack.getItem() instanceof SpellBook){
                if(SpellBook.getUnlockedSpells(itemStack.getTag()).contains(spellPart)){
                    playerIn.sendMessage(new TextComponent("You already know this spell!"),  Util.NIL_UUID);
                    return;
                }
                SpellBook.unlockSpell(itemStack.getTag(), this.spellPart.getTag());
                playerIn.getItemInHand(handIn).shrink(1);
                playerIn.sendMessage(new TextComponent("Unlocked " + this.spellPart.getName()), Util.NIL_UUID);
            }
        });
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if(spellPart != null){
            if(!Config.isSpellEnabled(this.spellPart.tag)){
                tooltip2.add(new TranslatableComponent("tooltip.ars_nouveau.glyph_disabled"));
            }else if(spellPart != null){
                tooltip2.add(new TranslatableComponent("tooltip.ars_nouveau.glyph_level", spellPart.getTier().ordinal() + 1).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
                tooltip2.add(new TranslatableComponent("ars_nouveau.schools"));
                for(SpellSchool s : spellPart.getSchools()){
                    tooltip2.add(s.getTextComponent());
                }
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
