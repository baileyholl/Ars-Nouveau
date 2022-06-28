package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.client.gui.book.GlyphUnlockMenu;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class ItemButton extends GuiImageButton{

    public String spellTag;
    public Ingredient ingredient = Ingredient.of();

    public ItemButton(BaseBook parent, int x, int y) {
        super( x, y, 0, 0, 22, 20, 22, 20, "textures/gui/spell_glyph_slot.png", (b) -> {});
        this.spellTag = "";
        this.resourceIcon = "";
        this.parent = parent;
    }

    @Override
    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        super.renderToolTip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public void render(PoseStack ms, int parX, int parY, float partialTicks) {
        if (visible)
        {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            //GuiSpellBook.drawFromTexture(new ResourceLocation(ExampleMod.MODID, this.resourceIcon), x, y, 0, 0, 20, 20, 20, 20);
            if(!this.resourceIcon.equals("")){
                GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + resourceIcon), x + 3, y + 2, u, v, 16, 16, 16, 16,ms);
            }
            if (ingredient != null && ingredient.getItems().length != 0) {
                ItemStack stack = ingredient.getItems()[(ClientInfo.ticksInGame / 20) % ingredient.getItems().length];
                if(parent.isMouseInRelativeRange(parX, parY, x, y, width, height)){
                    if(parent instanceof GlyphUnlockMenu menu){
                        Font font = Minecraft.getInstance().font;
                        List<ClientTooltipComponent> components = new ArrayList<>(net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(ItemStack.EMPTY, parent.getTooltipFromItem(stack), parX, width, height, font, font));
                        menu.renderTooltipInternal(ms, components, parX, parY);
                    }
                }
                RenderUtils.drawItemAsIcon(stack.getItem(), ms, x + 3, y + 2, 16, false);
            }
        }

        super.render(ms, parX, parY, partialTicks);

    }
}
