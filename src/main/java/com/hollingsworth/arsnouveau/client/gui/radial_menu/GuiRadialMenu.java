package com.hollingsworth.arsnouveau.client.gui.radial_menu;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import org.joml.Matrix3x2fStack;

import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
public class GuiRadialMenu<T> extends Screen {
    private static final float PRECISION = 5.0f;
    private static final int MAX_SLOTS = 20;

    private boolean closing;
    private boolean holdToOpenGUI;
    private RadialMenu<T> radialMenu;
    private List<RadialMenuSlot<T>> radialMenuSlots;
    final float OPEN_ANIMATION_LENGTH = 0.40f;
    private float totalTime;
    private float prevTick;
    private float extraTick;
    /**
     * Zero-Based index
     */
    private int selectedItem;
    public ItemRenderer itemRenderer;

    public GuiRadialMenu(RadialMenu<T> radialMenu) {
        super(Component.literal(""));
        this.radialMenu = radialMenu;
        this.radialMenuSlots = this.radialMenu.getRadialMenuSlots();
        this.closing = false;
        this.holdToOpenGUI = !Config.TOGGLE_RADIAL_HUD.get();
        this.selectedItem = -1;
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    public GuiRadialMenu<T> setHoldToOpenGUI(boolean ignore) {
        this.holdToOpenGUI = ignore;
        return this;
    }

    @SubscribeEvent
    public static void updateInputEvent(MovementInputUpdateEvent event) {
        if (Minecraft.getInstance().screen instanceof GuiRadialMenu) {
            Options settings = Minecraft.getInstance().options;
            ClientInput eInput = event.getInput();
            // In 1.21.11, InputConstants.isKeyDown takes Window object, not long
            com.mojang.blaze3d.platform.Window window = Minecraft.getInstance().getWindow();

            boolean up    = InputConstants.isKeyDown(window, settings.keyUp.getKey().getValue());
            boolean down  = InputConstants.isKeyDown(window, settings.keyDown.getKey().getValue());
            boolean left  = InputConstants.isKeyDown(window, settings.keyLeft.getKey().getValue());
            boolean right = InputConstants.isKeyDown(window, settings.keyRight.getKey().getValue());
            boolean jump  = InputConstants.isKeyDown(window, settings.keyJump.getKey().getValue());
            boolean shift = InputConstants.isKeyDown(window, settings.keyShift.getKey().getValue());
            boolean sprint = eInput.keyPresses.sprint();

            eInput.keyPresses = new Input(up, down, left, right, jump, shift, sprint);
        }
    }

    @Override
    public void tick() {
        if (totalTime != OPEN_ANIMATION_LENGTH) {
            extraTick++;
        }

        if (holdToOpenGUI) {
            int openRadialKey = ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue();
            boolean radialKeyIsDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), openRadialKey);
            if (!radialKeyIsDown) {
                if (this.selectedItem != -1) {
                    radialMenu.setCurrentSlot(selectedItem);
                }
                minecraft.player.closeContainer();
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        // In 1.21.11, graphics.pose() returns Matrix3x2fStack
        Matrix3x2fStack ms = graphics.pose();
        float openAnimation = closing ? 1.0f - totalTime / OPEN_ANIMATION_LENGTH : totalTime / OPEN_ANIMATION_LENGTH;

        float currTick = ClientInfo.partialTicks;
        totalTime += (currTick + extraTick - prevTick) / 20f;
        extraTick = 0;
        prevTick = currTick;

        float animProgress = Mth.clamp(openAnimation, 0, 1);
        animProgress = (float) (1 - Math.pow(1 - animProgress, 3));
        float radiusIn = Math.max(0.1f, 45 * animProgress);
        float radiusOut = radiusIn * 2;
        float itemRadius = (radiusIn + radiusOut) * 0.5f;

        int centerOfScreenX = width / 2;
        int centerOfScreenY = height / 2;
        int numberOfSlices = Math.min(MAX_SLOTS, radialMenuSlots.size());

        double mousePositionInDegreesInRelationToCenterOfScreen = Math.toDegrees(Math.atan2(mouseY - centerOfScreenY, mouseX - centerOfScreenX));
        double mouseDistanceToCenterOfScreen = Math.sqrt(Math.pow(mouseX - centerOfScreenX, 2) + Math.pow(mouseY - centerOfScreenY, 2));
        float slot0 = (((0 - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
        if (mousePositionInDegreesInRelationToCenterOfScreen < slot0) {
            mousePositionInDegreesInRelationToCenterOfScreen += 360;
        }

        ms.pushMatrix();
        // RenderSystem blend/shader calls removed in 1.21.11 - draw using GuiGraphics.fill approximation

        boolean hasMouseOver = false;
        int mousedOverSlot = -1;

        if (!closing) {
            selectedItem = -1;
            for (int i = 0; i < numberOfSlices; i++) {
                float sliceBorderLeft = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
                float sliceBorderRight = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
                if (mousePositionInDegreesInRelationToCenterOfScreen >= sliceBorderLeft && mousePositionInDegreesInRelationToCenterOfScreen < sliceBorderRight && mouseDistanceToCenterOfScreen >= radiusIn && mouseDistanceToCenterOfScreen < radiusOut) {
                    selectedItem = i;
                    break;
                }
            }
        }

        // Draw slices using pixel-by-pixel fill (approximation for removed Tessellator pipeline)
        for (int i = 0; i < numberOfSlices; i++) {
            float sliceBorderLeft = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
            float sliceBorderRight = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
            boolean isSelected = selectedItem == i;
            if (isSelected) {
                hasMouseOver = true;
                mousedOverSlot = selectedItem;
                drawSliceGraphics(graphics, centerOfScreenX, centerOfScreenY, radiusIn, radiusOut, sliceBorderLeft, sliceBorderRight, 63, 161, 191, 60);
            } else {
                drawSliceGraphics(graphics, centerOfScreenX, centerOfScreenY, radiusIn, radiusOut, sliceBorderLeft, sliceBorderRight, 0, 0, 0, 64);
            }
        }

        if (hasMouseOver && mousedOverSlot != -1) {
            int adjusted = ((mousedOverSlot + (numberOfSlices / 2 + 1)) % numberOfSlices) - 1;
            adjusted = adjusted == -1 ? numberOfSlices - 1 : adjusted;
            graphics.drawCenteredString(font, radialMenuSlots.get(adjusted).slotName(), width / 2, (height - font.lineHeight) / 2, 16777215);
        }

        ms.popMatrix();
        for (int i = 0; i < numberOfSlices; i++) {
            ItemStack stack = new ItemStack(Blocks.DIRT);
            float angle1 = ((i / (float) numberOfSlices) - 0.25f) * 2 * (float) Math.PI;
            if (numberOfSlices % 2 != 0) {
                angle1 += Math.PI / numberOfSlices;
            }
            float posX = centerOfScreenX - 8 + itemRadius * (float) Math.cos(angle1);
            float posY = centerOfScreenY - 8 + itemRadius * (float) Math.sin(angle1);
            // RenderSystem.disableDepthTest() removed in 1.21.11

            T primarySlotIcon = radialMenuSlots.get(i).primarySlotIcon();
            List<T> secondarySlotIcons = radialMenuSlots.get(i).secondarySlotIcons();
            if (primarySlotIcon != null) {
                radialMenu.drawIcon(primarySlotIcon, graphics, (int) posX, (int) posY, 16);
            }
            if (secondarySlotIcons != null && !secondarySlotIcons.isEmpty()) {
                drawSecondaryIcons(graphics, (int) posX, (int) posY, secondarySlotIcons);
            }
            ms.pushMatrix();
            // Matrix3x2fStack.translate is 2D; z-ordering handled by GUI system
            ms.translate(0, 0);
            drawSliceName(graphics, String.valueOf(i + 1), stack, (int) posX, (int) posY);
            ms.popMatrix();
        }

        if (mousedOverSlot != -1) {
            int adjusted = ((mousedOverSlot + (numberOfSlices / 2 + 1)) % numberOfSlices) - 1;
            adjusted = adjusted == -1 ? numberOfSlices - 1 : adjusted;
            selectedItem = adjusted;
        }
    }

    /**
     * Draws a pie slice using GuiGraphics.fill pixel approximation.
     * Replaces Tessellator/BufferBuilder approach removed in 1.21.11.
     */
    public void drawSliceGraphics(GuiGraphics graphics, float cx, float cy, float radiusIn, float radiusOut,
                                   float startAngleDeg, float endAngleDeg, int r, int g, int b, int a) {
        int color = (a << 24) | (r << 16) | (g << 8) | b;
        float angle = endAngleDeg - startAngleDeg;
        int sections = Math.max(1, Mth.ceil(angle / PRECISION));
        float startAngle = (float) Math.toRadians(startAngleDeg);
        float endAngle = (float) Math.toRadians(endAngleDeg);
        float totalAngle = endAngle - startAngle;

        for (int i = 0; i < sections; i++) {
            float a1 = startAngle + (i / (float) sections) * totalAngle;
            float a2 = startAngle + ((i + 1) / (float) sections) * totalAngle;

            // Draw a filled quad for each slice segment
            int x1in  = (int)(cx + radiusIn  * Math.cos(a1));
            int y1in  = (int)(cy + radiusIn  * Math.sin(a1));
            int x1out = (int)(cx + radiusOut * Math.cos(a1));
            int y1out = (int)(cy + radiusOut * Math.sin(a1));
            int x2in  = (int)(cx + radiusIn  * Math.cos(a2));
            int y2in  = (int)(cy + radiusIn  * Math.sin(a2));
            int x2out = (int)(cx + radiusOut * Math.cos(a2));
            int y2out = (int)(cy + radiusOut * Math.sin(a2));

            // Fill each triangular/quad section via fill
            int minX = Math.min(Math.min(x1in, x1out), Math.min(x2in, x2out));
            int maxX = Math.max(Math.max(x1in, x1out), Math.max(x2in, x2out));
            int minY = Math.min(Math.min(y1in, y1out), Math.min(y2in, y2out));
            int maxY = Math.max(Math.max(y1in, y1out), Math.max(y2in, y2out));
            graphics.fill(minX, minY, maxX + 1, maxY + 1, color);
        }
    }

    public void drawSecondaryIcons(GuiGraphics ms, int positionXOfPrimaryIcon, int positionYOfPrimaryIcon, List<T> secondarySlotIcons) {
        if (!radialMenu.isShowMoreSecondaryItems()) {
            drawSecondaryIcon(ms, secondarySlotIcons.get(0), positionXOfPrimaryIcon, positionYOfPrimaryIcon, radialMenu.getSecondaryIconStartingPosition());
        } else {
            SecondaryIconPosition currentSecondaryIconPosition = radialMenu.getSecondaryIconStartingPosition();
            for (T secondarySlotIcon : secondarySlotIcons) {
                drawSecondaryIcon(ms, secondarySlotIcon, positionXOfPrimaryIcon, positionYOfPrimaryIcon, currentSecondaryIconPosition);
                currentSecondaryIconPosition = SecondaryIconPosition.getNextPositon(currentSecondaryIconPosition);
            }
        }
    }

    public void drawSecondaryIcon(GuiGraphics poseStack, T item, int positionXOfPrimaryIcon, int positionYOfPrimaryIcon, SecondaryIconPosition secondaryIconPosition) {
        int offset = radialMenu.getOffset();
        switch (secondaryIconPosition) {
            case NORTH ->
                    radialMenu.drawIcon(item, poseStack, positionXOfPrimaryIcon + offset, positionYOfPrimaryIcon - 14 + offset, 10);
            case EAST ->
                    radialMenu.drawIcon(item, poseStack, positionXOfPrimaryIcon + 14 + offset, positionYOfPrimaryIcon + offset, 10);
            case SOUTH ->
                    radialMenu.drawIcon(item, poseStack, positionXOfPrimaryIcon + offset, positionYOfPrimaryIcon + 14 + offset, 10);
            case WEST ->
                    radialMenu.drawIcon(item, poseStack, positionXOfPrimaryIcon - 14 + offset, positionYOfPrimaryIcon + offset, 10);
        }
    }

    public void drawSliceName(GuiGraphics graphics, String sliceName, ItemStack stack, int posX, int posY) {
        if (!radialMenu.isShowMoreSecondaryItems()) {
            graphics.renderItemDecorations(font, stack, posX + 5, posY, sliceName);
        } else {
            graphics.renderItemDecorations(font, stack, posX + 5, posY + 5, sliceName);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int key = event.key();
        int adjustedKey = key - 48;
        if (adjustedKey >= 0 && adjustedKey < radialMenuSlots.size()) {
            selectedItem = adjustedKey == 0 ? radialMenuSlots.size() : adjustedKey;
            selectedItem = selectedItem - 1; // Offset by 1 because 0 based indexing but users see 1 indexed
            // Simulate click to select
            if (this.selectedItem != -1) {
                radialMenu.setCurrentSlot(selectedItem);
                minecraft.player.closeContainer();
            }
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean consumed) {
        if (this.selectedItem != -1) {
            radialMenu.setCurrentSlot(selectedItem);
            minecraft.player.closeContainer();
        }
        return super.mouseClicked(event, consumed);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

/*
Note: This code has been modified from David Quintana's solution.
Below is the required copyright notice.
Copyright (c) 2015, David Quintana <gigaherz@gmail.com>
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the author nor the
      names of the contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
