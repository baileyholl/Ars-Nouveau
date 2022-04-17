package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.book.GlyphUnlockMenu;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
    public AbstractSpellPart spellPart;
    private int id;
    public String tooltip = "";
    public GlyphRecipe recipe;
    public boolean playerKnows;
    GlyphUnlockMenu parent;
    public boolean selected;
    public UnlockGlyphButton(GlyphUnlockMenu parent, int x, int y, boolean isCraftingSlot, AbstractSpellPart spellPart) {
        super(x, y,  16, 16, Component.nullToEmpty(""), parent::onGlyphClick);
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.isCraftingSlot = isCraftingSlot;
        this.spellPart = spellPart;
        this.id = 0;
        Recipe recipe = Minecraft.getInstance().level.getRecipeManager().byKey(new ResourceLocation("ars_nouveau:glyph_" + spellPart.getId())).orElse(null);;
        this.recipe = recipe instanceof GlyphRecipe ? (GlyphRecipe) recipe : null;
    }

    public int getId() {
        return id;
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        if (visible)
        {
            if(this.spellPart != null) {
                if(!this.spellPart.isRenderAsIcon()) {
                    RenderUtils.drawItemAsIcon(this.spellPart.glyphItem, ms, x, y, 16, !playerKnows);
                }
                else {
                    GL11.glEnable(GL11.GL_BLEND);
                    if (playerKnows) {
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    } else {
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5f);
                    }

                    GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + this.spellPart.getIcon()), x, y, 0, 0, 16, 16,16,16 , ms);
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glDisable(GL11.GL_BLEND);
                }
                if(selected)
                    GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/glyph_selected.png"), x, y, 0, 0, 16, 16,16,16 , ms);
            }

            if(parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height)){
                if(parent.api.getSpellpartMap().containsKey(this.spellPart.getId())) {
                    List<Component> tip = new ArrayList<>();
                    AbstractSpellPart spellPart = parent.api.getSpellpartMap().get(this.spellPart.getId());
                    tip.add(new TranslatableComponent(spellPart.getLocalizationKey()));
                    if(Screen.hasShiftDown()){
                        tip.add(spellPart.getBookDescLang());
                    }else{
                        tip.add(new TranslatableComponent("ars_nouveau.tier", spellPart.getTier().value).withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
                        tip.add(new TranslatableComponent("tooltip.ars_nouveau.hold_shift"));
                    }
                    parent.tooltip = tip;
                    parent.hoveredRecipe = recipe;
                }
            }

        }
    }
}
