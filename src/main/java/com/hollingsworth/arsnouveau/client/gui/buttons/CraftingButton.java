package com.hollingsworth.arsnouveau.client.gui.buttons;

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
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;

public class CraftingButton extends GuiImageButton {
    public int slotNum;
    public ResourceLocation spellTag;
    public AbstractSpellPart abstractSpellPart;
    public List<SpellValidationError> validationErrors;

    public CraftingButton(GuiSpellBook parent, int x, int y, int slotNum, Button.OnPress onPress) {
        super(x, y, 0, 0, 22, 20, 22, 20, "textures/gui/spell_glyph_slot.png", onPress);
        this.slotNum = slotNum;
        this.spellTag = ArsNouveauAPI.EMPTY_KEY;
        this.resourceIcon = "";
        this.validationErrors = new LinkedList<>();
        this.parent = parent;
    }

    public void clear() {
        this.spellTag = ArsNouveauAPI.EMPTY_KEY;
        this.resourceIcon = "";
        this.validationErrors.clear();
        this.abstractSpellPart = null;
    }

    @Override
    public void render(PoseStack ms, int parX, int parY, float partialTicks) {
        if (visible) {
            if (validationErrors.isEmpty()) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                RenderSystem.setShaderColor(1.0F, 0.7F, 0.7F, 1.0F);
            }
            //GuiSpellBook.drawFromTexture(new ResourceLocation(ExampleMod.MODID, this.resourceIcon), x, y, 0, 0, 20, 20, 20, 20);
            if (this.abstractSpellPart != null) {
                RenderUtils.drawSpellPart(this.abstractSpellPart, ms, x + 3, y + 2, 16, !validationErrors.isEmpty());
            }
            if (parent.isMouseInRelativeRange(parX, parY, x, y, width, height)) {

                if (parent.api.getSpellpartMap().containsKey(this.spellTag)) {
                    List<Component> tooltip = new LinkedList<>();
                    tooltip.add(Component.translatable(parent.api.getSpellpartMap().get(this.spellTag).getLocalizationKey()));
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
