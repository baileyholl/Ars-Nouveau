package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.client.gui.buttons.FamiliarButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class GuiFamiliarScreen extends BaseBook{
    public ArsNouveauAPI api;
    public List<AbstractFamiliarHolder> familiars;
    public GuiFamiliarScreen(ArsNouveauAPI api, List<AbstractFamiliarHolder> familiars){
        this.api = api;
        this.familiars = familiars;
    }

    @Override
    public void init() {
        super.init();
        layoutParts();
    }

    public void layoutParts(){
        int xStart = bookLeft + 20;
        int yStart = bookTop + 34;
        final int PER_ROW = 6;
        int toLayout = Math.min(familiars.size(), PER_ROW * 5);
        for (int i = 0; i < toLayout; i++) {
            AbstractFamiliarHolder part = familiars.get(i);
            int xOffset = 20 * (i % PER_ROW);
            int yOffset = (i / PER_ROW) * 18;
            FamiliarButton cell = new FamiliarButton(this, xStart + xOffset, yStart + yOffset, false, part);
            addButton(cell);
        }
    }

    @Override
    public void drawBackgroundElements(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/clear_paper.png"), 161, 179, 0, 0, 47, 15,47,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_paper.png"), 216, 179, 0, 0, 56, 15,56,15, stack);
        minecraft.font.draw(stack,new TranslationTextComponent("ars_nouveau.spell_book_gui.familiar").getString(), 20, 24, -8355712);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.spell_book_gui.cancel"), 233, 183, -8355712);
        minecraft.font.draw(stack,new TranslationTextComponent("ars_nouveau.spell_book_gui.clear").getString(), 177, 183, -8355712);
    }

    public void onGlyphClick(Button button){
        FamiliarButton button1 = (FamiliarButton) button;

//        if (button1.validationErrors.isEmpty()) {
//            for (CraftingButton b : craftingCells) {
//                if (b.resourceIcon.equals("")) {
//                    b.resourceIcon = button1.resourceIcon;
//                    b.spellTag = button1.spell_id;
//                    validate();
//                    return;
//                }
//            }
//        }
    }
}
