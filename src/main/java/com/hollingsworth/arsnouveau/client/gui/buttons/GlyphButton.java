package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GlyphButton extends Button {

    public boolean isCraftingSlot;
    public String resourceIcon;
    public String spell_id; //Reference to a spell ID for spell crafting
    private int id;
    public String tooltip = "tooltip";

    GuiSpellBook parent;

    public GlyphButton(GuiSpellBook parent, int x, int y, boolean isCraftingSlot, String resource_image, String spell_id) {
        super(x, y,  16, 16, ITextComponent.nullToEmpty(""), parent::onGlyphClick);
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.isCraftingSlot = isCraftingSlot;
        this.resourceIcon = resource_image;
        this.spell_id = spell_id;
        this.id = 0;
    }

    public GlyphButton(GuiSpellBook parent, int x, int y, boolean isCraftingSlot, String resource_image, String spell_id, Integer id) {
        this(parent, x, y, isCraftingSlot, resource_image, spell_id);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

    @Override
    public void render(MatrixStack ms,int mouseX, int mouseY, float partialTicks) {
        if (visible)
        {
            if(this.resourceIcon != null && !this.resourceIcon.equals("")) {
                RenderSystem.color3f(1F, 1F, 1F);

                GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + this.resourceIcon), x, y, 0, 0, 16, 16,16,16 , ms);
            }

            if(parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height)){

                if(parent.api.getSpell_map().containsKey(this.spell_id)) {
                    List<String> test = new ArrayList<>();
                    test.add(parent.api.getSpell_map().get(this.spell_id).getLocaleName());
                    parent.tooltip = test;
                }
            }

        }
        //super.render(mouseX, mouseY, partialTicks);
    }

}