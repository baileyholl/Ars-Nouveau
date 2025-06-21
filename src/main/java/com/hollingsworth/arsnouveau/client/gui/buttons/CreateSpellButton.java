package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import java.util.List;
import java.util.function.Supplier;

public class CreateSpellButton extends GuiImageButton {

    public Supplier<List<SpellValidationError>> errors;

    public CreateSpellButton(int x, int y, Button.OnPress onPress, Supplier<List<SpellValidationError>> errors) {
        super(x, y, DocAssets.SAVE_BUTTON, onPress);
        this.errors = errors;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(graphics, pMouseX, pMouseY, pPartialTick);
        if (errors.get().isEmpty()) {
            graphics.drawString(Minecraft.getInstance().font, Component.translatable("ars_nouveau.spell_book_gui.create"), x + 18, y + 4, BaseBook.FONT_COLOR, false);
        } else {
            Component textComponent = Component.translatable("ars_nouveau.spell_book_gui.create")
                    .withStyle(s -> s.withStrikethrough(true).withColor(TextColor.parseColor("#FFB2B2").getOrThrow()));
            graphics.drawString(Minecraft.getInstance().font, textComponent, x + 18, y + 4, -8355712, false);
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        if (!errors.get().isEmpty()) {
            boolean foundGlyphErrors = false;
            tooltip.add(Component.translatable("ars_nouveau.spell.validation.crafting.invalid").withStyle(ChatFormatting.RED));

            // Add any spell-wide errors
            for (SpellValidationError error : errors.get()) {
                if (error.getPosition() < 0) {
                    tooltip.add(error.makeTextComponentExisting());
                } else {
                    foundGlyphErrors = true;
                }
            }
            // Show a single placeholder for all the per-glyph errors
            if (foundGlyphErrors) {
                tooltip.add(Component.translatable("ars_nouveau.spell.validation.crafting.invalid_glyphs"));
            }
        }
    }
}
