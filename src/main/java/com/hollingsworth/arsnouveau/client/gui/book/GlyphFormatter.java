package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class GlyphFormatter {
    int perRow = 7;
    int maxRows = 7;

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

    static {
        CATEGORIES.add(new Category(ArsNouveau.prefix("form"), p -> p instanceof AbstractCastMethod, Component.translatable("ars_nouveau.form_icon_tooltip"), DocAssets.FORM_ICON_CRAFTING));
        CATEGORIES.add(new Category(ArsNouveau.prefix("effect"), p -> p instanceof AbstractEffect, Component.translatable("ars_nouveau.effect_icon_tooltip"), DocAssets.EFFECT_ICON_CRAFTING));
        CATEGORIES.add(new Category(ArsNouveau.prefix("augment"), p -> p instanceof AbstractAugment, Component.translatable("ars_nouveau.augment_icon_tooltip"), DocAssets.AUGMENT_ICON_CRAFTING));
    }

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
            part.x = bookLeft + 16 + (isNextPage ? 134 : 0) + (count % perRow) * 18;
            part.y = numRows * 18 + bookTop + 18;
            addRenderableWidget.accept(part);
            if (part instanceof GlyphButton glyphButton) {
                glyphButtons.add(glyphButton);
            }
            addedWidgets.add(part);
            count++;
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
