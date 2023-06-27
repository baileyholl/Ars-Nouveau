package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GlyphButton extends Button {

    private final int id;
    public boolean isCraftingSlot;
    public AbstractSpellPart abstractSpellPart;
    public String tooltip = "tooltip";
    public List<SpellValidationError> validationErrors;
    GuiSpellBook parent;

    public GlyphButton(GuiSpellBook parent, int x, int y, boolean isCraftingSlot, AbstractSpellPart abstractSpellPart) {
        super(x, y, 16, 16, Component.nullToEmpty(""), parent::onGlyphClick, Button.DEFAULT_NARRATION);
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.isCraftingSlot = isCraftingSlot;
        this.abstractSpellPart = abstractSpellPart;
        this.id = 0;
        this.validationErrors = new LinkedList<>();
    }

    public int getId() {
        return id;
    }



    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if(!visible){
            return;
        }

        RenderUtils.drawSpellPart(this.abstractSpellPart, graphics, x, y, 16, !validationErrors.isEmpty());
        if (parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height)) {
            if (parent.api.getSpellpartMap().containsKey(this.abstractSpellPart.getRegistryName())) {
                List<Component> tip = new ArrayList<>();
                AbstractSpellPart spellPart = parent.api.getSpellpartMap().get(this.abstractSpellPart.getRegistryName());
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

                parent.tooltip = tip;
            }
        }

    }

}