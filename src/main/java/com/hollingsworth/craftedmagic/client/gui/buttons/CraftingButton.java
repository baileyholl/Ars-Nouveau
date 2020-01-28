package com.hollingsworth.craftedmagic.client.gui.buttons;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CraftingButton extends GuiImageButton{
    int slotNum;
    public String spell_id;
    public String resourceIcon;

    public CraftingButton(int x, int y, int slotNum, Button.IPressable onPress) {
        super(x, y, 0, 0, 20, 20, 20, 20, "textures/gui/glyph_slot.png", onPress);
        this.slotNum = slotNum;
        this.spell_id = "";
        this.resourceIcon = "";
    }

    @Override
    public void render(int parX, int parY, float partialTicks) {
        if (visible)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            //GuiSpellBook.drawFromTexture(new ResourceLocation(ExampleMod.MODID, this.resourceIcon), x, y, 0, 0, 20, 20, 20, 20);
            if(!this.resourceIcon.equals("")){
                GuiSpellBook.drawFromTexture(new ResourceLocation(ExampleMod.MODID, "textures/gui/spells/" + resourceIcon), x + 2, y + 2, u, v, 16, 16, 16, 16);
            }
        }
        super.render(parX, parY, partialTicks);
    }
}
