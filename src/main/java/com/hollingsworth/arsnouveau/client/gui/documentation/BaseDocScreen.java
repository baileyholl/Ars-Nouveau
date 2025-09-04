package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocPlayerData;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.client.gui.SearchBar;
import com.hollingsworth.nuggets.client.gui.BaseButton;
import com.hollingsworth.nuggets.client.gui.BaseScreen;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class BaseDocScreen extends BaseScreen {

    public NuggetImageButton leftArrow;

    public NuggetImageButton rightArrow;
    public BaseButton backButton;

    public int arrowIndex;
    public int maxArrowIndex;
    public SearchBar searchBar;
    public String previousString = "";
    public BaseDocScreen previousScreen = null;
    SoundManager manager = Minecraft.getInstance().getSoundManager();

    List<AbstractWidget> bookmarkButtons = new ArrayList<>();
    public static final int LEFT_PAGE_OFFSET = 19;
    public static final int RIGHT_PAGE_OFFSET = 153;
    public static final int PAGE_TOP_OFFSET = 17;
    public static final int ONE_PAGE_WIDTH = 118;
    public static final int ONE_PAGE_HEIGHT = 146;

    public BaseDocScreen() {
        super(Component.empty(), DocAssets.BACKGROUND.width(), DocAssets.BACKGROUND.height(), DocAssets.BACKGROUND.location());
    }

    @Override
    public void init() {
        super.init();
        searchBar = new SearchBar(minecraft.font, screenRight - 130, screenTop - 3);
        searchBar.setResponder(this::onSearchChanged);
        addRenderableWidget(searchBar);
        backButton = new NuggetImageButton(screenLeft + 6, screenTop + 6, DocAssets.ARROW_BACK_HOVER.width(), DocAssets.ARROW_BACK_HOVER.height(), DocAssets.ARROW_BACK.location(), DocAssets.ARROW_BACK_HOVER.location(), (b) -> {
            if (isShiftDown()) {
                var home = new IndexScreen();
                transition(home);
                home.previousScreen = null;
                home.backButton.visible = false;
            } else {
                goBack();
            }
        }).withTooltip(Component.translatable("ars_nouveau.shift_back"));
        addRenderableWidget(backButton);

        int nextPageYOffset = screenBottom - 20;
        rightArrow = new NuggetImageButton(screenRight - DocAssets.ARROW_RIGHT.width() - 1, nextPageYOffset, DocAssets.ARROW_RIGHT.width(), DocAssets.ARROW_RIGHT.height(), DocAssets.ARROW_RIGHT.location(), DocAssets.ARROW_RIGHT_HOVER.location(), this::onRightArrowClick);
        leftArrow = new NuggetImageButton(screenLeft + 1, nextPageYOffset, DocAssets.ARROW_RIGHT.width(), DocAssets.ARROW_RIGHT.height(), DocAssets.ARROW_LEFT.location(), DocAssets.ARROW_LEFT_HOVER.location(), this::onLeftArrowClick);

        addRenderableWidget(leftArrow);
        addRenderableWidget(rightArrow);

        if (!showLeftArrow()) {
            leftArrow.visible = false;
        }

        if (!showRightArrow()) {
            rightArrow.visible = false;
        }
        addRenderableWidget(new NuggetImageButton(screenLeft - 15, screenTop + 140, 0, 0, 23, 20, 23, 20, ArsNouveau.prefix("textures/gui/discord_tab.png"), (b) -> {
            try {
                Util.getPlatform().openUri(new URI("https://discord.com/invite/y7TMXZu"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }).withTooltip(Component.translatable("ars_nouveau.gui.discord")));
        if (previousScreen == null && !(this instanceof IndexScreen)) {
            previousScreen = new IndexScreen();
        }
        backButton.visible = previousScreen != null;
        initBookmarks();
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
    }

    public void onSearchChanged(String str) {
        if (str.equals(previousString))
            return;
        previousString = str;
        if (!str.isEmpty()) {
            transition(new SearchScreen(str));
        }
    }

    public boolean isShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), Minecraft.getInstance().options.keyShift.getKey().getValue());
    }

    public void initBookmarks() {
        for (AbstractWidget button : bookmarkButtons) {
            removeWidget(button);
        }
        bookmarkButtons.clear();

        List<ResourceLocation> bookmarks = DocPlayerData.bookmarks;
        for (int i = 0; i < bookmarks.size(); i++) {
            ResourceLocation entryId = bookmarks.get(i);
            DocEntry entry = DocumentationRegistry.getEntry(entryId);

            BookmarkButton slot = addRenderableWidget(new BookmarkButton(screenLeft + 281, screenTop + 1 + 15 * (i + 1), entry, (b) -> {
                if (entry == null) return;
                if (isShiftDown()) {
                    bookmarks.remove(entryId);
                    initBookmarks();
                } else {
                    PageHolderScreen pageHolderScreen = new PageHolderScreen(entry);
                    // Prevent bookmarks from transitioning to the same screen
                    if (Minecraft.getInstance().screen instanceof PageHolderScreen newPageHolder && newPageHolder.entry == entry) {
                        return;
                    }
                    transition(pageHolderScreen);
                }
            }));
            this.bookmarkButtons.add(slot);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY < 0 && rightArrow.visible) {
            onRightArrowClick(rightArrow);
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        } else if (scrollY > 0 && leftArrow.visible) {
            onLeftArrowClick(leftArrow);
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!searchBar.isHovered()) {
            if (button == 1) {
                goBack();
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (!searchBar.isFocused()) {
            searchBar.setFocused(true);
        }
        if (searchBar.charTyped(c, i)) {
            return true;
        }

        return super.charTyped(c, i);
    }

    public void transition(BaseDocScreen screen) {
        SoundManager manager = Minecraft.getInstance().getSoundManager();
        screen.previousScreen = this;
        manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        Minecraft.getInstance().setScreen(screen);
    }

    public void goBack() {
        if (previousScreen != null) {
            Minecraft.getInstance().setScreen(previousScreen);
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        DocPlayerData.previousScreen = this;
    }

    public boolean showLeftArrow() {
        return this.arrowIndex > 0;
    }

    public boolean showRightArrow() {
        return this.arrowIndex < this.maxArrowIndex;
    }

    public void onLeftArrowClick(Button button) {
        this.arrowIndex--;
        this.onArrowIndexChange();
    }

    public void onRightArrowClick(Button button) {
        this.arrowIndex++;
        this.onArrowIndexChange();
    }

    public void onArrowIndexChange() {
        leftArrow.visible = showLeftArrow();
        rightArrow.visible = showRightArrow();
    }

    public RecipeManager recipeManager() {
        Level level = ArsNouveau.proxy.getClientWorld();
        return level.getRecipeManager();
    }

    public void setMinecraft(Minecraft minecraft) {
        this.minecraft = minecraft;
    }
}
