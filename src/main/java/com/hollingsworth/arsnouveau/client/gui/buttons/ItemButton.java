package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.client.gui.book.GlyphUnlockMenu;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.ArrayList;
import java.util.List;

public class ItemButton extends GuiImageButton {

    public String spellTag;
    public Ingredient ingredient = Ingredient.of();

    public ItemButton(BaseBook parent, int x, int y) {
        super(x, y, 0, 0, 22, 20, 22, 20, "textures/gui/spell_glyph_slot.png", (b) -> {
        });
        this.spellTag = "";
        this.resourceIcon = "";
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics graphics, int parX, int parY, float partialTicks) {
        if (visible) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            //GuiSpellBook.drawFromTexture(new ResourceLocation(ExampleMod.MODID, this.resourceIcon), x, y, 0, 0, 20, 20, 20, 20);
            if (!this.resourceIcon.equals("")) {
                GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/item/" + resourceIcon), x + 3, y + 2, u, v, 16, 16, 16, 16, graphics);
            }
            if (ingredient != null && ingredient.getItems().length != 0) {
                ItemStack stack = ingredient.getItems()[(ClientInfo.ticksInGame / 20) % ingredient.getItems().length];
                if (parent.isMouseInRelativeRange(parX, parY, x, y, width, height)) {
                    if (parent instanceof GlyphUnlockMenu menu) {
                        Font font = Minecraft.getInstance().font;
                        List<ClientTooltipComponent> components = new ArrayList<>(ForgeHooksClient.gatherTooltipComponents(ItemStack.EMPTY, Screen.getTooltipFromItem(Minecraft.getInstance(), stack), parX, width, height, font));
                        menu.renderTooltipInternal(graphics, components, parX, parY);
                    }
                }
                RenderUtils.drawItemAsIcon(stack.getItem(), graphics, x + 3, y + 2, 16, false);
            }
        }

        super.render(graphics, parX, parY, partialTicks);

    }
}
