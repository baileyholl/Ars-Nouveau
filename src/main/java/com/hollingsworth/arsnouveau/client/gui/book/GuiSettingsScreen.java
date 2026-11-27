package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectableButton;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSummonDog;
import com.hollingsworth.arsnouveau.common.network.PacketUnsummonDog;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class GuiSettingsScreen extends BaseBook {

    public Screen parent;

    public GuiSettingsScreen(@Nullable Screen parent) {
        super();
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();
        int yOffset = 36;
        SelectableButton dynamicButton = new SelectableButton(bookLeft + 20, bookTop + yOffset, 0, 0, 16, 16, 16, 16, ArsNouveau.prefix("textures/gui/settings_dynamic_light_off.png"),
                ArsNouveau.prefix("textures/gui/settings_dynamic_light_on.png"), (b) -> {
            SelectableButton button = (SelectableButton) b;
            button.isSelected = !button.isSelected;
            LightManager.toggleLightsAndConfig(!Config.DYNAMIC_LIGHTS_ENABLED.get());
            button.withTooltip(Component.translatable(button.isSelected ? "ars_nouveau.dynamic_lights.button_on" : "ars_nouveau.dynamic_lights.button_off"));
        });
        dynamicButton.isSelected = Config.DYNAMIC_LIGHTS_ENABLED.get();
        dynamicButton.withTooltip(Component.translatable(dynamicButton.isSelected ? "ars_nouveau.dynamic_lights.button_on" : "ars_nouveau.dynamic_lights.button_off"));

        addRenderableWidget(dynamicButton);
        if (ClientInfo.isSupporter) {
            GuiImageButton lilyButton = new GuiImageButton(bookLeft + 40, bookTop + yOffset, 0, 0, 16, 16, 16, 16, ArsNouveau.prefix("textures/gui/settings_summon_lily.png"), (b) -> {
                Networking.sendToServer(new PacketSummonDog(PacketSummonDog.DogType.LILY));
            });
            lilyButton.withTooltip(Component.translatable("ars_nouveau.settings.summon_lily"));

            GuiImageButton unsummonLily = new GuiImageButton(bookLeft + 60, bookTop + yOffset, 0, 0, 16, 16, 16, 16, ArsNouveau.prefix("textures/gui/settings_unsummon_lily.png"), (b) -> {
                Networking.sendToServer(new PacketUnsummonDog(PacketSummonDog.DogType.LILY));
            });
            unsummonLily.withTooltip(Component.translatable("ars_nouveau.settings.unsummon_lily"));

            addRenderableWidget(lilyButton);
            addRenderableWidget(unsummonLily);

            GuiImageButton nookButton = new GuiImageButton(bookLeft + 80, bookTop + yOffset, 0, 0, 16, 16, 16, 16, ArsNouveau.prefix("textures/gui/settings_summon_nook.png"), (b) -> {
                Networking.sendToServer(new PacketSummonDog(PacketSummonDog.DogType.NOOK));
            });
            nookButton.withTooltip(Component.translatable("ars_nouveau.settings.summon_nook"));

            GuiImageButton unsummonNook = new GuiImageButton(bookLeft + 100, bookTop + yOffset, 0, 0, 16, 16, 16, 16, ArsNouveau.prefix("textures/gui/settings_unsummon_nook.png"), (b) -> {
                Networking.sendToServer(new PacketUnsummonDog(PacketSummonDog.DogType.NOOK));
            });
            unsummonNook.withTooltip(Component.translatable("ars_nouveau.settings.unsummon_nook"));

            addRenderableWidget(nookButton);
            addRenderableWidget(unsummonNook);
        }
        yOffset += 30;
        addRenderableWidget(new BookSlider(bookLeft + LEFT_PAGE_OFFSET, bookTop + yOffset, 100, 20, Component.translatable("ars_nouveau.book_offset"), Component.empty(), 0, 5, Config.SPELLBOOK_Y_OFFSET.getAsInt(), 1, 1, true, (value) -> {
            int realValue = (int) (value * 5);
            Config.SPELLBOOK_Y_OFFSET.set(realValue);
            Config.SPELLBOOK_Y_OFFSET.save();
        }));
        addSaveButton((button) -> Minecraft.getInstance().setScreen(parent));
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.drawString(font, Component.translatable("ars_nouveau.settings.title").getString(), 51, 24, -8355712, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.close"), 238, 183, -8355712, false);
    }
}
