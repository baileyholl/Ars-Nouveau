package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.book.GlyphUnlockMenu;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class UnlockGlyphButton extends Button {
    public boolean isCraftingSlot;
    public String resourceIcon;
    public String spell_id; //Reference to a spell ID for spell crafting
    private int id;
    public String tooltip = "";
    public GlyphRecipe recipe;
    public boolean playerKnows;
    GlyphUnlockMenu parent;
    public boolean selected;
    public UnlockGlyphButton(GlyphUnlockMenu parent, int x, int y, boolean isCraftingSlot, String resource_image, String spell_id) {
        super(x, y,  16, 16, Component.nullToEmpty(""), parent::onGlyphClick);
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.isCraftingSlot = isCraftingSlot;
        this.resourceIcon = resource_image;
        this.spell_id = spell_id;
        this.id = 0;
        Recipe recipe = Minecraft.getInstance().level.getRecipeManager().byKey(new ResourceLocation("ars_nouveau:glyph_" + spell_id)).orElse(null);;
        this.recipe = recipe instanceof GlyphRecipe ? (GlyphRecipe) recipe : null;
    }

    public int getId() {
        return id;
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        if (visible)
        {
            if(this.resourceIcon != null && !this.resourceIcon.equals("")) {
                GL11.glEnable(GL11.GL_BLEND);
                if (playerKnows) {
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5f);
                }

                GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + this.resourceIcon), x, y, 0, 0, 16, 16,16,16 , ms);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glDisable(GL11.GL_BLEND);
                if(selected)
                    GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/glyph_selected.png"), x, y, 0, 0, 16, 16,16,16 , ms);
            }

            if(parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height)){
                if(parent.api.getSpellpartMap().containsKey(this.spell_id)) {
                    List<Component> tip = new ArrayList<>();
                    AbstractSpellPart spellPart = parent.api.getSpellpartMap().get(this.spell_id);
                    tip.add(new TranslatableComponent(spellPart.getLocalizationKey()));
                    if(Screen.hasShiftDown()){
                        tip.add(spellPart.getBookDescLang());
                    }else{
                        tip.add(new TranslatableComponent("tooltip.ars_nouveau.hold_shift"));
                    }
                    parent.tooltip = tip;
                    parent.hoveredRecipe = recipe;
                }
            }

        }
    }
}
