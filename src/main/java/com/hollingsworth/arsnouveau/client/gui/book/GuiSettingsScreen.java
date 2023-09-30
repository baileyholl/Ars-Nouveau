package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSummonLily;
import com.hollingsworth.arsnouveau.common.network.PacketUnsummonLily;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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
        addRenderableWidget(new GuiImageButton(bookRight - 71, bookBottom - 13, 0, 0, 41, 12, 41, 12, "textures/gui/clear_icon.png", (e) -> {
            Minecraft.getInstance().setScreen(parent);
        }));
        if(ClientInfo.isSupporter) {
            GuiImageButton lilyButton = new GuiImageButton(bookLeft + 40, bookTop + 34, 0, 0, 16, 16, 16, 16, new ResourceLocation(ArsNouveau.MODID, "textures/gui/settings_summon_lily.png"), (b) -> {
                Networking.sendToServer(new PacketSummonLily());

            });
            lilyButton.withTooltip(this, Component.translatable("ars_nouveau.settings.summon_lily"));

            GuiImageButton unsummonLily = new GuiImageButton(bookLeft + 60, bookTop + 34, 0, 0, 16, 16, 16, 16, new ResourceLocation(ArsNouveau.MODID, "textures/gui/settings_unsummon_lily.png"), (b) -> {
                Networking.sendToServer(new PacketUnsummonLily());

            });
            unsummonLily.withTooltip(this, Component.translatable("ars_nouveau.settings.unsummon_lily"));

            addRenderableWidget(lilyButton);
            addRenderableWidget(unsummonLily);
        }
    }

    @Override
    public void drawBackgroundElements(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_paper.png"), 216, 179, 0, 0, 56, 15, 56, 15, stack);
        minecraft.font.draw(stack, Component.translatable("ars_nouveau.settings.title").getString(), 51, 24, -8355712);
        minecraft.font.draw(stack, Component.translatable("ars_nouveau.spell_book_gui.close"), 238, 183, -8355712);
    }
}
