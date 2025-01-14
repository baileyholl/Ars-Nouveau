package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.FamiliarRegistry;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import com.hollingsworth.arsnouveau.client.gui.GuiUtils;
import com.hollingsworth.arsnouveau.client.gui.ModdedScreen;
import com.hollingsworth.arsnouveau.client.gui.buttons.ANButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BaseBook extends ModdedScreen {

    public static final int FULL_WIDTH = 290;
    public static final int FULL_HEIGHT = 188;
    public static ResourceLocation background = ArsNouveau.prefix( "textures/gui/spell_book_template.png");
    public int bookLeft;
    public int bookTop;
    public int bookRight;
    public int bookBottom;
    public List<SpellValidationError> validationErrors = new ArrayList<>();
    public Screen parent;

    public BaseBook() {
        super(Component.literal(""));
    }

    @Override
    public void init() {
        super.init();
        bookLeft = width / 2 - FULL_WIDTH / 2;
        bookTop = height / 2 - FULL_HEIGHT / 2;
        bookRight = width / 2 + FULL_WIDTH / 2;
        bookBottom = height / 2 + FULL_HEIGHT / 2;


        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 22, 0, 0, 23, 20, 23, 20, "textures/gui/worn_book_bookmark.png", this::onDocumentationClick)
                .withTooltip(Component.translatable("ars_nouveau.gui.notebook")));
        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 70, 0, 0, 23, 20, 23, 20, "textures/gui/summon_circle_bookmark.png", this::onFamiliarClick)
                .withTooltip(Component.translatable("ars_nouveau.gui.familiar")));
        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 118, 0, 0, 23, 20, 23, 20, "textures/gui/settings_tab.png", (b) -> {
            Minecraft.getInstance().setScreen(new GuiSettingsScreen(this));
        }).withTooltip(Component.translatable("ars_nouveau.gui.settings")));

        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 142, 0, 0, 23, 20, 23, 20, "textures/gui/discord_tab.png", (b) -> {
            try {
                Util.getPlatform().openUri(new URI("https://discord.com/invite/y7TMXZu"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }).withTooltip(Component.translatable("ars_nouveau.gui.discord")));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        if (scaleFactor != 1) {
            matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);
            mouseX /= scaleFactor;
            mouseY /= scaleFactor;
        }
        drawScreenAfterScale(graphics, mouseX, mouseY, partialTicks);
        matrixStack.popPose();
    }

    public <T extends ANButton> void clearButtons(List<T> buttons) {
        for (ANButton b : buttons) {
            renderables.remove(b);
            children().remove(b);
        }
        buttons.clear();
    }

    public void onFamiliarClick(Button button) {
        Collection<ResourceLocation> familiarHolders = new ArrayList<>();
        IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(ArsNouveau.proxy.getPlayer());
        if (cap != null) {
            familiarHolders = cap.getUnlockedFamiliars().stream().map(s -> s.familiarHolder.getRegistryName()).collect(Collectors.toList());
        }
        Collection<ResourceLocation> finalFamiliarHolders = familiarHolders;
        Minecraft.getInstance().setScreen(new GuiFamiliarScreen(FamiliarRegistry.getFamiliarHolderMap().values().stream().filter(f -> finalFamiliarHolders.contains(f.getRegistryName())).collect(Collectors.toList()), this));
    }

    public void onDocumentationClick(Button button) {
        GuiUtils.openWiki(ArsNouveau.proxy.getPlayer());
    }

    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(background, 0, 0, 0, 0, FULL_WIDTH, FULL_HEIGHT, FULL_WIDTH, FULL_HEIGHT);
    }

    public void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void drawScreenAfterScale(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(bookLeft, bookTop, 0);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        drawForegroundElements(mouseX, mouseY, partialTicks);
        poseStack.popPose();
        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTicks);
        }
        drawTooltip(graphics, mouseX, mouseY);
    }

    public BookSlider buildSlider(int x, int y, Component prefix, Component suffix, double currentVal) {
        return new BookSlider(x, y, 100, 20, prefix, suffix, 1.0D, 255.0D, currentVal, 1, 1, true);
    }

    @Override
    protected void renderBlurredBackground(float pPartialTick) {

    }
}
