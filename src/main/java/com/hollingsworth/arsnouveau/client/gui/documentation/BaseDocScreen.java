package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocPlayerData;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.nuggets.client.gui.BaseScreen;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
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

    public static ResourceLocation background = ArsNouveau.prefix("textures/gui/spell_book_template.png");

    public NuggetImageButton leftArrow;

    public NuggetImageButton rightArrow;
    public NuggetImageButton backButton;

    public int arrowIndex;
    public int maxArrowIndex;

    public BaseDocScreen previousScreen = null;
    SoundManager manager = Minecraft.getInstance().getSoundManager();

    List<AbstractWidget> bookmarkButtons = new ArrayList<>();

    public BaseDocScreen() {
        super(Component.empty(), 290, 194, background);
    }

    @Override
    public void init() {
        super.init();
        backButton = new NuggetImageButton(bookLeft + 6, bookTop + 10, 14, 8, DocAssets.ARROW_BACK.location(), DocAssets.ARROW_BACK_HOVER.location(), (b) -> {
            goBack();
        });
        addRenderableWidget(backButton);
        rightArrow = new NuggetImageButton(bookRight - 13, bookTop + 88, 11, 14, DocAssets.ARROW_RIGHT.location(), DocAssets.ARROW_RIGHT_HOVER.location(), this::onRightArrowClick);
        leftArrow = new NuggetImageButton(bookLeft + 1, bookTop + 88, 11, 14, DocAssets.ARROW_LEFT.location(), DocAssets.ARROW_LEFT_HOVER.location(), this::onLeftArrowClick);
        addRenderableWidget(leftArrow);
        addRenderableWidget(rightArrow);

        if(!showLeftArrow()){
            leftArrow.visible = false;
        }

        if(!showRightArrow()){
            rightArrow.visible = false;
        }
        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 142, 0, 0, 23, 20, 23, 20, "textures/gui/discord_tab.png", (b) -> {
            try {
                Util.getPlatform().openUri(new URI("https://discord.com/invite/y7TMXZu"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }).withTooltip(Component.translatable("ars_nouveau.gui.discord")));
        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 142, 0, 0, 23, 20, 23, 20, "textures/gui/discord_tab.png", (b) -> {
            try {
                Util.getPlatform().openUri(new URI("https://discord.com/invite/y7TMXZu"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }).withTooltip(Component.translatable("ars_nouveau.gui.discord")));
        backButton.visible = previousScreen != null;
        initBookmarks();
    }

    public void initBookmarks(){
        for(AbstractWidget button : bookmarkButtons){
            removeWidget(button);
        }
        bookmarkButtons.clear();

        List<ResourceLocation> bookmarks = DocPlayerData.bookmarks;
        for (int i = 0; i < bookmarks.size(); i++) {
            ResourceLocation entryId = bookmarks.get(i);
            DocEntry entry = DocumentationRegistry.getEntry(entryId);

            BookmarkButton slot = addRenderableWidget(new BookmarkButton(bookLeft + 281, bookTop + 1 + 15 * (i + 1), entry, (b) ->{
                if(entry == null) return;
                boolean isShiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), Minecraft.getInstance().options.keyShift.getKey().getValue());
                if(isShiftDown){
                    bookmarks.remove(entryId);
                    initBookmarks();
                }else {
                    PageHolderScreen pageHolderScreen = new PageHolderScreen(entry);
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
        if(button == 1){
            goBack();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void transition(BaseDocScreen screen){
        SoundManager manager = Minecraft.getInstance().getSoundManager();
        screen.previousScreen = this;
        manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        Minecraft.getInstance().setScreen(screen);
    }

    public void goBack(){
        if(previousScreen != null){
            Minecraft.getInstance().setScreen(previousScreen);
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        DocPlayerData.previousScreen = this;
    }

    public boolean showLeftArrow(){
        return this.arrowIndex > 0;
    }

    public boolean showRightArrow(){
        return this.arrowIndex < this.maxArrowIndex;
    }

    public void onLeftArrowClick(Button button){
        this.arrowIndex--;
        this.onArrowIndexChange();
    }

    public void onRightArrowClick(Button button){
        this.arrowIndex++;
        this.onArrowIndexChange();
    }

    public void onArrowIndexChange(){
        leftArrow.visible = showLeftArrow();
        rightArrow.visible = showRightArrow();
    }

    public RecipeManager recipeManager(){
        Level level = ArsNouveau.proxy.getClientWorld();
        return level.getRecipeManager();
    }
}
