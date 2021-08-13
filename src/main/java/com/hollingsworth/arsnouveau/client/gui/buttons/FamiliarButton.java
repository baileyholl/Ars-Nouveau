package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliar;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.book.GuiFamiliarScreen;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class FamiliarButton extends Button {

    public boolean isCraftingSlot;
    public String resourceIcon;
    public String spell_id; //Reference to a spell ID for spell crafting
    public String tooltip = "tooltip";
    public List<SpellValidationError> validationErrors;

    GuiFamiliarScreen parent;

    public FamiliarButton(GuiFamiliarScreen parent, int x, int y, boolean isCraftingSlot, AbstractFamiliar familiar) {
        super(x, y,  16, 16, ITextComponent.nullToEmpty(""), parent::onGlyphClick);
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.isCraftingSlot = isCraftingSlot;
        this.resourceIcon = familiar.id + ".png";
    }


    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        if (visible)
        {
            if(this.resourceIcon != null && !this.resourceIcon.equals("")) {
                GL11.glEnable(GL11.GL_BLEND);
                if (validationErrors.isEmpty()) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.25F);
                }

                GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + this.resourceIcon), x, y, 0, 0, 16, 16,16,16 , ms);
                GL11.glDisable(GL11.GL_BLEND);
            }

            if(parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height)){
                if(parent.api.getSpell_map().containsKey(this.spell_id)) {
                    List<ITextComponent> tip = new ArrayList<>();
//                    AbstractSpellPart spellPart = parent.api.getSpell_map().get(this.spell_id);
//                    tip.add(new TranslationTextComponent(spellPart.getLocalizationKey()));
//                    for (SpellValidationError ve : validationErrors) {
//                        tip.add(ve.makeTextComponentAdding().withStyle(TextFormatting.RED));
//                    }
                    if(Screen.hasShiftDown()){
                        tip.add(new TranslationTextComponent(""));
                    }else{
                        tip.add(new TranslationTextComponent("tooltip.ars_nouveau.hold_shift"));
                    }

                    parent.tooltip = tip;
                }
            }

        }
        //super.render(mouseX, mouseY, partialTicks);
    }

}