package com.hollingsworth.craftedmagic.client.gui;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.api.CraftedMagicAPI;
import com.hollingsworth.craftedmagic.client.gui.buttons.GuiImageButton;
import com.hollingsworth.craftedmagic.client.gui.buttons.GuiSpellCell;
import com.hollingsworth.craftedmagic.client.gui.buttons.GuiSpellSlot;
import com.hollingsworth.craftedmagic.items.Spell;
import com.hollingsworth.craftedmagic.network.Networking;
import com.hollingsworth.craftedmagic.network.PacketUpdateSpellbook;
import com.hollingsworth.craftedmagic.spell.effect.EffectType;
import com.hollingsworth.craftedmagic.spell.method.CastMethod;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiSpellCreation extends ModdedScreen {

    private final int FULL_WIDTH = 256;
    private final int FULL_HEIGHT = 192;
    private static ResourceLocation background = new ResourceLocation(ExampleMod.MODID, "textures/gui/spell_creation.png");
    public int numLinks = 5;
    public int bookLeft;
    public int bookTop;
    public int bookRight;
    public Spell spellBook;
    public CraftedMagicAPI api;
    private int offsetFromScreenLeft;
    private int offsetFromScreenTop;
    private int selected_cast_slot;
    TextFieldWidget spell_name;
    public CompoundNBT spell_book_tag;
    public GuiSpellSlot selected_slot;

    List<GuiSpellCell> craftingCells;

    public GuiSpellCreation(CraftedMagicAPI api, CompoundNBT tag) {
        super(new StringTextComponent(""));
        this.api = api;
        selected_cast_slot = 1;
        craftingCells = new ArrayList<>();
        this.spell_book_tag = tag;
    }

    @Override
    public void init() {
        super.init();

        // DEBUG
        offsetFromScreenLeft = (width - FULL_WIDTH) / 2;
        offsetFromScreenTop = (height - FULL_HEIGHT) / 2;
        bookLeft = width / 2 - FULL_WIDTH / 2;
        bookTop = height / 2 - FULL_HEIGHT / 2;
        bookRight = width / 2 + FULL_WIDTH / 2;

        int bookBottom = height / 2 + FULL_HEIGHT / 2;

        //Crafting slots
        for (int i = 0; i <= numLinks; i++) {
            String icon = null;
            String spell_id = "";
            GuiSpellCell cell = new GuiSpellCell(this,bookLeft + 10 + 28 * i, bookTop + FULL_HEIGHT - 24, true, icon, spell_id, i+1);
            addButton(cell);
            craftingCells.add(cell);
        }
        updateCraftingSlots(1);

        addSpellParts();
        addButton(new GuiImageButton(bookRight - 43, bookBottom - 22, 0,0,38, 16, "textures/gui/create.png", this::onCreateClick));
        spell_name = new TextFieldWidget(minecraft.fontRenderer, bookLeft + 6, bookTop + FULL_HEIGHT - 42, 100, 12, null, "Spell Name");
        spell_name.setText(Spell.getSpellName(spell_book_tag, 1));

        addButton(spell_name);

        // Add spell slots
        for(int i = 1; i <= 3; i++){
            GuiSpellSlot slot = new GuiSpellSlot(this,bookLeft + 220, bookTop   + 20 * i, "textures/gui/spell_cell.png", i);
            if(i == 1) {
                selected_slot = slot;
                slot.isSelected = true;
            }
            addButton(slot);
        }

    }

    public void addSpellParts(){
        Set<String> keys = this.api.spell_map.keySet();
        //Adding spell parts
        int numCast = 1;
        int numEffect =1;
        int numEnhancement = 1;
        for(String key  : keys){
            AbstractSpellPart spell = this.api.spell_map.get(key);
            GuiSpellCell cell;
            if(spell instanceof CastMethod) {
                cell = new GuiSpellCell(this, bookLeft + 20, bookTop + 10 + 18 * numCast++, false, spell.getIcon(), spell.tag);
            }else if(spell instanceof EffectType){
                int yOffset = numEffect % 2 == 0 ? 18 * (numEffect/2 -1) : 18*(numEffect/2);
                int xOffset = (numEffect % 2 == 0 ? 20 : 0);

                cell = new GuiSpellCell(this, bookLeft + 75 + xOffset, bookTop + 28 +  yOffset, false, spell.getIcon(), spell.tag);
                numEffect ++;
            }else{
                cell = new GuiSpellCell(this, bookLeft + 20 + 95, bookTop + 10 + 18 * numEnhancement++, false, spell.getIcon(), spell.tag);
            }
            addButton(cell);
        }
    }

    public void onCraftingSlotClick(Button button){
        if(!(button instanceof GuiSpellCell)) {
            throw new IllegalStateException("Wrong button type passed.");
        }

        if(!((GuiSpellCell) button).isCraftingSlot){
            List<Widget> test = this.buttons.stream().filter(b-> b instanceof GuiSpellCell && ((GuiSpellCell) b).getId() != 0).collect(Collectors.toList());
            for(Widget b : test){
                if(((GuiSpellCell) b).spell_id.equals("") || ((GuiSpellCell) b).spell_id == null){
                    ((GuiSpellCell) b).spell_id = ((GuiSpellCell) button).spell_id;
                    ((GuiSpellCell) b).resourceIcon = this.api.spell_map.get(((GuiSpellCell) button).spell_id).getIcon();
                    break;
                }
            }
        }else if(((GuiSpellCell) button).isCraftingSlot){
            ((GuiSpellCell) button).spell_id = "";
            ((GuiSpellCell) button).resourceIcon = "";
        }
    }

    public void onSlotChange(Button button){
        this.selected_slot.isSelected = false;
        this.selected_slot = (GuiSpellSlot) button;
        this.selected_slot.isSelected = true;
        this.selected_cast_slot = this.selected_slot.slotNum;
        updateCraftingSlots(this.selected_cast_slot);
        spell_name.setText(Spell.getSpellName(spell_book_tag, this.selected_cast_slot));
    }

    public void updateCraftingSlots(int bookSlot){
        //Crafting slots
        ArrayList<AbstractSpellPart> spell_recipe = this.spell_book_tag != null ? Spell.getRecipeFromTag(spell_book_tag, bookSlot) : null;
        for (int i = 0; i < craftingCells.size(); i++) {
            GuiSpellCell slot = craftingCells.get(i);
            if (slot.isCraftingSlot) {
                slot.spell_id = "";
                slot.resourceIcon = "";
            }
            if (spell_recipe != null && i < spell_recipe.size()){
                slot.spell_id = spell_recipe.get(i).getTag();
                slot.resourceIcon = spell_recipe.get(i).getIcon();
            }
        }
    }

    public void onCreateClick(Button button){
        List<String> ids = new ArrayList<>();
        for(GuiSpellCell slot : craftingCells){
            ids.add(slot.spell_id);
        }
        Networking.INSTANCE.sendToServer(new PacketUpdateSpellbook(ids.toString(), this.selected_cast_slot, this.spell_name.getText()));

    }

    public static void open(CraftedMagicAPI api, CompoundNBT spell_book_tag){ Minecraft.getInstance().displayGuiScreen(new GuiSpellCreation(api, spell_book_tag)); }

    final void drawScreenAfterScale(int mouseX, int mouseY, float partialTicks) {
        resetTooltip();
        renderBackground();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(bookLeft, bookTop, 0);
        GlStateManager.color3f(1F, 1F, 1F);
        drawBackgroundElements(mouseX, mouseY, partialTicks);
        drawForegroundElements(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
        super.render(mouseX, mouseY, partialTicks);
        drawTooltip(mouseX, mouseY);
    }

    final void drawBackgroundElements(int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().textureManager.bindTexture(background);
        blit(0, 0, 0, 0, FULL_WIDTH, FULL_HEIGHT);
        minecraft.fontRenderer.drawSplitString("Cast Type", 10, 10, 116, 0);
        minecraft.fontRenderer.drawSplitString("Effect", 80, 10, 116, 0);
        minecraft.fontRenderer.drawSplitString("Enhancement", 130, 10, 116, 0);
        minecraft.fontRenderer.drawSplitString("Slot", 220, 10, 116, 0);
        minecraft.fontRenderer.drawSplitString("Create", 214, 172, 116, 0);
    }

    public static void drawFromTexture(ResourceLocation resourceLocation, int x, int y, int u, int v, int w, int h) {
        Minecraft.getInstance().textureManager.bindTexture(resourceLocation);
        blit(x, y, u, v, w, h, 256, 256);
    }

    private void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }


    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        if(scaleFactor != 1) {
            GlStateManager.scalef(scaleFactor, scaleFactor, scaleFactor);

            mouseX /= scaleFactor;
            mouseY /= scaleFactor;
        }
        drawScreenAfterScale(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
    }

}