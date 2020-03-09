package com.hollingsworth.craftedmagic.client.gui.buttons;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellBook;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class CraftingButton extends GuiImageButton{
    int slotNum;
    public String spell_id;
    public String resourceIcon;

    public CraftingButton(GuiSpellBook parent, int x, int y, int slotNum, Button.IPressable onPress) {
        super( x, y, 0, 0, 20, 20, 20, 20, "textures/gui/glyph_slot.png", onPress);
        this.slotNum = slotNum;
        this.spell_id = "";
        this.resourceIcon = "";
        this.parent = parent;
    }

    @Override
    public void render(int parX, int parY, float partialTicks) {
        if (visible)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            //GuiSpellBook.drawFromTexture(new ResourceLocation(ExampleMod.MODID, this.resourceIcon), x, y, 0, 0, 20, 20, 20, 20);
            if(!this.resourceIcon.equals("")){
                GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/spells/" + resourceIcon), x + 2, y + 2, u, v, 16, 16, 16, 16);
            }
            if(parent.isMouseInRelativeRange(parX, parY, x, y, width, height)){

                if(parent.api.getSpell_map().containsKey(this.spell_id)) {
                    List<String> test = new ArrayList<>();
                    test.add(parent.api.getSpell_map().get(this.spell_id).description);
                    parent.tooltip = test;
                }
            }
        }
        super.render(parX, parY, partialTicks);
    }
}
