package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

import java.util.List;

public abstract class ListParticleWidgetProvider extends ParticleConfigWidgetProvider{

    List<Button> buttons;
    GuiImageButton upButton;
    GuiImageButton downButton;
    int pageOffset;
    int maxEntries;

    public ListParticleWidgetProvider(int x, int y, int width, int height, List<Button> buttons) {
        super(x, y, width, height);
        this.buttons = buttons;
        maxEntries = buttons.size();
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
        int numPerPage = 8;
        pageOffset += numPerPage * offset;
        pageOffset = Math.max(0, Math.min(pageOffset, maxEntries - numPerPage));

        for(var button : buttons){
            button.active = false;
            button.visible = false;
            button.setPosition(-100, -100);
        }
        var sublist = buttons.subList(pageOffset, Math.min(buttons.size(), pageOffset + numPerPage));
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

    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    public void addWidgets(List<AbstractWidget> widgets){
        for (Button button : buttons) {
            widgets.add(button);
        }

        upButton = new GuiImageButton(x + 80, y + height - 5, DocAssets.BUTTON_UP, (button) -> {
            onScroll(-1);
        }).withHoverImage(DocAssets.BUTTON_UP_HOVER);

        downButton = new GuiImageButton(x + 100, y + height - 5, DocAssets.BUTTON_DOWN, (button) -> {
            onScroll(1);
        }).withHoverImage(DocAssets.BUTTON_DOWN_HOVER);

        widgets.add(upButton);
        widgets.add(downButton);
        onScroll(pageOffset);
    }
}
