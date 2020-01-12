package com.hollingsworth.craftedmagic.client.gui;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
class GuiSpellSlot extends GuiImageButton{

    public boolean isCraftingSlot;
    public String resourceIcon;
    public String spell_id; //Reference to a spell ID for spell crafting
    public int id;
    public String tooltip = "tooltip";

    GuiSpellCreation parent;

    public GuiSpellSlot(GuiSpellCreation parent, int x, int y,boolean isCraftingSlot, String resource_image,  String spell_id) {
        super(x, y, 0, 0, 18, 18, "textures/gui/spell_cell.png", parent::onCraftingSlotClick);
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = 18;
        this.height = 18;
        this.isCraftingSlot = isCraftingSlot;
        this.resourceIcon = resource_image;
        this.spell_id = spell_id;
        this.id = 0;
        System.out.println("x:" + x + " y:" + y);
    }

    public GuiSpellSlot(GuiSpellCreation parent, int x, int y,boolean isCraftingSlot, String resource_image,  String spell_id, Integer id) {
        this(parent, x, y, isCraftingSlot, resource_image, spell_id);
        this.id = id;
    }



//
//    public GuiSpellSlot(int parPosX, int parPosY,boolean isCraftingSlot, String resource_image, String spell_id, Button.IPressable pressable) {
//        this(parPosX, parPosY, isCraftingSlot, resource_image,spell_id, pressable);
//
//    }


    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
//        System.out.println(mouseX);
//        System.out.println(mouseY);


        if (visible)
        {
            if(this.resourceIcon != null && !this.resourceIcon.equals("")) {
                GlStateManager.color3f(1F, 1F, 1F);
//                Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(ExampleMod.MODID, "textures/gui/spells/" + this.resourceIcon));
//                blit(x + 2, y + 2, 0, 0, 16, 16);
                GuiSpellCreation.drawFromTexture(new ResourceLocation(ExampleMod.MODID, "textures/gui/spells/" + this.resourceIcon), x+2, y+2, 0, 0, 16, 16 );
            }
//
//            if(parent != null && parent.isMouseInRelativeRange(mouseX, mouseY,x, y,  16, 16)){
//                List<String> test = new ArrayList<>();
//                test.add("Memes");
//                parent.tooltip = test;
//            }mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height)

            if(parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height)){

                if(parent.api.spell_map.containsKey(this.spell_id)) {
                    List<String> test = new ArrayList<>();
                    test.add(parent.api.spell_map.get(this.spell_id).description);
                    parent.tooltip = test;
                }
            }

        }
        super.render(mouseX, mouseY, partialTicks);
    }

}