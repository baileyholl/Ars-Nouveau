package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.SchoolTooltip;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class GlyphButton extends ANButton {

    public AbstractSpellPart abstractSpellPart;
    public List<SpellValidationError> validationErrors;
    public AbstractSpellPart augmentingParent;

    public GlyphButton(int x, int y, AbstractSpellPart abstractSpellPart, OnPress onPress) {
        super(x, y, 16, 16, onPress);
        this.abstractSpellPart = abstractSpellPart;
        this.validationErrors = new LinkedList<>();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!visible) {
            return;
        }
        RenderUtils.drawSpellPart(this.abstractSpellPart, graphics, x, y, 16, !validationErrors.isEmpty(), 0);
    }

    @Override
    public void getTooltip(List<Component> tip) {
        if (!GlyphRegistry.getSpellpartMap().containsKey(this.abstractSpellPart.getRegistryName())) {
            return;
        }
        AbstractSpellPart spellPart = GlyphRegistry.getSpellpartMap().get(this.abstractSpellPart.getRegistryName());
        tip.add(Component.translatable(spellPart.getLocalizationKey()));
        for (SpellValidationError ve : validationErrors) {
            tip.add(ve.makeTextComponentAdding().withStyle(ChatFormatting.RED));
        }
        if (Screen.hasShiftDown()) {
            tip.add(Component.translatable("tooltip.ars_nouveau.glyph_level", spellPart.getConfigTier().value).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
            tip.add(Component.translatable("ars_nouveau.schools"));
            for (SpellSchool s : spellPart.spellSchools) {
                tip.add(s.getTextComponent());
            }
            tip.add(spellPart.getBookDescLang());
        } else {
            tip.add(Component.translatable("tooltip.ars_nouveau.hold_shift", Minecraft.getInstance().options.keyShift.getKey().getDisplayName()));
            var modName = ModList.get()
                    .getModContainerById(spellPart.getRegistryName().getNamespace())
                    .map(ModContainer::getModInfo)
                    .map(IModInfo::getDisplayName).orElse(spellPart.getRegistryName().getNamespace());
            tip.add(Component.literal(modName).withStyle(ChatFormatting.BLUE));
        }
        if (this.abstractSpellPart instanceof AbstractAugment augment && this.augmentingParent != null) {
            if (validationErrors != null && !validationErrors.isEmpty()) {
                return;
            }
            Component augmentDescription = augmentingParent.augmentDescriptions.get(augment);
            if (augmentDescription != null) {
                tip.add(Component.translatable("ars_nouveau.augmenting", augmentingParent.getLocaleName()));
                tip.add(augmentDescription.copy().withStyle(ChatFormatting.GOLD));
            }
        }
    }

    @Override
    public TooltipComponent getTooltipImage() {
        return abstractSpellPart.spellSchools.isEmpty() ? null : new SchoolTooltip(abstractSpellPart);
    }
}