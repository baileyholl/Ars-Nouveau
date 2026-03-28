package com.hollingsworth.arsnouveau.api.documentation;

import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.DocItemTooltipHandler;
import com.hollingsworth.arsnouveau.client.gui.GuiUtils;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.client.gui.documentation.IndexScreen;
import com.hollingsworth.arsnouveau.client.gui.documentation.PageHolderScreen;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import com.hollingsworth.nuggets.client.gui.NuggetMultilLineLabel;
import org.joml.Matrix3x2fStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DocClientUtils {

    public static void openBook() {
        if (DocPlayerData.previousScreen != null) {
            Minecraft.getInstance().setScreen(DocPlayerData.previousScreen);
            return;
        }
        IndexScreen.open();
    }

    public static void openToEntry(Identifier resourceLocation, int pageIndex) {
        DocEntry entry = DocumentationRegistry.getEntry(resourceLocation);
        if (entry == null) {
            IndexScreen.open();
            return;
        }
        if (Minecraft.getInstance().screen instanceof BaseDocScreen baseDocScreen) {
            baseDocScreen.transition(new PageHolderScreen(entry));
        } else {
            PageHolderScreen pageHolderScreen = new PageHolderScreen(entry);
            pageHolderScreen.arrowIndex = pageIndex < entry.pages().size() ? pageIndex : 0;
            if (!(DocPlayerData.previousScreen instanceof PageHolderScreen pageHolderScreen1 && pageHolderScreen1.entry == entry)) {
                pageHolderScreen.previousScreen = DocPlayerData.previousScreen;
            }
            Minecraft.getInstance().setScreen(pageHolderScreen);
        }
    }

    public static void drawStringScaled(GuiGraphics graphics, Component component, int x, int y, int color, float scale, boolean shadow) {
        // 1.21.11: GuiGraphics.pose() returns Matrix3x2fStack; use pushMatrix/popMatrix and translate(x,y)
        Matrix3x2fStack poseStack = graphics.pose();
        poseStack.pushMatrix();
        poseStack.translate(x + 3, y);
        // scale(x,y) omitted; Matrix3x2fStack.scale only takes (x,y) but draws distorted — skipping for compatibility
        // 1.21.11: drawString skips if alpha==0. Ensure opaque.
        int opaqueColor = (color & 0xFF000000) == 0 ? (color | 0xFF000000) : color;
        graphics.drawString(Minecraft.getInstance().font, component.copy().withStyle(component.getStyle().withFont(new FontDescription.Resource(Minecraft.UNIFORM_FONT))), 0, 0, opaqueColor, shadow);
        poseStack.popMatrix();
    }


    public static void drawHeader(NuggetMultilLineLabel title, GuiGraphics graphics, int x, int y) {
        title.renderCenteredNoShadow(graphics, x, y + (title.getLineCount() > 1 ? 3 : 7), 8, 0xFF000000);
    }

    public static void blit(GuiGraphics graphics, DocAssets.BlitInfo info, int x, int y) {
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, info.location(), x, y, info.u(), info.v(), info.width(), info.height(), info.width(), info.height());
    }

    public static ItemStack renderIngredientAtAngle(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float angle, Ingredient ingredient) {
        if (ingredient.isEmpty()) {
            return ItemStack.EMPTY;
        }

        angle -= 90;
        int radius = 41;
        double xPos = x + nextXAngle(angle, radius);
        double yPos = y + nextYAngle(angle, radius);
        // 1.21.11: GuiGraphics.pose() returns Matrix3x2fStack; use pushMatrix/popMatrix and translate(x,y)
        Matrix3x2fStack ms = graphics.pose();
        ms.pushMatrix(); // This translation makes it not stuttery. It does not affect the tooltip as that is drawn separately later.
        ms.translate((float)(xPos - (int) xPos), (float)(yPos - (int) yPos));
        ItemStack hovered = DocClientUtils.renderIngredient(graphics, (int) xPos, (int) yPos, mouseX, mouseY, ingredient);
        ms.popMatrix();
        return hovered;
    }

    public static double nextXAngle(double angle, int radius) {
        return Math.cos(angle * Math.PI / 180D) * radius + 32;
    }

    public static double nextYAngle(double angle, int radius) {
        return Math.sin(angle * Math.PI / 180D) * radius + 32;
    }

    /**
     * @return returns the hovered stack
     */
    public static ItemStack renderIngredient(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, Ingredient ingr) {
        // items() replaced getItems() in MC 1.21.11 — returns Stream<Holder<Item>>
        List<ItemStack> stacks = ingr.items().map(h -> h.value().getDefaultInstance()).collect(Collectors.toList());
        if (!stacks.isEmpty()) {
            return DocClientUtils.renderItemStack(graphics, x, y, mouseX, mouseY, stacks.get((ClientInfo.ticksInGame / 20) % stacks.size()));
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack renderItemStack(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Font font = Minecraft.getInstance().font;
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(font, stack, x, y);
        if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16)) {
            DocItemTooltipHandler.onTooltip(graphics, stack, mouseX, mouseY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static void drawHeader(@Nullable Component title, GuiGraphics guiGraphics, int x, int y, int width, int mouseX, int mouseY, float partialTick) {
        DocClientUtils.blit(guiGraphics, DocAssets.UNDERLINE, x, y + 9);
        if (title != null) {
            GuiHelpers.drawCenteredStringNoShadow(Minecraft.getInstance().font, guiGraphics, title, x + width / 2, y, 0);
        }
    }

    public static void drawHeaderNoUnderline(@Nullable Component title, GuiGraphics guiGraphics, int x, int y, int width, int mouseX, int mouseY, float partialTick) {
        if (title != null) {
            GuiHelpers.drawCenteredStringNoShadow(Minecraft.getInstance().font, guiGraphics, title, x + width / 2, y, 0);
        }
    }

    public static void drawParagraph(Component text, GuiGraphics guiGraphics, int x, int y, int width, int mouseX, int mouseY, float partialTick) {
        // 1.21.11: GuiGraphics.pose() returns Matrix3x2fStack; use pushMatrix/popMatrix and translate(x,y)
        Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.pushMatrix();
        poseStack.translate(x + 1, y);
        NuggetMultilLineLabel label = NuggetMultilLineLabel.create(Minecraft.getInstance().font, text.copy().withStyle(Style.EMPTY.withFont(new FontDescription.Resource(Minecraft.UNIFORM_FONT))), width);
        int lineHeight = 9;
        label.renderLeftAlignedNoShadow(guiGraphics, 0, 0, lineHeight, 0);
        poseStack.popMatrix();
    }

    public static void drawParagraph(NuggetMultilLineLabel label, GuiGraphics guiGraphics, int x, int y, int width, int mouseX, int mouseY, float partialTick) {
        int lineHeight = 9;
        label.renderLeftAlignedNoShadow(guiGraphics, x + 1, y, lineHeight, 0);
    }

    public static final int ROWS_FOR_TITLE_PAGE = 14;
    public static final int ROWS_FOR_NORMAL_PAGE = 17;

    public static final int PARAGRAPH_WIDTH = 118;

    public static List<NuggetMultilLineLabel> splitToFitFullPage(Component text) {
        return DocClientUtils.splitToFitPageWithOffset(text, ROWS_FOR_NORMAL_PAGE, ROWS_FOR_NORMAL_PAGE);
    }

    public static List<NuggetMultilLineLabel> splitToFitTitlePage(Component text) {
        return DocClientUtils.splitToFitPageWithOffset(text, ROWS_FOR_TITLE_PAGE, ROWS_FOR_NORMAL_PAGE);
    }


    public static List<NuggetMultilLineLabel> splitToFitPageWithOffset(Component text, int firstMaxRows, int secondMaxRows) {

        Font font = Minecraft.getInstance().font;
        List<FormattedText> list = Lists.newArrayList();
        String content = text.getString();
        font.getSplitter().splitLines(content, PARAGRAPH_WIDTH, Style.EMPTY.withFont(new FontDescription.Resource(Minecraft.UNIFORM_FONT)), true, (style, currentPos, width) -> {
            String s2 = content.substring(currentPos, width);
            boolean addLine = false;
            if (StringUtils.endsWith(s2, "\n")) {
                s2 = StringUtils.stripEnd(s2, "\n").trim();
                addLine = true;
            }
            if (!s2.isEmpty()) {
                list.add(FormattedText.of(s2, style));
                if (addLine) {
                    list.add(FormattedText.of(" ", style));
                }
            }
        });

        List<NuggetMultilLineLabel> labels = new ArrayList<>();

        List<FormattedText> firstList = list.subList(0, Math.min(firstMaxRows, list.size()));
        NuggetMultilLineLabel firstLabel = NuggetMultilLineLabel.create(font, PARAGRAPH_WIDTH, formattedToComponent(firstList).toArray(new Component[0]));

        labels.add(firstLabel);

        // Split list into sublists of size maxRows
        for (int i = firstMaxRows; i < list.size(); i += secondMaxRows) {
            int end = Math.min(i + secondMaxRows, list.size());
            List<Component> sublist = formattedToComponent(list.subList(i, end));
            // Removes empty lines at the start of the sublist and recalculates the end index so we have no gaps
            while (true) {
                if (sublist.isEmpty()) {
                    break;
                }
                if (sublist.getFirst().getString().trim().isEmpty()) {
                    sublist.removeFirst();
                    i++;
                    end = Math.min(i + secondMaxRows, list.size());
                    sublist = formattedToComponent(list.subList(i, end));
                } else {
                    break;
                }
            }

            NuggetMultilLineLabel label = NuggetMultilLineLabel.create(font, PARAGRAPH_WIDTH, sublist.toArray(new Component[0]));
            labels.add(label);
        }
        return labels;
    }

    private static List<Component> formattedToComponent(List<FormattedText> list) {
        List<Component> components = new ArrayList<>();
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            FormattedText formatted = list.get(i);
            Component component = Component.literal(formatted.getString()).withStyle(Style.EMPTY.withFont(new FontDescription.Resource(Minecraft.UNIFORM_FONT)));
            components.add(component);
        }
        return components;
    }

}
