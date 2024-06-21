package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.GuiUtils;
import com.hollingsworth.arsnouveau.client.gui.book.GlyphUnlockMenu;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.client.ClientHooks;
import java.util.ArrayList;
import java.util.List;

public class ItemButton extends GuiImageButton {
    public Ingredient ingredient = Ingredient.of();
    public GlyphUnlockMenu parent;
    public ItemButton(GlyphUnlockMenu parent, int x, int y) {
        super(x, y, 0, 0, 22, 20, 22, 20, "textures/gui/spell_glyph_slot.png", (b) -> {});
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics graphics, int parX, int parY, float partialTicks) {
        if (visible) {
            if (ingredient != null && ingredient.getItems().length != 0) {
                ItemStack stack = ingredient.getItems()[(ClientInfo.ticksInGame / 20) % ingredient.getItems().length];
                if (GuiUtils.isMouseInRelativeRange(parX, parY, x, y, width, height)) {
                    Font font = Minecraft.getInstance().font;
                    List<ClientTooltipComponent> components = new ArrayList<>(ClientHooks.gatherTooltipComponents(ItemStack.EMPTY, Screen.getTooltipFromItem(Minecraft.getInstance(), stack), parX, width, height, font));
                    parent.renderTooltipInternal(graphics, components, parX, parY);
                }
                RenderUtils.drawItemAsIcon(stack, graphics, x + 3, y + 2, 16, false);
            }
        }
        super.render(graphics, parX, parY, partialTicks);
    }
}
