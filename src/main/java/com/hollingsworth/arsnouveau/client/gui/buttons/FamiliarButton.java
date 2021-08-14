package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.client.gui.book.GuiFamiliarScreen;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class FamiliarButton extends Button {

    public boolean isCraftingSlot;
    public String resourceIcon;
    public String spell_id; //Reference to a spell ID for spell crafting
    public String tooltip = "tooltip";

    GuiFamiliarScreen parent;
    AbstractFamiliarHolder familiarHolder;
    public FamiliarButton(GuiFamiliarScreen parent, int x, int y, boolean isCraftingSlot, AbstractFamiliarHolder familiar) {
        super(x, y,  16, 16, ITextComponent.nullToEmpty(""), parent::onGlyphClick);
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.isCraftingSlot = isCraftingSlot;
        this.resourceIcon = familiar.getImagePath();
        this.familiarHolder = familiar;
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
                GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + this.resourceIcon), x, y, 0, 0, 16, 16,16,16 , ms);
            }

            if(parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height)){

                List<ITextComponent> tip = new ArrayList<>();
//                    AbstractSpellPart spellPart = parent.api.getSpell_map().get(this.spell_id);
//                    tip.add(new TranslationTextComponent(spellPart.getLocalizationKey()));
//                    for (SpellValidationError ve : validationErrors) {
//                        tip.add(ve.makeTextComponentAdding().withStyle(TextFormatting.RED));
//                    }
                if(Screen.hasShiftDown()){
                    tip.add(familiarHolder.getDescription());
                }else{
                    tip.add(new TranslationTextComponent("tooltip.ars_nouveau.hold_shift"));
                }

                parent.tooltip = tip;

            }

        }
        //super.render(mouseX, mouseY, partialTicks);
    }

}