package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.documentation.search.Search;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.client.gui.documentation.PageHolderScreen;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.items.WornNotebook;
import com.hollingsworth.nuggets.client.rendering.RenderHelpers;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import org.joml.Matrix4f;
import org.joml.Vector2ic;
import org.lwjgl.opengl.GL11;

public class DocItemTooltipHandler {

    private static long lexiconStartLookupTime = -1;

    public static void onTooltip(RenderTooltipEvent.Pre event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() && Minecraft.getInstance().screen instanceof BaseDocScreen docScreen) {
            stack = hoveredDocStack(docScreen);
        }
        if (stack.isEmpty()) {
            return;
        }

        int width = 0;
        int height = event.getComponents().size() == 1 ? -2 : 0;
        for (ClientTooltipComponent component : event.getComponents()) {
            width = Math.max(width, component.getWidth(event.getFont()));
            height += component.getHeight();
        }
        Vector2ic pos = event.getTooltipPositioner().positionTooltip(event.getScreenWidth(), event.getScreenHeight(), event.getX(), event.getY(), width, height);
        int tooltipY = event.getY() - 4;
        int x = event.getX() - 34;
        int[] validSpots = new int[]{event.getX() - 24, event.getX() + 12};
        for (int candidate : validSpots) {
            boolean onScreen = candidate - 6 >= 0 && candidate + 22 <= event.getScreenWidth();
            boolean overlaps = candidate - 6 < pos.x() + width + 4 && candidate + 22 > pos.x() - 4
                    && tooltipY - 6 < pos.y() + height + 4 && tooltipY + 28 > pos.y() - 4;
            if (onScreen && !overlaps) {
                x = candidate;
                break;
            }
        }
        renderOverlay(event.getGraphics(), stack, x, tooltipY);
    }

    private static ItemStack hoveredDocStack(BaseDocScreen screen) {
        for (Renderable renderable : screen.renderablesList()) {
            if (renderable instanceof SinglePageWidget page && page.visible && page.isHovered() && !page.tooltipStack.isEmpty()) {
                return page.tooltipStack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static void renderOverlay(GuiGraphics graphics, ItemStack stack, int x, int tooltipY) {
        PoseStack ms = graphics.pose();
        Minecraft mc = Minecraft.getInstance();
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

        RenderSystem.disableDepthTest();

        graphics.fill(x - 4, tooltipY - 4, x + 20, tooltipY + 26, 0x44000000);
        graphics.fill(x - 6, tooltipY - 6, x + 22, tooltipY + 28, 0x44000000);
        boolean boundToControl = ModKeyBindings.OPEN_DOCUMENTATION.getKey().getValue() == 341;
        if (boundToControl ? PageHolderScreen.hasControlDown() :
                InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), ModKeyBindings.OPEN_DOCUMENTATION.getKey().getValue())) {

            if (lexiconStartLookupTime == -1) {
                lexiconStartLookupTime = System.currentTimeMillis();
            }

            int cx = x + 8;
            int cy = tooltipY + 8;
            float r = 12;
            float time = 1000;
            float angles = (System.currentTimeMillis() - lexiconStartLookupTime) / time * 360F;

            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            ms.pushPose();
            ms.translate(0, 0, 410);
            Matrix4f pose = ms.last().pose();

            BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

            float a = 0.5F + 0.2F * ((float) Math.cos((ClientInfo.totalTicks) / 10) * 0.5F + 0.5F);
            buf.addVertex(pose, cx, cy, 0).setColor(0F, 0.5F, 0F, a);

            for (float i = angles; i > 0; i--) {
                double rad = (i - 90) / 180F * Math.PI;
                buf.addVertex(pose, (float) (cx + Math.cos(rad) * r), (float) (cy + Math.sin(rad) * r), 0).setColor(0F, 1F, 0F, 1F);
            }

            buf.addVertex(pose, cx, cy, 0).setColor(0F, 1F, 0F, 0F);
            BufferUploader.drawWithShader(buf.build());
            ms.popPose();
            RenderSystem.disableBlend();

            if (angles >= 360) {
                DocClientUtils.openToEntry(docEntry.id(), 0);
                resetLexiconLookupTime();
            }
        } else {
            resetLexiconLookupTime();
        }

        ms.pushPose();
        ms.translate(0, 0, 300);
        RenderHelpers.drawItemAsIcon(docEntry.renderStack(), graphics, x, tooltipY, 16, false);
        ms.popPose();

        ms.pushPose();
        ms.translate(0, 0, 500);
        graphics.drawString(mc.font, "?", x + 10, tooltipY + 8, 0xFFFFFFFF);

        ms.scale(0.5F, 0.5F, 1F);
        boolean mac = Minecraft.ON_OSX;
        Component key = (boundToControl ? (mac ? Component.literal("Cmd") : Component.literal("Ctrl")) : ModKeyBindings.OPEN_DOCUMENTATION.getTranslatedKeyMessage().copy())
                .withStyle(ChatFormatting.BOLD);
        graphics.drawString(mc.font, key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF);
        ms.popPose();

        RenderSystem.enableDepthTest();
    }

    public static void resetLexiconLookupTime() {
        lexiconStartLookupTime = -1;
    }
}
