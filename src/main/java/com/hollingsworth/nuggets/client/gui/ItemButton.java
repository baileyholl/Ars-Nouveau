package com.hollingsworth.nuggets.client.gui;

import com.hollingsworth.nuggets.client.NuggetClientData;
import com.hollingsworth.nuggets.client.rendering.RenderHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemButton extends BaseButton {
    public Ingredient ingredient;
    public Screen screen;
    public int scale = 16;

    public ItemButton(int x, int y, int w, int h, @NotNull Component text, OnPress onPress, Ingredient ingredient, Screen screen) {
        super(x, y, w, h, text, onPress);
        this.ingredient = ingredient;
        this.screen = screen;
    }

    public ItemButton(int x, int y, int w, int h, @NotNull Component text, OnPress onPress, ItemStack stack, Screen screen) {
        // In 1.21.11 Ingredient.of(ItemStack) is gone; use DataComponentIngredient to preserve stack components
        this(x, y, w, h, text, onPress, net.neoforged.neoforge.common.crafting.DataComponentIngredient.of(false, stack), screen);
    }

    public ItemButton withScale(int scale) {
        this.scale = scale;
        return this;
    }

    /**
     * Returns the display items for this ingredient, cycling by ticks.
     * In 1.21.11, Ingredient.getItems() was removed; use items() stream instead.
     */
    protected ItemStack getDisplayStack() {
        if (ingredient == null) return ItemStack.EMPTY;
        List<ItemStack> stacks = ingredient.items().map(ItemStack::new).toList();
        if (stacks.isEmpty()) return ItemStack.EMPTY;
        return stacks.get((NuggetClientData.ticksInGame / 20) % stacks.size());
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        ItemStack stack = getDisplayStack();
        if (!stack.isEmpty()) {
            RenderHelpers.drawItemAsIcon(stack, graphics, getX(), getY(), scale, false);
        }
    }

    @Override
    public void gatherTooltips(GuiGraphics graphics, int mouseX, int mouseY, List<Component> tooltip) {
        super.gatherTooltips(graphics, mouseX, mouseY, tooltip);
        ItemStack stack = getDisplayStack();
        if (!stack.isEmpty()) {
            Font font = Minecraft.getInstance().font;
            List<ClientTooltipComponent> components = new ArrayList<>(GuiHelpers.gatherTooltipComponents(Screen.getTooltipFromItem(Minecraft.getInstance(), stack), mouseX, screen.width, screen.height, font));
            RenderHelpers.renderTooltipInternal(graphics, components, mouseX, mouseY, screen);
        }
    }
}
