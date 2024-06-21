package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import var;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class UnlockGlyphButton extends ANButton {
    public boolean isCraftingSlot;
    public AbstractSpellPart spellPart;

    public String tooltip = "";
    public GlyphRecipe recipe;
    public boolean playerKnows;
    public boolean selected;

    public UnlockGlyphButton(int x, int y, boolean isCraftingSlot, AbstractSpellPart spellPart, OnPress onPress) {
        super(x, y, 16, 16, onPress);
        this.isCraftingSlot = isCraftingSlot;
        this.spellPart = spellPart;
        Recipe recipe = Minecraft.getInstance().level.getRecipeManager().byKey(spellPart.getRegistryName()).orElse(null);
        this.recipe = recipe instanceof GlyphRecipe ? (GlyphRecipe) recipe : null;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            if (this.spellPart != null) {
                RenderUtils.drawSpellPart(this.spellPart, graphics, x, y, width, !playerKnows, 0);
                if (selected)
                    graphics.blit(new ResourceLocation(ArsNouveau.MODID, "textures/gui/glyph_selected.png"), x, y, 0, 0, 16, 16, 16, 16);
            }
        }
    }

    @Override
    public void getTooltip(List<Component> tip) {
        if(this.spellPart == null)
            return;
        AbstractSpellPart spellPart = this.spellPart;
        tip.add(Component.translatable(spellPart.getLocalizationKey()));
        if (Screen.hasShiftDown()) {
            tip.add(spellPart.getBookDescLang());
        } else {
            tip.add(Component.translatable("ars_nouveau.tier", spellPart.getConfigTier().value).withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
            tip.add(Component.translatable("tooltip.ars_nouveau.hold_shift", Minecraft.getInstance().options.keyShift.getKey().getDisplayName()));
            var modName = ModList.get()
                    .getModContainerById(spellPart.getRegistryName().getNamespace())
                    .map(ModContainer::getModInfo)
                    .map(IModInfo::getDisplayName).orElse(spellPart.getRegistryName().getNamespace());
            tip.add(Component.literal(modName).withStyle(ChatFormatting.BLUE));
        }
    }
}
