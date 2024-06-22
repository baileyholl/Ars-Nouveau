package com.hollingsworth.arsnouveau.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;

public class PatchouliTooltipEvent {

    private static float lexiconLookupTime = 0;

    public static void onTooltip(PoseStack ms, ItemStack stack, int mouseX, int mouseY) {
//        Minecraft mc = Minecraft.getInstance();
//        int tooltipX = mouseX;
//        int tooltipY = mouseY - 4;
//
//        if (mc.player != null && !(mc.screen instanceof GuiBook)) {
//            Pair<BookEntry, Integer> lexiconEntry = null;
//            boolean hasSpellBook = false;
//            for (int i = 0; i < Inventory.getSelectionSize(); i++) {
//                ItemStack stackAt = mc.player.getInventory().getItem(i);
//                if (!stackAt.isEmpty()) {
//                    Book book = ItemStackUtil.getBookFromStack(stackAt);
//                    if (book != null && book.id.equals(ArsNouveau.prefix( "worn_notebook"))) {
//                        return;
//                    }
//                    if(stackAt.getItem() instanceof SpellBook){
//                        hasSpellBook = true;
//                    }
//                }
//            }
//            if(!hasSpellBook){
//                return;
//            }
//            ItemStack lexiconStack = new ItemStack(ItemsRegistry.WORN_NOTEBOOK);
//            Book book = ItemStackUtil.getBookFromStack(lexiconStack);
//            Pair<BookEntry, Integer> entry = book.getContents().getEntryForStack(stack);
//
//            if (entry != null && !entry.getFirst().isLocked()) {
//                lexiconEntry = entry;
//            }
//
//            if (lexiconEntry != null) {
//                int x = tooltipX - 34;
//                RenderSystem.disableDepthTest();
//
//                GuiComponent.fill(ms, x - 4, tooltipY - 4, x + 20, tooltipY + 26, 0x44000000);
//                GuiComponent.fill(ms, x - 6, tooltipY - 6, x + 22, tooltipY + 28, 0x44000000);
//
//                if (PatchouliConfig.get().useShiftForQuickLookup() ? Screen.hasShiftDown() : Screen.hasControlDown()) {
//                    lexiconLookupTime += ClientTicker.delta;
//
//                    int cx = x + 8;
//                    int cy = tooltipY + 8;
//                    float r = 12;
//                    float time = 20F;
//                    float angles = lexiconLookupTime / time * 360F;
//
//                    RenderSystem.disableTexture();
//                    RenderSystem.enableBlend();
//                    RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//
//                    BufferBuilder buf = Tesselator.getInstance().getBuilder();
//                    buf.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
//
//                    float a = 0.5F + 0.2F * ((float) Math.cos(ClientTicker.total / 10) * 0.5F + 0.5F);
//                    buf.vertex(cx, cy, 0).color(0F, 0.5F, 0F, a).endVertex();
//
//                    for (float i = angles; i > 0; i--) {
//                        double rad = (i - 90) / 180F * Math.PI;
//                        buf.vertex(cx + Math.cos(rad) * r, cy + Math.sin(rad) * r, 0).color(0F, 1F, 0F, 1F).endVertex();
//                    }
//
//                    buf.vertex(cx, cy, 0).color(0F, 1F, 0F, 0F).endVertex();
//                    Tesselator.getInstance().end();
//
//                    RenderSystem.disableBlend();
//                    RenderSystem.enableTexture();
//
//                    if (lexiconLookupTime >= time) {
//                        int spread = lexiconEntry.getSecond();
//                        ClientBookRegistry.INSTANCE.displayBookGui(lexiconEntry.getFirst().getBook().id, lexiconEntry.getFirst().getId(), spread * 2);
//                    }
//                } else {
//                    lexiconLookupTime = 0F;
//                }
//
//                mc.getItemRenderer().blitOffset = 300;
//                RenderHelper.renderItemStackInGui(ms, lexiconStack, x, tooltipY);
//                mc.getItemRenderer().blitOffset = 0;
//
//                ms.pushPose();
//                ms.translate(0, 0, 500);
//                mc.font.drawShadow(ms, "?", x + 10, tooltipY + 8, 0xFFFFFFFF);
//
//                ms.scale(0.5F, 0.5F, 1F);
//                boolean mac = Minecraft.ON_OSX;
//                Component key = Component.literal(PatchouliConfig.get().useShiftForQuickLookup() ? "Shift" : mac ? "Cmd" : "Ctrl")
//                        .withStyle(ChatFormatting.BOLD);
//                mc.font.drawShadow(ms, key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, 0xFFFFFFFF);
//                ms.popPose();
//
//                RenderSystem.enableDepthTest();
//            } else {
//                lexiconLookupTime = 0F;
//            }
//        } else {
//            lexiconLookupTime = 0F;
//        }
    }
}
