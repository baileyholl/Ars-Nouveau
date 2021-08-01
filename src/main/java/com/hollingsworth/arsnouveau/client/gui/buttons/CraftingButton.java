package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public class CraftingButton extends GuiImageButton{
    int slotNum;
    public String spellTag;
    public String resourceIcon;
    public List<SpellValidationError> validationErrors;

    public CraftingButton(GuiSpellBook parent, int x, int y, int slotNum, Button.IPressable onPress) {
        super( x, y, 0, 0, 22, 20, 22, 20, "textures/gui/spell_glyph_slot.png", onPress);
        this.slotNum = slotNum;
        this.spellTag = "";
        this.resourceIcon = "";
        this.validationErrors = new LinkedList<>();
        this.parent = parent;
    }

    public void clear() {
        this.spellTag = "";
        this.resourceIcon = "";
        this.validationErrors.clear();
    }

    @Override
    public void render(MatrixStack ms, int parX, int parY, float partialTicks) {
        if (visible)
        {
            if (validationErrors.isEmpty()) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                GL11.glColor4f(1.0F, 0.7F, 0.7F, 1.0F);
            }
            //GuiSpellBook.drawFromTexture(new ResourceLocation(ExampleMod.MODID, this.resourceIcon), x, y, 0, 0, 20, 20, 20, 20);
            if(!this.resourceIcon.equals("")){
                GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + resourceIcon), x + 3, y + 2, u, v, 16, 16, 16, 16,ms);
            }
            if(parent.isMouseInRelativeRange(parX, parY, x, y, width, height)){

                if(parent.api.getSpell_map().containsKey(this.spellTag)) {
                    List<ITextComponent> tooltip = new LinkedList<>();
                    tooltip.add(new TranslationTextComponent(parent.api.getSpell_map().get(this.spellTag).getLocalizationKey()));
                    for (SpellValidationError ve : validationErrors) {
                        tooltip.add(ve.makeTextComponentExisting().withStyle(TextFormatting.RED));
                    }
                    parent.tooltip = tooltip;
                }
            }
        }
        super.render(ms, parX, parY, partialTicks);
    }
}
