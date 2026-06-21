package com.hollingsworth.arsnouveau.client.container;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ArcanoRewardScreen extends AbstractContainerScreen<ArcanoRewardMenu> {

    public ArcanoRewardScreen(ArcanoRewardMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 132;
    }

    @Override
    protected void init() {
        super.init();
        int buttonY = topPos + 96;
        addRenderableWidget(Button.builder(Component.translatable("ars_nouveau.arcano_reward.continue"), button -> sendButton(ArcanoRewardMenu.CONTINUE_BUTTON))
                .bounds(leftPos + 16, buttonY, 68, 20)
                .build());
        addRenderableWidget(Button.builder(Component.translatable("ars_nouveau.arcano_reward.leave"), button -> sendButton(ArcanoRewardMenu.LEAVE_BUTTON))
                .bounds(leftPos + 92, buttonY, 68, 20)
                .build());
    }

    private void sendButton(int id) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xF0101010);
        graphics.fill(leftPos + 6, topPos + 6, leftPos + imageWidth - 6, topPos + imageHeight - 6, 0xFF2B1838);
        graphics.fill(leftPos + 13, topPos + 31, leftPos + imageWidth - 13, topPos + 78, 0xFF171019);

        for (int i = 0; i < ArcanoRewardMenu.DUMMY_LOOT.size(); i++) {
            int x = leftPos + 51 + i * 28;
            int y = topPos + 46;
            graphics.fill(x - 2, y - 2, x + 18, y + 18, 0xFF3F2A4D);
            ItemStack stack = ArcanoRewardMenu.DUMMY_LOOT.get(i);
            graphics.renderItem(stack, x, y);
            graphics.renderItemDecorations(font, stack, x, y);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 12, 13, 0xE6D7FF, false);
        graphics.drawString(font, Component.translatable("ars_nouveau.arcano_reward.loot"), 14, 33, 0xCBB7DD, false);
    }
}
