package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.documentation.search.Search;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.documentation.PageHolderScreen;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.WornNotebook;
import com.hollingsworth.nuggets.client.rendering.RenderHelpers;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.InputQuirks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

public class DocItemTooltipHandler {

    private static long lexiconStartLookupTime = -1;

    public static void onTooltip(GuiGraphics graphics, ItemStack stack, int mouseX, int mouseY) {
        // In 1.21.11, graphics.pose() returns Matrix3x2fStack, not PoseStack
        Matrix3x2fStack ms = graphics.pose();
        Minecraft mc = Minecraft.getInstance();
        int tooltipX = mouseX;
        int tooltipY = mouseY - 4;
        if (mc.player == null) {
            return;
        }

        DocEntry docEntry = Search.itemToEntryMap.get(stack.getItem());

        if (docEntry == null) {
            return;
        }
        boolean hasSpellBook = false;
        for (int i = 0; i < Inventory.getSelectionSize(); i++) {
            ItemStack stackAt = mc.player.getInventory().getItem(i);
            if (!stackAt.isEmpty()) {
                if (stackAt.getItem() instanceof SpellBook || stackAt.getItem() instanceof WornNotebook) {
                    hasSpellBook = true;
                }
            }
        }
        if (!hasSpellBook) {
            resetLexiconLookupTime();
            return;
        }

        if (mc.screen instanceof PageHolderScreen pageHolderScreen && pageHolderScreen.entry == docEntry) {
            return;
        }

        int x = tooltipX - 34;
        // RenderSystem.disableDepthTest() removed in 1.21.11

        graphics.fill(x - 4, tooltipY - 4, x + 20, tooltipY + 26, 0x44000000);
        graphics.fill(x - 6, tooltipY - 6, x + 22, tooltipY + 28, 0x44000000);
        boolean boundToControl = ModKeyBindings.OPEN_DOCUMENTATION.getKey().getValue() == 341;
        // In 1.21.11: InputConstants.isKeyDown takes Window object, not long
        if (boundToControl ? PageHolderScreen.hasControlDown() :
                InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), ModKeyBindings.OPEN_DOCUMENTATION.getKey().getValue())) {

            if (lexiconStartLookupTime == -1) {
                lexiconStartLookupTime = System.currentTimeMillis();
            }

            int cx = x + 8;
            int cy = tooltipY + 8;
            float r = 12;
            float time = 1000;
            float angles = (System.currentTimeMillis() - lexiconStartLookupTime) / time * 360F;

            // RenderSystem blend/depth methods removed in 1.21.11 (replaced by RenderPipelines).
            // Drawing the arc progress indicator using GuiGraphics.fill as approximation.
            // TODO: Replace with RenderPipeline-based arc drawing if precise arc needed.
            if (angles > 0) {
                int arcColor = (0x80 << 24) | (0x00 << 16) | (0xCC << 8) | 0x00; // semi-transparent green
                int filled = (int) (angles / 360f * 16);
                for (int i = 0; i < filled; i++) {
                    float rad = (float) (i / 16.0 * 2 * Math.PI - Math.PI / 2);
                    int px = (int) (cx + Math.cos(rad) * r) - 1;
                    int py = (int) (cy + Math.sin(rad) * r) - 1;
                    graphics.fill(px, py, px + 2, py + 2, arcColor);
                }
            }

            if (angles >= 360) {
                DocClientUtils.openToEntry(docEntry.id(), 0);
                resetLexiconLookupTime();
            }
        } else {
            resetLexiconLookupTime();
        }

        ms.pushMatrix();
        ms.translate(0, 0);
        RenderHelpers.drawItemAsIcon(docEntry.renderStack(), graphics, x, tooltipY, 16, false);
        ms.popMatrix();

        ms.pushMatrix();
        ms.translate(0, 0);
        graphics.drawString(mc.font, "?", x + 10, tooltipY + 8, 0xFFFFFFFF);

        ms.scale(0.5F, 0.5F);
        boolean mac = InputQuirks.ON_OSX;
        Component key = (boundToControl ? (mac ? Component.translatable("ars_nouveau.key.cmd") : Component.translatable("ars_nouveau.key.ctrl")) : ModKeyBindings.OPEN_DOCUMENTATION.getTranslatedKeyMessage().copy())
                .withStyle(ChatFormatting.BOLD);
        graphics.drawString(mc.font, key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF);
        ms.popMatrix();
    }

    public static void resetLexiconLookupTime() {
        lexiconStartLookupTime = -1;
    }
}
