package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class CraftingButton extends GuiImageButton {
    protected AbstractSpellPart abstractSpellPart;
    protected AbstractSpellPart augmentedParent;
    public List<SpellValidationError> validationErrors;
    public int slotNum;

    public CraftingButton(int x, int y, Button.OnPress onPress, int slotNum) {
        super(x, y, 0, 0, 22, 20, 22, 20, "textures/gui/spell_glyph_slot.png", onPress);
        this.validationErrors = new LinkedList<>();
        abstractSpellPart = null;
        augmentedParent = null;
        this.slotNum = slotNum;
    }

    public void clear() {
        this.validationErrors.clear();
        this.abstractSpellPart = null;
        this.augmentedParent = null;
    }

    public @Nullable AbstractSpellPart getAbstractSpellPart() {
        return this.abstractSpellPart;
    }

    public void setAbstractSpellPart(AbstractSpellPart abstractSpellPart) {
        this.abstractSpellPart = abstractSpellPart;
    }

    public void setAugmenting(@Nullable AbstractSpellPart parent) {
        this.augmentedParent = parent;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (validationErrors.isEmpty()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            RenderSystem.setShaderColor(1.0F, 0.7F, 0.7F, 1.0F);
        }
        if (this.abstractSpellPart != null) {
            RenderUtils.drawSpellPart(this.abstractSpellPart, graphics, x + 3, y + 2, 16, !validationErrors.isEmpty(), 0);
        }
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (abstractSpellPart != null) {
            tooltip.add(Component.translatable(abstractSpellPart.getLocalizationKey()));
            for (SpellValidationError ve : validationErrors) {
                tooltip.add(ve.makeTextComponentExisting().withStyle(ChatFormatting.RED));
            }

            if(abstractSpellPart instanceof AbstractAugment augment && augmentedParent != null) {
                if(validationErrors != null && !validationErrors.isEmpty()){
                    return;
                }
                Component augmentDescription = augmentedParent.augmentDescriptions.get(augment);
                if (augmentDescription != null) {
                    tooltip.add(Component.translatable("ars_nouveau.augmenting", augmentedParent.getLocaleName()));
                    tooltip.add(augmentDescription.copy().withStyle(ChatFormatting.GOLD));
                }
            }
        }
    }
}
