package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CreateSpellButton extends GuiImageButton {
    private final ResourceLocation image = new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_icon.png");

    public CreateSpellButton(BaseBook parent, int x, int y, Button.OnPress onPress) {
        super(x, y, 0,0,50, 12, 50, 12, "textures/gui/create_icon.png", onPress);
        this.parent = parent;
    }

    @Override
    public void render(PoseStack ms, int parX, int parY, float partialTicks) {
        if (visible) {
            if (parent.validationErrors.isEmpty()) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                RenderSystem.setShaderColor(1.0F, 0.7F, 0.7F, 1.0F);
            }

            GuiSpellBook.drawFromTexture(image, x, y, u, v, width, height, image_width, image_height, ms);

            if (parent.isMouseInRelativeRange(parX, parY, x, y, width, height)) {
                if (!parent.validationErrors.isEmpty()) {
                    List<Component> tooltip = new ArrayList<>();
                    boolean foundGlyphErrors = false;

                    tooltip.add(Component.translatable("ars_nouveau.spell.validation.crafting.invalid").withStyle(ChatFormatting.RED));

                    // Add any spell-wide errors
                    for (SpellValidationError error : parent.validationErrors) {
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

                    parent.tooltip = tooltip;
                }
            }
        }
        // We've handled all of the required rendering at this point.  No need to call super.
        //super.render(ms, parX, parY, partialTicks);
    }
}
