package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

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
    public void getTooltip(List<Component> tip) {
        if (abstractSpellPart != null) {
            tip.add(Component.translatable(abstractSpellPart.getLocalizationKey()));
            for (SpellValidationError ve : validationErrors) {
                tip.add(ve.makeTextComponentExisting().withStyle(ChatFormatting.RED));
            }
            if (abstractSpellPart instanceof AbstractAugment augment && augmentedParent != null) {
                if (validationErrors != null && !validationErrors.isEmpty()) {
                    return;
                }
                Component augmentDescription = augmentedParent.augmentDescriptions.get(augment);
                if (augmentDescription != null) {
                    tip.add(Component.translatable("ars_nouveau.augmenting", augmentedParent.getLocaleName()));
                    tip.add(augmentDescription.copy().withStyle(ChatFormatting.GOLD));
                }
            }
            if (Screen.hasShiftDown()) {
                tip.add(Component.translatable("tooltip.ars_nouveau.glyph_level", abstractSpellPart.getConfigTier().value).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
                tip.add(Component.translatable("ars_nouveau.schools"));
                for (SpellSchool s : abstractSpellPart.spellSchools) {
                    tip.add(s.getTextComponent());
                }
                tip.add(abstractSpellPart.getBookDescLang());
            } else {
                tip.add(Component.translatable("tooltip.ars_nouveau.hold_shift", Minecraft.getInstance().options.keyShift.getKey().getDisplayName()));
                var modName = ModList.get()
                        .getModContainerById(abstractSpellPart.getRegistryName().getNamespace())
                        .map(ModContainer::getModInfo)
                        .map(IModInfo::getDisplayName).orElse(abstractSpellPart.getRegistryName().getNamespace());
                tip.add(Component.literal(modName).withStyle(ChatFormatting.BLUE));
            }
        }
    }
}
