package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.buttons.ANButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class GlyphFormatter {
    int perRow = 6;
    int maxRows = 7;

    int bookLeft;
    int bookTop;
    Button.OnPress onGlyphClick;

    public List<GlyphButton> glyphButtons = new ArrayList<>();
    List<AbstractWidget> addedWidgets = new ArrayList<>();
    Consumer<List<? extends AbstractWidget>> clearButtons;
    Consumer<AbstractWidget> addRenderableWidget;
    List<ANButton> sortedWidgets = new ArrayList<>();

    public GlyphFormatter(int bookLeft, int bookTop, Button.OnPress onGlyphClick, Consumer<List<? extends AbstractWidget>> clearButtons, Consumer<AbstractWidget> addRenderableWidget) {
        this.bookLeft = bookLeft;
        this.bookTop = bookTop;
        this.onGlyphClick = onGlyphClick;
        this.clearButtons = clearButtons;
        this.addRenderableWidget = addRenderableWidget;

    }

    private void buildSortedWidgets(List<AbstractSpellPart> displayedGlyphs) {
        List<AbstractSpellPart> sorted = new ArrayList<>(displayedGlyphs);
        sorted.sort(Comparator.comparingInt((AbstractSpellPart p) -> switch (p) {
            case AbstractAugment ignored -> 3;
            default -> p.getTypeIndex();
        }).thenComparing(AbstractSpellPart::getLocaleName));
        List<ANButton> buttons = new ArrayList<>();
        boolean addedForms = false;
        boolean addedAugments = false;
        boolean addedEffects = false;

        for (int i = 0; i < sorted.size(); i++) {
            if (!addedForms && sorted.get(i) instanceof AbstractCastMethod) {
                addedForms = true;
                buttons.add(new GuiImageButton(0, 0, DocAssets.FORM_ICON, (b) -> {
                }));
            } else if (!addedEffects && sorted.get(i) instanceof AbstractEffect) {
                addedEffects = true;
                buttons.add(new GuiImageButton(0, 0, DocAssets.EFFECT_ICON, (b) -> {
                }));
            } else if (!addedAugments && sorted.get(i) instanceof AbstractAugment) {
                addedAugments = true;
                buttons.add(new GuiImageButton(0, 0, DocAssets.AUGMENT_ICON, (b) -> {
                }));
            }

            buttons.add(new GlyphButton(0, 0, sorted.get(i), this.onGlyphClick));
        }
        sortedWidgets = new ArrayList<>(buttons);
    }


    public void layoutAllGlyphs(int page, List<AbstractSpellPart> displayedGlyphs) {
        clearButtons.accept(addedWidgets);
        int fromIndex = 84 * page;

        if (page == 0) {
            buildSortedWidgets(displayedGlyphs);
        }

        List<ANButton> buttons = new ArrayList<>(sortedWidgets);

        if (fromIndex < buttons.size()) {
            buttons = buttons.subList(fromIndex, buttons.size());
        }
        int count = 0;
        for (ANButton part : buttons) {
            if (count > 84) {
                break;
            }
            if (count != 0 && !(part instanceof GlyphButton) && (count % perRow) != 0) {
                // Space an entire row plus the rest of the current row
                count += (perRow - (count % perRow));
            }
            boolean isNextPage = count >= (perRow * maxRows);
            int numRows = count / perRow;
            if (isNextPage) {
                numRows = (count - (perRow * maxRows)) / perRow;
            }
            part.x = bookLeft + 20 + (isNextPage ? 134 : 0) + (count % perRow) * 20;
            part.y = numRows * 18 + bookTop + 20;
            addRenderableWidget.accept(part);
            if (part instanceof GlyphButton glyphButton) {
                glyphButtons.add(glyphButton);
            }
            addedWidgets.add(part);
            count++;
        }
    }
}
