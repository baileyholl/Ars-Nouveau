package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.nuggets.client.gui.BaseScreen;
import com.hollingsworth.nuggets.client.gui.NuggetImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class BaseDocScreen extends BaseScreen {

    public static ResourceLocation background = ArsNouveau.prefix("textures/gui/spell_book_template.png");

    public static ResourceLocation PAGE_LEFT = ArsNouveau.prefix("textures/gui/documentation/doc_button_next_page_left.png");
    public static ResourceLocation PAGE_RIGHT = ArsNouveau.prefix("textures/gui/documentation/doc_button_next_page_right.png");

    public NuggetImageButton leftArrow;

    public NuggetImageButton rightArrow;

    public int arrowIndex;
    public int maxArrowIndex;

    public BaseDocScreen previousScreen = null;

    public BaseDocScreen() {
        super(Component.empty(), 290, 194, background);
    }

    @Override
    public void init() {
        super.init();
        rightArrow = new NuggetImageButton(bookRight - 13, bookTop + 88, 11, 14, PAGE_RIGHT, this::onRightArrowClick);
        leftArrow = new NuggetImageButton(bookLeft + 1, bookTop + 88, 11, 14, PAGE_LEFT, this::onLeftArrowClick);
        addRenderableWidget(leftArrow);
        addRenderableWidget(rightArrow);

        if(!showLeftArrow()){
            leftArrow.visible = false;
        }

        if(!showRightArrow()){
            rightArrow.visible = false;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        SoundManager manager = Minecraft.getInstance().getSoundManager();
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
        if(button == 1 && previousScreen != null){
            Minecraft.getInstance().setScreen(previousScreen);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void transition(BaseDocScreen screen){
        screen.previousScreen = this;
        Minecraft.getInstance().setScreen(screen);
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
}
