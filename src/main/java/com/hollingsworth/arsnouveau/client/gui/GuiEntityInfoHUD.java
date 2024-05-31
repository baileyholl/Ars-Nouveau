package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class GuiEntityInfoHUD {
    public static final IGuiOverlay OVERLAY = GuiEntityInfoHUD::renderOverlay;

    public static int hoverTicks = 0;
    public static Object lastHovered = null;

    public static void renderOverlay(ExtendedGui gui, GuiGraphics graphics, float partialTicks, int width,
                                     int height) {
        PoseStack poseStack = graphics.pose();
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        HitResult objectMouseOver = mc.hitResult;
        List<Component> tooltip = new ArrayList<>();
        Object hovering = null;
        if(objectMouseOver instanceof BlockHitResult hitResult){
            hovering = hitResult.getBlockPos();
            if(mc.level.getBlockEntity(hitResult.getBlockPos()) instanceof ITooltipProvider iTooltipProvider){
                iTooltipProvider.getTooltip(tooltip);
            }
        }else if(objectMouseOver instanceof EntityHitResult result){
            if (result.getEntity() instanceof ITooltipProvider iTooltipProvider) {
                iTooltipProvider.getTooltip(tooltip);
            }
            if (result.getEntity() instanceof ItemFrame frame) {
                ItemScroll.ItemScrollData data = new ItemScroll.ItemScrollData(frame.getItem());
                for(ItemStack i : data.getItems()){
                    tooltip.add(i.getHoverName());
                }
            }
            hovering = result.getEntity();
        }

        if (hovering == null || (lastHovered != null && !lastHovered.equals(hovering))) {
            lastHovered = null;
            hoverTicks = 0;
        }

        if (lastHovered == null || lastHovered.equals(hovering))
            hoverTicks++;
        else
            hoverTicks = 0;
        lastHovered = hovering;


        if (tooltip.isEmpty()) {
            return;
        }
        poseStack.pushPose();

        int tooltipTextWidth = 0;
        for (FormattedText textLine : tooltip) {
            int textLineWidth = mc.font.width(textLine);
            if (textLineWidth > tooltipTextWidth)
                tooltipTextWidth = textLineWidth;
        }

        int tooltipHeight = 8;
        if (tooltip.size() > 1) {
            tooltipHeight += 2; // gap between title lines and next lines
            tooltipHeight += (tooltip.size() - 1) * 10;
        }
        int xOffset = Config.TOOLTIP_X_OFFSET.get();
        int posX = width / 2 + xOffset;
        int posY = height / 2 + Config.TOOLTIP_Y_OFFSET.get();

        posX = Math.min(posX, width - tooltipTextWidth - 20);
        posY = Math.min(posY, height - tooltipHeight - 20);

        float fade = Mth.clamp((hoverTicks + partialTicks) / 12f, 0, 1);
        Color colorBackground = VANILLA_TOOLTIP_BACKGROUND.scaleAlpha(.75f);
        Color colorBorderTop = VANILLA_TOOLTIP_BORDER_1;
        Color colorBorderBot = VANILLA_TOOLTIP_BORDER_2;

        if (fade < 1) {
            poseStack.translate((1 - fade) * Math.signum(xOffset + .5f) * 4, 0, 0);
            colorBackground.scaleAlpha(fade);
            colorBorderTop.scaleAlpha(fade);
            colorBorderBot.scaleAlpha(fade);
        }

        drawHoveringText(ItemStack.EMPTY, graphics, tooltip,  posX, posY, width, height, -1, colorBackground.getRGB(),
                colorBorderTop.getRGB(), colorBorderBot.getRGB(), mc.font);
        poseStack.popPose();
    }
    public static final Color VANILLA_TOOLTIP_BORDER_1 = new Color(0x50_5000ff, true);
    public static final Color VANILLA_TOOLTIP_BORDER_2 = new Color(0x50_28007f, true);
    public static final Color VANILLA_TOOLTIP_BACKGROUND =  new Color(0xf0_100010, true);


    public static void drawHoveringText(@NotNull final ItemStack stack, GuiGraphics graphics,
                                        List<? extends FormattedText> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight,
                                        int maxTextWidth, int backgroundColor, int borderColorStart, int borderColorEnd, Font font) {
        if (textLines.isEmpty())
            return;
        PoseStack pStack = graphics.pose();
        List<ClientTooltipComponent> list = ClientHooks.gatherTooltipComponents(stack, textLines, stack.getTooltipImage(), mouseX, screenWidth, screenHeight, font);
        RenderTooltipEvent.Pre event =
                new RenderTooltipEvent.Pre(stack, graphics, mouseX, mouseY, screenWidth, screenHeight, font, list, DefaultTooltipPositioner.INSTANCE);
        if (NeoForge.EVENT_BUS.post(event))
            return;

        mouseX = event.getX();
        mouseY = event.getY();
        screenWidth = event.getScreenWidth();
        screenHeight = event.getScreenHeight();
        font = event.getFont();

        // RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();
        int tooltipTextWidth = 0;

        for (FormattedText textLine : textLines) {
            int textLineWidth = font.width(textLine);
            if (textLineWidth > tooltipTextWidth)
                tooltipTextWidth = textLineWidth;
        }

        boolean needsWrap = false;

        int titleLinesCount = 1;
        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) // if the tooltip doesn't fit on the screen
            {
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }

        if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
            tooltipTextWidth = maxTextWidth;
            needsWrap = true;
        }

        if (needsWrap) {
            int wrappedTooltipWidth = 0;
            List<FormattedText> wrappedTextLines = new ArrayList<>();
            for (int i = 0; i < textLines.size(); i++) {
                FormattedText textLine = textLines.get(i);
                List<FormattedText> wrappedLine = font.getSplitter()
                        .splitLines(textLine, tooltipTextWidth, Style.EMPTY);
                if (i == 0)
                    titleLinesCount = wrappedLine.size();

                for (FormattedText line : wrappedLine) {
                    int lineWidth = font.width(line);
                    if (lineWidth > wrappedTooltipWidth)
                        wrappedTooltipWidth = lineWidth;
                    wrappedTextLines.add(line);
                }
            }
            tooltipTextWidth = wrappedTooltipWidth;
            textLines = wrappedTextLines;

            if (mouseX > screenWidth / 2)
                tooltipX = mouseX - 16 - tooltipTextWidth;
            else
                tooltipX = mouseX + 12;
        }

        int tooltipY = mouseY - 12;
        int tooltipHeight = 8;

        if (textLines.size() > 1) {
            tooltipHeight += (textLines.size() - 1) * 10;
            if (textLines.size() > titleLinesCount)
                tooltipHeight += 2; // gap between title lines and next lines
        }

        if (tooltipY < 4)
            tooltipY = 4;
        else if (tooltipY + tooltipHeight + 4 > screenHeight)
            tooltipY = screenHeight - tooltipHeight - 4;

        final int zLevel = 400;
        RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, graphics, tooltipX, tooltipY,
                font, backgroundColor, borderColorStart, borderColorEnd, list);
        NeoForge.EVENT_BUS.post(colorEvent);
        backgroundColor = colorEvent.getBackgroundStart();
        borderColorStart = colorEvent.getBorderStart();
        borderColorEnd = colorEvent.getBorderEnd();

        pStack.pushPose();
        Matrix4f mat = pStack.last()
                .pose();
        graphics.fillGradient(tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3,
                tooltipY - 3, backgroundColor, backgroundColor);
        graphics.fillGradient(tooltipX - 3, tooltipY + tooltipHeight + 3,
                tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
        graphics.fillGradient(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3,
                tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        graphics.fillGradient(tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3,
                backgroundColor, backgroundColor);
        graphics.fillGradient(tooltipX + tooltipTextWidth + 3, tooltipY - 3,
                tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        graphics.fillGradient(tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1,
                tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        graphics.fillGradient(tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1,
                tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        graphics.fillGradient(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3,
                tooltipY - 3 + 1, borderColorStart, borderColorStart);
        graphics.fillGradient(tooltipX - 3, tooltipY + tooltipHeight + 2,
                tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

        MultiBufferSource.BufferSource renderType = MultiBufferSource.immediate(Tesselator.getInstance()
                .getBuilder());
        pStack.translate(0.0D, 0.0D, zLevel);

        for (int lineNumber = 0; lineNumber < list.size(); ++lineNumber) {
            ClientTooltipComponent line = list.get(lineNumber);

            if (line != null)
                line.renderText(font, tooltipX, tooltipY, mat, renderType);

            if (lineNumber + 1 == titleLinesCount)
                tooltipY += 2;

            tooltipY += 10;
        }

        renderType.endBatch();
        pStack.popPose();

        RenderSystem.enableDepthTest();
    }
}
