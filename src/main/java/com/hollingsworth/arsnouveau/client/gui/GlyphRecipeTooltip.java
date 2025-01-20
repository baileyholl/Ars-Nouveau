package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GlyphRecipeTooltip implements ClientTooltipComponent {

    public static final ResourceLocation TEXTURE_LOCATION = ArsNouveau.prefix("textures/gui/bundle.png");
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int TEX_SIZE = 128;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;
    private final List<Ingredient> items;


    public GlyphRecipeTooltip(List<Ingredient> items) {
        this.items = items;
    }

    public int getHeight() {
        return this.gridSizeY() * SLOT_SIZE_Y + 2 + MARGIN_Y;
    }

    public int getWidth(@NotNull Font pFont) {
        return this.gridSizeX() * SLOT_SIZE_X + 2;
    }


    public void renderImage(@NotNull Font pFont, int pMouseX, int pMouseY, @NotNull GuiGraphics graphics) {
        if (this.items.isEmpty())
            return;
        int i = this.gridSizeX();
        int j = this.gridSizeY();
        boolean overWEight = false;
        int k = 0;

        for (int l = 0; l < j; ++l) {
            for (int i1 = 0; i1 < i; ++i1) {
                int j1 = pMouseX + i1 * SLOT_SIZE_X + BORDER_WIDTH;
                int k1 = pMouseY + l * SLOT_SIZE_Y + BORDER_WIDTH;
                this.renderSlot(j1, k1, k++, overWEight, pFont,graphics);
            }
        }

        this.drawBorder(pMouseX, pMouseY, i, j, graphics);
    }

    private void renderSlot(int pX, int pY, int pItemIndex, boolean pIsBundleFull, Font pFont, GuiGraphics graphics) {
        if (pItemIndex >= this.items.size()) {
            this.blit(graphics, pX, pY, pIsBundleFull ? GlyphRecipeTooltip.Texture.BLOCKED_SLOT : GlyphRecipeTooltip.Texture.SLOT);
        } else {
            List<ItemStack> items = new ArrayList<>(List.of(this.items.get(pItemIndex).getItems()));
            ItemStack itemstack = items.get((ClientInfo.ticksInGame / 20) % items.size());
            this.blit(graphics, pX, pY, GlyphRecipeTooltip.Texture.SLOT);
            graphics.renderItem(itemstack, pX + BORDER_WIDTH, pY + BORDER_WIDTH, pItemIndex);
            graphics.renderItemDecorations(pFont, itemstack, pX + BORDER_WIDTH, pY + BORDER_WIDTH);
        }
    }

    private void drawBorder(int pX, int pY, int pSlotWidth, int pSlotHeight, GuiGraphics pPoseStack) {
        this.blit(pPoseStack, pX, pY, GlyphRecipeTooltip.Texture.BORDER_CORNER_TOP);
        this.blit(pPoseStack, pX + pSlotWidth * SLOT_SIZE_X + BORDER_WIDTH, pY, GlyphRecipeTooltip.Texture.BORDER_CORNER_TOP);

        for (int i = 0; i < pSlotWidth; ++i) {
            this.blit(pPoseStack, pX + BORDER_WIDTH + i * SLOT_SIZE_X, pY, GlyphRecipeTooltip.Texture.BORDER_HORIZONTAL_TOP);
            this.blit(pPoseStack, pX + BORDER_WIDTH + i * SLOT_SIZE_X, pY + pSlotHeight * SLOT_SIZE_Y, GlyphRecipeTooltip.Texture.BORDER_HORIZONTAL_BOTTOM);
        }

        for (int j = 0; j < pSlotHeight; ++j) {
            this.blit(pPoseStack, pX, pY + j * SLOT_SIZE_Y + BORDER_WIDTH, GlyphRecipeTooltip.Texture.BORDER_VERTICAL);
            this.blit(pPoseStack, pX + pSlotWidth * SLOT_SIZE_X + BORDER_WIDTH, pY + j * SLOT_SIZE_Y + BORDER_WIDTH, GlyphRecipeTooltip.Texture.BORDER_VERTICAL);
        }

        this.blit(pPoseStack, pX, pY + pSlotHeight * SLOT_SIZE_Y, GlyphRecipeTooltip.Texture.BORDER_CORNER_BOTTOM);
        this.blit(pPoseStack, pX + pSlotWidth * SLOT_SIZE_X + BORDER_WIDTH, pY + pSlotHeight * SLOT_SIZE_Y, GlyphRecipeTooltip.Texture.BORDER_CORNER_BOTTOM);
    }

    private void blit(GuiGraphics graphics, int pX, int pY, GlyphRecipeTooltip.Texture pTexture) {
        graphics.blit(TEXTURE_LOCATION, pX, pY, (float) pTexture.x, (float) pTexture.y, pTexture.w, pTexture.h, TEX_SIZE, TEX_SIZE);
    }

    private int gridSizeX() {
        return this.items.isEmpty() ? 0 : Math.min(3, this.items.size());
    }

    private int gridSizeY() {
        if (items.isEmpty())
            return 0;
        if (items.size() % 3 != 0) {
            return items.size() / 3 + 1;
        }
        return items.size() / 3;
    }

    enum Texture {
        SLOT(0, 0, 18, 20),
        BLOCKED_SLOT(0, 40, 18, 20),
        BORDER_VERTICAL(0, 18, BORDER_WIDTH, 20),
        BORDER_HORIZONTAL_TOP(0, 20, 18, BORDER_WIDTH),
        BORDER_HORIZONTAL_BOTTOM(0, 60, 18, BORDER_WIDTH),
        BORDER_CORNER_TOP(0, 20, BORDER_WIDTH, BORDER_WIDTH),
        BORDER_CORNER_BOTTOM(0, 60, BORDER_WIDTH, BORDER_WIDTH);

        public final int x;
        public final int y;
        public final int w;
        public final int h;

        Texture(int p_169928_, int p_169929_, int p_169930_, int p_169931_) {
            this.x = p_169928_;
            this.y = p_169929_;
            this.w = p_169930_;
            this.h = p_169931_;
        }
    }
}
