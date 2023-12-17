package com.hollingsworth.arsnouveau.client.gui.book;

import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectHarm;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public class InfiniteUi extends BaseBook{

    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
    public static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
    public static final int WINDOW_WIDTH = 252;
    public static final int WINDOW_HEIGHT = 140;
    private static final int WINDOW_INSIDE_X = 9;
    private static final int WINDOW_INSIDE_Y = 18;
    public static final int WINDOW_INSIDE_WIDTH = 234;
    public static final int WINDOW_INSIDE_HEIGHT = 113;
    private static final int WINDOW_TITLE_X = 8;
    private static final int WINDOW_TITLE_Y = 6;
    public static final int BACKGROUND_TILE_WIDTH = 16;
    public static final int BACKGROUND_TILE_HEIGHT = 16;
    public static final int BACKGROUND_TILE_COUNT_X = 14;
    public static final int BACKGROUND_TILE_COUNT_Y = 7;
    private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
    private static final Component NO_ADVANCEMENTS_LABEL = Component.translatable("advancements.empty");
    private static final Component TITLE = Component.translatable("gui.advancements");
    private final Map<Advancement, AdvancementTab> tabs = Maps.newLinkedHashMap();
    @Nullable
    private AdvancementTab selectedTab;
    private boolean isScrolling;
    private static int tabPage, maxPages;
    private double scrollX;
    private double scrollY;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    public GlyphNode root;

    public InfiniteUi() {
        super();
    }

    public void init() {
        this.tabs.clear();
        this.selectedTab = null;
        this.root = new GlyphNode(null, 0, 0, MethodProjectile.INSTANCE.glyphItem.getDefaultInstance());
//        if (this.selectedTab == null && !this.tabs.isEmpty()) {
//            this.advancements.setSelectedTab(this.tabs.values().iterator().next().getAdvancement(), true);
//        } else {
//            this.advancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
//        }
//        if (this.tabs.size() > AdvancementTabType.MAX_TABS) {
//            int guiLeft = (this.width - 252) / 2;
//            int guiTop = (this.height - 140) / 2;
//            addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal("<"), b -> tabPage = Math.max(tabPage - 1, 0       ))
//                    .pos(guiLeft, guiTop - 50).size(20, 20).build());
//            addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal(">"), b -> tabPage = Math.min(tabPage + 1, maxPages))
//                    .pos(guiLeft + WINDOW_WIDTH - 20, guiTop - 50).size(20, 20).build());
//            maxPages = this.tabs.size() / AdvancementTabType.MAX_TABS;
//        }
        this.addNode(this.root);
        GlyphNode node = new GlyphNode(this.root, 20, 0, EffectHarm.INSTANCE.glyphItem.getDefaultInstance());
        root.addChild(node);
        this.addNode(node);
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
//        if (pButton == 0) {
//            int i = (this.width - 252) / 2;
//            int j = (this.height - 140) / 2;
//
//            for(AdvancementTab advancementtab : this.tabs.values()) {
//                if (advancementtab.getPage() == tabPage && advancementtab.isMouseOver(i, j, pMouseX, pMouseY)) {
//                    this.advancements.setSelectedTab(advancementtab.getAdvancement(), true);
//                    break;
//                }
//            }
//        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = (this.width - 252) / 2;
        int j = (this.height - 140) / 2;
        this.renderBackground(pGuiGraphics);
        if (maxPages != 0) {
            net.minecraft.network.chat.Component page = Component.literal(String.format("%d / %d", tabPage + 1, maxPages + 1));
            int width = this.font.width(page);
            pGuiGraphics.drawString(this.font, page.getVisualOrderText(), i + (252 / 2) - (width / 2), j - 44, -1);
        }
        this.renderInside(pGuiGraphics, pMouseX, pMouseY, i + 9, j + 18);
//        this.renderWindow(pGuiGraphics, i, j);
        this.renderTooltips(pGuiGraphics, pMouseX, pMouseY, i, j);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else {
                if (this.maxX - this.minX > 234) {
                    this.scrollX = Mth.clamp(this.scrollX + pDragX, (double)(-(this.maxX - 234)), 0.0D);
                }

                if (this.maxY - this.minY > 113) {
                    this.scrollY = Mth.clamp(this.scrollY + pDragY, (double)(-(this.maxY - 113)), 0.0D);
                }
            }

            return true;
        }
    }

    private void renderInside(GuiGraphics pGuiGraphics, int mouseX, int mouseY, int pX, int pY) {
//        AdvancementTab advancementtab = this.selectedTab;
//        if (advancementtab == null) {
//            pGuiGraphics.fill(pOffsetX + 9, pOffsetY + 18, pOffsetX + 9 + 234, pOffsetY + 18 + 113, -16777216);
//            int i = pOffsetX + 9 + 117;
//            pGuiGraphics.drawCenteredString(this.font, NO_ADVANCEMENTS_LABEL, i, pOffsetY + 18 + 56 - 9 / 2, -1);
//            pGuiGraphics.drawCenteredString(this.font, VERY_SAD_LABEL, i, pOffsetY + 18 + 113 - 9, -1);
//        } else {
//            if (!this.centered) {
//                this.scrollX = (double)(117 - (this.maxX + this.minX) / 2);
//                this.scrollY = (double)(56 - (this.maxY + this.minY) / 2);
//                this.centered = true;
//            }

            pGuiGraphics.enableScissor(pX, pY, pX + 234, pY + 113);
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate((float)pX, (float)pY, 0.0F);
            ResourceLocation resourcelocation = Objects.requireNonNullElse(new ResourceLocation(ArsNouveau.MODID, "textures/gui/advancements/backgrounds/sourcestone.png"), TextureManager.INTENTIONAL_MISSING_TEXTURE);
            int i = Mth.floor(this.scrollX);
            int j = Mth.floor(this.scrollY);
            int k = i % 16;
            int l = j % 16;

            for(int i1 = -1; i1 <= 15; ++i1) {
                for(int j1 = -1; j1 <= 8; ++j1) {
                    pGuiGraphics.blit(resourcelocation, k + 16 * i1, l + 16 * j1, 0.0F, 0.0F, 16, 16, 16, 16);
                }
            }

            this.root.drawConnectivity(pGuiGraphics, i, j, true);
            this.root.drawConnectivity(pGuiGraphics, i, j, false);
            this.root.draw(pGuiGraphics, i, j);
            pGuiGraphics.pose().popPose();
            pGuiGraphics.disableScissor();
//        }
    }

    public void renderWindow(GuiGraphics pGuiGraphics, int pOffsetX, int pOffsetY) {
        RenderSystem.enableBlend();
        pGuiGraphics.blit(WINDOW_LOCATION, pOffsetX, pOffsetY, 0, 0, 252, 140);
        if (this.tabs.size() > 1) {
            for(AdvancementTab advancementtab : this.tabs.values()) {
                if (advancementtab.getPage() == tabPage)
                    advancementtab.drawTab(pGuiGraphics, pOffsetX, pOffsetY, advancementtab == this.selectedTab);
            }

            for(AdvancementTab advancementtab1 : this.tabs.values()) {
                if (advancementtab1.getPage() == tabPage)
                    advancementtab1.drawIcon(pGuiGraphics, pOffsetX, pOffsetY);
            }
        }

        pGuiGraphics.drawString(this.font, TITLE, pOffsetX + 8, pOffsetY + 6, 4210752, false);
    }

    private void renderTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int pOffsetX, int pOffsetY) {
        if (this.selectedTab != null) {
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate((float)(pOffsetX + 9), (float)(pOffsetY + 18), 400.0F);
            RenderSystem.enableDepthTest();
            this.selectedTab.drawTooltips(pGuiGraphics, pMouseX - pOffsetX - 9, pMouseY - pOffsetY - 18, pOffsetX, pOffsetY);
            RenderSystem.disableDepthTest();
            pGuiGraphics.pose().popPose();
        }

        if (this.tabs.size() > 1) {
            for(AdvancementTab advancementtab : this.tabs.values()) {
                if (advancementtab.getPage() == tabPage && advancementtab.isMouseOver(pOffsetX, pOffsetY, (double)pMouseX, (double)pMouseY)) {
                    pGuiGraphics.renderTooltip(this.font, advancementtab.getTitle(), pMouseX, pMouseY);
                }
            }
        }

    }

    private void addNode(GlyphNode pWidget) {
//        this.widgets.put(pAdvancement, pWidget);
        int i = pWidget.getX();
        int j = i + 28;
        int k = pWidget.getY();
        int l = k + 27;
        this.minX = Math.min(this.minX, i);
        this.maxX = Math.max(this.maxX, j);
        this.minY = Math.min(this.minY, k);
        this.maxY = Math.max(this.maxY, l);

//        for(AdvancementWidget advancementwidget : this.widgets.values()) {
//            advancementwidget.attachToParent();
//        }

    }

}
