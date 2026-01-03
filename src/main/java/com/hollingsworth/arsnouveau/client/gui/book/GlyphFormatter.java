package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.gui.buttons.ANButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class GlyphFormatter {
    int perRow = 7;
    int maxRows = 7;
    public static int MAX_PER_PAGE = 98;

    int bookLeft;
    int bookTop;
    Button.OnPress onGlyphClick;

    public List<GlyphButton> glyphButtons = new ArrayList<>();
    List<AbstractWidget> addedWidgets = new ArrayList<>();
    Consumer<List<? extends AbstractWidget>> clearButtons;
    Consumer<AbstractWidget> addRenderableWidget;
    List<ANButton> sortedWidgets = new ArrayList<>();
    public static List<Category> CATEGORIES = new ArrayList<>();

    Set<ResourceLocation> addedCategories = new HashSet<>();

    List<List<ANButton>> pages = new ArrayList<>();

    static {
        CATEGORIES.add(new Category(ArsNouveau.prefix("form"), p -> p instanceof AbstractCastMethod, Component.translatable("ars_nouveau.form_icon_tooltip"), DocAssets.FORM_ICON_CRAFTING));
        CATEGORIES.add(new Category(ArsNouveau.prefix("effect"), p -> p instanceof AbstractEffect && !(p instanceof AbstractFilter), Component.translatable("ars_nouveau.effect_icon_tooltip"), DocAssets.EFFECT_ICON_CRAFTING));
        CATEGORIES.add(new Category(ArsNouveau.prefix("augment"), p -> p instanceof AbstractAugment, Component.translatable("ars_nouveau.augment_icon_tooltip"), DocAssets.AUGMENT_ICON_CRAFTING));
        CATEGORIES.add(new Category(ArsNouveau.prefix("filter"), p -> p instanceof IFilter, Component.translatable("ars_nouveau.filter_icon_tooltip"), DocAssets.FILTER_ICON_CRAFTING));
    }

    public GlyphFormatter(int bookLeft, int bookTop, Button.OnPress onGlyphClick, List<AbstractSpellPart> glyphs, Consumer<List<? extends AbstractWidget>> clearButtons, Consumer<AbstractWidget> addRenderableWidget) {
        this.bookLeft = bookLeft;
        this.bookTop = bookTop;
        this.onGlyphClick = onGlyphClick;
        this.clearButtons = clearButtons;
        this.addRenderableWidget = addRenderableWidget;
        buildPages(glyphs);
    }

    public void buildPages(List<AbstractSpellPart> glyphs) {
        pages.clear();
        int count = 0;
        List<ANButton> currentPage = new ArrayList<>();
        pages.add(currentPage);
        buildSortedWidgets(glyphs);
        for (ANButton part : sortedWidgets) {
            if (count >= MAX_PER_PAGE) {
                currentPage = new ArrayList<>();
                pages.add(currentPage);
                count = 0;
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
            part.x = bookLeft + 16 + (isNextPage ? 134 : 0) + (count % perRow) * 18;
            part.y = numRows * 18 + bookTop + 18;
            currentPage.add(part);
            count++;
        }
    }

    private void buildSortedWidgets(List<AbstractSpellPart> displayedGlyphs) {
        List<AbstractSpellPart> sorted = new ArrayList<>(displayedGlyphs);
        sorted.sort(Comparator.comparingInt((AbstractSpellPart p) -> switch (p) {
            case AbstractAugment ignored -> 3;
            default -> p.getTypeIndex();
        }).thenComparing(AbstractSpellPart::getLocaleName));
        List<ANButton> buttons = new ArrayList<>();
        addedCategories = new HashSet<>();

        for (AbstractSpellPart abstractSpellPart : sorted) {
            for (Category category : CATEGORIES) {
                if (!addedCategories.contains(category.id) && category.filter.test(abstractSpellPart)) {
                    addedCategories.add(category.id);
                    buttons.add(new GuiImageButton(0, 0, category.blitInfo(), (b) -> {
                    }).withTooltip(category.tooltip));
                }
            }

            buttons.add(new GlyphButton(0, 0, abstractSpellPart, this.onGlyphClick));
        }
        sortedWidgets = new ArrayList<>(buttons);
    }


    public void layoutAllGlyphs(int page) {
        clearButtons.accept(addedWidgets);
        List<ANButton> buttons = pages.get(page);
        for (ANButton part : buttons) {
            addRenderableWidget.accept(part);
            if (part instanceof GlyphButton glyphButton) {
                glyphButtons.add(glyphButton);
            }
            addedWidgets.add(part);
        }
    }

    public record Category(ResourceLocation id, Predicate<AbstractSpellPart> filter, Component tooltip,
                           DocAssets.BlitInfo blitInfo) {


        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Category category = (Category) o;
            return Objects.equals(id, category.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }
}
