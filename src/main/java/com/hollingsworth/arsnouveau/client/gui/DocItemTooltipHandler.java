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
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class DocItemTooltipHandler {

    private static long lexiconStartLookupTime = -1;

    public static void onTooltip(GuiGraphics graphics, ItemStack stack, int mouseX, int mouseY) {
        PoseStack ms = graphics.pose();
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

        if(mc.screen instanceof PageHolderScreen pageHolderScreen && pageHolderScreen.entry == docEntry){
            return;
        }

        int x = tooltipX - 34;
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
            RenderSystem.disableDepthTest();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

            float a = 0.5F + 0.2F * ((float) Math.cos((ClientInfo.totalTicks) / 10) * 0.5F + 0.5F);
            buf.addVertex(cx, cy, 0).setColor(0F, 0.5F, 0F, a);

            for (float i = angles; i > 0; i--) {
                double rad = (i - 90) / 180F * Math.PI;
                buf.addVertex((float) (cx + Math.cos(rad) * r), (float) (cy + Math.sin(rad) * r), 0).setColor(0F, 1F, 0F, 1F);
            }

            buf.addVertex(cx, cy, 0).setColor(0F, 1F, 0F, 0F);
            BufferUploader.drawWithShader(buf.build());
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();

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
