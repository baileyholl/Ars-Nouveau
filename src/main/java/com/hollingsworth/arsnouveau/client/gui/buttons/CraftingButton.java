package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

public class CraftingButton extends GuiImageButton{
    int slotNum;
    public String spellTag;
    public AbstractSpellPart abstractSpellPart;
    public List<SpellValidationError> validationErrors;

    public CraftingButton(GuiSpellBook parent, int x, int y, int slotNum, Button.OnPress onPress) {
        super( x, y, 0, 0, 22, 20, 22, 20, "textures/gui/spell_glyph_slot.png", onPress);
        this.slotNum = slotNum;
        this.spellTag = "";
        this.resourceIcon = "";
        this.validationErrors = new LinkedList<>();
        this.parent = parent;
    }

    public void clear() {
        this.spellTag = "";
        this.resourceIcon = "";
        this.validationErrors.clear();
        this.abstractSpellPart = null;
    }

    @Override
    public void render(PoseStack ms, int parX, int parY, float partialTicks) {
        if (visible)
        {
            if (validationErrors.isEmpty()) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                RenderSystem.setShaderColor(1.0F, 0.7F, 0.7F, 1.0F);
            }
            //GuiSpellBook.drawFromTexture(new ResourceLocation(ExampleMod.MODID, this.resourceIcon), x, y, 0, 0, 20, 20, 20, 20);
            if(this.abstractSpellPart != null) {
                if (this.abstractSpellPart.isRenderAsIcon()) {
                    if (this.abstractSpellPart.getIcon() != null && !this.abstractSpellPart.getIcon().equals("")) {
                        GL11.glEnable(GL11.GL_BLEND);
                        if (validationErrors.isEmpty()) {
                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        } else {
                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.25F);
                        }

                        GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + this.abstractSpellPart.getIcon()), x, y, 0, 0, 16, 16, 16, 16, ms);
                        GL11.glDisable(GL11.GL_BLEND);
                    }
                } else {
                    Item glyphItem = ArsNouveauAPI.getInstance().getGlyphItemMap().get(abstractSpellPart.getId()).get();
                    RenderUtils.drawItemAsIcon(glyphItem, ms, x, y, 16, !validationErrors.isEmpty());
                }
            }
            if(parent.isMouseInRelativeRange(parX, parY, x, y, width, height)){

                if(parent.api.getSpellpartMap().containsKey(this.spellTag)) {
                    List<Component> tooltip = new LinkedList<>();
                    tooltip.add(new TranslatableComponent(parent.api.getSpellpartMap().get(this.spellTag).getLocalizationKey()));
                    for (SpellValidationError ve : validationErrors) {
                        tooltip.add(ve.makeTextComponentExisting().withStyle(ChatFormatting.RED));
                    }
                    parent.tooltip = tooltip;
                }
            }
        }
        super.render(ms, parX, parY, partialTicks);
    }
}
