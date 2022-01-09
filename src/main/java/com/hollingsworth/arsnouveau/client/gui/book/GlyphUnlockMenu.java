package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

public class GlyphUnlockMenu extends BaseBook{


    public List<AbstractSpellPart> displayedGlyphs = new ArrayList<>();
    public List<AbstractSpellPart> allParts = new ArrayList<>();
    public int page = 0;
    public PageButton nextButton;
    public PageButton previousButton;
    public List<GlyphButton> glyphButtons = new ArrayList<>();
    public NoShadowTextField searchBar;
    public String previousString = "";
    ArsNouveauAPI api = ArsNouveauAPI.getInstance();
    int maxPerPage = 58;

    public GlyphUnlockMenu(){
        super();
        allParts = new ArrayList<>(ArsNouveauAPI.getInstance().getSpellpartMap().values());

    }

    @Override
    public void init() {
        super.init();
        searchBar = new NoShadowTextField(minecraft.font, bookRight - 73, bookTop +2,
                54, 12, null, new TranslatableComponent("ars_nouveau.spell_book_gui.search"));
        searchBar.setBordered(false);
        searchBar.setTextColor(12694931);
        searchBar.onClear = (val) -> {
            this.onSearchChanged("");
            return null;
        };
        if(searchBar.getValue().isEmpty())
            searchBar.setSuggestion(new TranslatableComponent("ars_nouveau.spell_book_gui.search").getString());
        searchBar.setResponder(this::onSearchChanged);
        addRenderableWidget(searchBar);

        this.nextButton = addRenderableWidget(new PageButton(bookRight -20, bookBottom -10, true, this::onPageIncrease, true));
        this.previousButton = addRenderableWidget(new PageButton(bookLeft - 5 , bookBottom -10, false, this::onPageDec, true));
        updateNextPageButtons();
        previousButton.active = false;
        previousButton.visible = false;
    }


    public void updateNextPageButtons(){
        if(displayedGlyphs.size() < maxPerPage){
            nextButton.visible = false;
            nextButton.active = false;
        }else{
            nextButton.visible = true;
            nextButton.active = true;
        }
    }

    public void onSearchChanged(String str){
        if(str.equals(previousString))
            return;
        previousString = str;

        if (!str.isEmpty()) {
            searchBar.setSuggestion("");
            displayedGlyphs = new ArrayList<>();

            for (AbstractSpellPart spellPart : allParts) {
                if (spellPart.getLocaleName().toLowerCase().contains(str.toLowerCase())) {
                    displayedGlyphs.add(spellPart);
                }
            }
            // Set visibility of Cast Methods and Augments
            for(Widget w : renderables) {
                if(w instanceof GlyphButton glyphButton) {
                    if (glyphButton.spell_id != null) {
                        AbstractSpellPart part = api.getSpellpartMap().get(glyphButton.spell_id);
                        if (part != null) {
                            glyphButton.visible = part.getLocaleName().toLowerCase().contains(str.toLowerCase());
                        }
                    }
                }
            }
        } else {
            // Reset our book on clear
            searchBar.setSuggestion(new TranslatableComponent("ars_nouveau.spell_book_gui.search").getString());
            displayedGlyphs = allParts;
            for(Widget w : renderables){
                if(w instanceof GlyphButton ) {
                    ((GlyphButton) w).visible = true;
                }
            }
        }
        resetPageState();
    }

    public void resetPageState(){
        updateNextPageButtons();
        this.page = 0;
        previousButton.active = false;
        previousButton.visible = false;
        layoutAllGlyphs(page);
    }

    public void layoutAllGlyphs(int page){
    }


    public void clearButtons( List<GlyphButton> glyphButtons){
        for (GlyphButton b : glyphButtons) {
            renderables.remove(b);
            children().remove(b);
        }
        glyphButtons.clear();
    }

    public void onPageIncrease(Button button){
        page++;
        if(displayedGlyphs.size() < maxPerPage * (page + 1)){
            nextButton.visible = false;
            nextButton.active = false;
        }
        previousButton.active = true;
        previousButton.visible = true;
        layoutAllGlyphs(page);
    }

    public void onPageDec(Button button){
        page--;
        if(page == 0){
            previousButton.active = false;
            previousButton.visible = false;
        }

        if(displayedGlyphs.size() > maxPerPage * (page + 1)){
            nextButton.visible = true;
            nextButton.active = true;
        }
        layoutAllGlyphs(page);
    }
}
