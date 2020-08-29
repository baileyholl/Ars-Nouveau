package com.hollingsworth.arsnouveau.client;


import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;


public class GuiEntityInfoHUD extends AbstractGui {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD(EntityWhelp whelp) {

       int offsetLeft = 5;
        fill(offsetLeft, 50, (int)100+ offsetLeft, 0, 300);
        ArrayList<AbstractSpellPart> spellParts = SpellRecipeUtil.getSpellsFromTagString(whelp.getRecipeString());
        String spellString = spellParts.size() > 4 ? SpellRecipeUtil.getDisplayString(spellParts.subList(0, 4)) + "..." :SpellRecipeUtil.getDisplayString(spellParts);

        minecraft.fontRenderer.drawStringWithShadow("Casting: " + spellString, offsetLeft, 5, 0xFFFFFF);

        String itemString = whelp.getHeldStack() == ItemStack.EMPTY ? "Nothing." : whelp.getHeldStack().getDisplayName().getFormattedText();
        String itemAction = whelp.getHeldStack().getItem() instanceof BlockItem ? "Placing: " : "Using: ";
        minecraft.fontRenderer.drawStringWithShadow(itemAction + itemString, offsetLeft, 15, 0xFFFFFF);

    }
}
