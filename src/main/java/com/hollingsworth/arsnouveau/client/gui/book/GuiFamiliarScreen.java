package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliar;
import com.hollingsworth.arsnouveau.client.gui.buttons.FamiliarButton;
import net.minecraft.client.gui.widget.button.Button;

import java.util.List;

public class GuiFamiliarScreen extends BaseBook{
    public ArsNouveauAPI api;
    public List<AbstractFamiliar> familiars;
    public GuiFamiliarScreen(ArsNouveauAPI api, List<AbstractFamiliar> familiars){
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
            AbstractFamiliar part = familiars.get(i);
            int xOffset = 20 * (i % PER_ROW);
            int yOffset = (i / PER_ROW) * 18;
            FamiliarButton cell = new FamiliarButton(this, xStart + xOffset, yStart + yOffset, false, part);
            addButton(cell);
        }
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
