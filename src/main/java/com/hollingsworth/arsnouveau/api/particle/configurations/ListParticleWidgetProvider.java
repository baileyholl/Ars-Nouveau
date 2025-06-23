package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.client.gui.SearchBar;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.documentation.DocEntryButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ListParticleWidgetProvider extends ParticleConfigWidgetProvider {

    public List<DocEntryButton> buttons;
    public GuiImageButton upButton;
    public GuiImageButton downButton;
    public int pageOffset;
    public int maxEntries;
    public int numPerPage = 8;
    public SearchBar searchBar;
    public String previousSearch = "";
    private Supplier<Map<String, Object>> preservedData;

    public ListParticleWidgetProvider(int x, int y, int width, int height, List<DocEntryButton> buttons, int numPerPage, Supplier<Map<String, Object>> providerData) {
        super(x, y, width, height);
        this.buttons = buttons;
        maxEntries = buttons.size();
        this.numPerPage = numPerPage;
        this.preservedData = providerData;
        Map<String, Object> data = providerData.get();
        if (!data.isEmpty()) {
            if (data.containsKey("search")) {
                previousSearch = (String) data.get("search");
            }
            if (data.containsKey("pageOffset")) {
                pageOffset = (int) data.get("pageOffset");
            }
            if (data.containsKey("maxEntries")) {
                maxEntries = (int) data.get("maxEntries");
            }
        }
    }

    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        SoundManager manager = Minecraft.getInstance().getSoundManager();
        if (pScrollY < 0) {
            onScroll(1);
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        } else if (pScrollY > 0) {
            onScroll(-1);
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }

        return true;
    }


    public void onScroll(int offset) {
        pageOffset += numPerPage * offset;
        pageOffset = Math.max(0, Math.min(pageOffset, maxEntries - numPerPage));
        preservedData.get().put("pageOffset", pageOffset);
        updateButtons();
    }

    public List<DocEntryButton> filter(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return buttons;
        }
        String lowerFilter = filterText.toLowerCase(Locale.ROOT);
        return buttons.stream()
                .filter(button -> button.title.getString().toLowerCase(Locale.ROOT).contains(lowerFilter))
                .toList();
    }


    protected void updateButtons() {
        List<DocEntryButton> filteredButtons = filter(searchBar.value);
        maxEntries = filteredButtons.size();
        preservedData.get().put("maxEntries", maxEntries);
        for (var button : buttons) {
            button.active = false;
            button.visible = false;
            button.setPosition(-100, -100);
        }
        var sublist = filteredButtons.subList(pageOffset, Math.min(filteredButtons.size(), pageOffset + numPerPage));
        for (int i = 0; i < sublist.size(); i++) {
            int x = this.x;
            int y = this.y + 20 + 15 * (i % 8);
            Button button = sublist.get(i);
            button.visible = true;
            button.active = true;
            button.setPosition(x, y);
        }

        boolean hasMore = pageOffset + numPerPage < maxEntries;
        boolean hasFewer = pageOffset > 0;
        upButton.visible = hasFewer;
        upButton.active = hasFewer;
        downButton.active = hasMore;
        downButton.visible = hasMore;
    }

    public void onSearchChanged(String search) {
        if (search.equals(previousSearch)) {
            return;
        }
        pageOffset = 0;
        previousSearch = search;
        preservedData.get().put("search", search);
        updateButtons();
    }

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    public void addWidgets(List<AbstractWidget> widgets) {
        searchBar = new SearchBar(Minecraft.getInstance().font, x + 11, y - 19);
        if (previousSearch != null) {
            searchBar.setValue(previousSearch);
        }
        searchBar.setResponder(this::onSearchChanged);

        for (Button button : buttons) {
            widgets.add(button);
        }
        int arrowY = y + height - 5 - (8 - numPerPage) * 15;
        upButton = new GuiImageButton(x + 80, arrowY, DocAssets.BUTTON_UP, (button) -> {
            onScroll(-1);
        }).withHoverImage(DocAssets.BUTTON_UP_HOVER);

        downButton = new GuiImageButton(x + 100, arrowY, DocAssets.BUTTON_DOWN, (button) -> {
            onScroll(1);
        }).withHoverImage(DocAssets.BUTTON_DOWN_HOVER);

        widgets.add(upButton);
        widgets.add(downButton);
        if (buttons.size() > numPerPage) {
            widgets.add(searchBar);
        }
        updateButtons();
    }
}
