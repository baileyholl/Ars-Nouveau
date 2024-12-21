package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.nuggets.client.gui.ItemButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AugmentIcon extends ItemButton {

    AbstractSpellPart parent;


    public AugmentIcon(AbstractSpellPart parent, int x, int y, int w, int h, @NotNull Component text, OnPress onPress, Ingredient ingredient, Screen screen) {
        super(x, y, w, h, text, onPress, ingredient, screen);
        this.parent = parent;
    }

    public AugmentIcon(AbstractSpellPart parent, int x, int y, int w, int h, @NotNull Component text, OnPress onPress, ItemStack stack, Screen screen) {
        super(x, y, w, h, text, onPress, stack, screen);
        this.parent = parent;
    }

    @Override
    public void gatherTooltips(GuiGraphics graphics, int mouseX, int mouseY, List<Component> tooltip) {
        gatherTooltips(tooltip);
    }

    @Override
    public void gatherTooltips(List<Component> tooltip) {
        if( parent != null && this.ingredient.getItems()[0].getItem() instanceof Glyph glyph) {
            AbstractSpellPart spellPart = glyph.spellPart;
            tooltip.add(Component.translatable(spellPart.getLocalizationKey()));

            Component augmentDescription = parent.augmentDescriptions.get(spellPart);
            if (augmentDescription != null) {
                tooltip.add(Component.translatable("ars_nouveau.augmenting", parent.getLocaleName()));
                tooltip.add(augmentDescription.copy().withStyle(ChatFormatting.GOLD));
            }

            if (Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("tooltip.ars_nouveau.glyph_level", spellPart.getConfigTier().value).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
                tooltip.add(spellPart.getBookDescLang());
            } else {
                tooltip.add(Component.translatable("tooltip.ars_nouveau.hold_shift", Minecraft.getInstance().options.keyShift.getKey().getDisplayName()));
                var modName = ModList.get()
                        .getModContainerById(spellPart.getRegistryName().getNamespace())
                        .map(ModContainer::getModInfo)
                        .map(IModInfo::getDisplayName).orElse(spellPart.getRegistryName().getNamespace());
                tooltip.add(Component.literal(modName).withStyle(ChatFormatting.BLUE));
            }
        }
    }
}
