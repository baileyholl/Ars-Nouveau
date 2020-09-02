package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.client.gui.buttons.CraftingButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiSpellSlot;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellbook;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GuiSpellBook extends ModdedScreen {

    private final int FULL_WIDTH = 272;
    private final int FULL_HEIGHT = 180;
    private static ResourceLocation background = new ResourceLocation(ArsNouveau.MODID, "textures/gui/spell_book.png");
    public int numLinks = 10;
    public int bookLeft;
    public int bookTop;
    public int bookRight;
    public SpellBook spellBook;
    public ArsNouveauAPI api;
    private int offsetFromScreenLeft;
    private int offsetFromScreenTop;
    private int selected_cast_slot;
    public TextFieldWidget spell_name;
    public CompoundNBT spell_book_tag;
    public GuiSpellSlot selected_slot;
    public int max_spell_tier; // Used to load spells that are appropriate tier
    List<CraftingButton> craftingCells;

    public ArrayList<AbstractSpellPart>unlockedSpells;
    public GuiSpellBook(ArsNouveauAPI api, CompoundNBT tag, int tier, String unlockedSpells) {
        super(new StringTextComponent(""));
        this.api = api;
        selected_cast_slot = 1;
        craftingCells = new ArrayList<>();
        this.max_spell_tier = tier;
        this.spell_book_tag = tag;
        this.unlockedSpells = SpellRecipeUtil.getSpellsFromString(unlockedSpells);
    }

    @Override
    public void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        // DEBUG
        offsetFromScreenLeft = (width - FULL_WIDTH) / 2;
        offsetFromScreenTop = (height - FULL_HEIGHT) / 2;
        bookLeft = width / 2 - FULL_WIDTH / 2;
        bookTop = height / 2 - FULL_HEIGHT / 2;
        bookRight = width / 2 + FULL_WIDTH / 2;
        int selected_slot_ind = SpellBook.getMode(spell_book_tag);
        if(selected_slot_ind == 0) selected_slot_ind = 1;
        int bookBottom = height / 2 + FULL_HEIGHT / 2;

        //Crafting slots
        for (int i = 0; i < numLinks; i++) {
            String icon = null;
            String spell_id = "";
            int offset = i >= 5 ? 5 : 0;
            CraftingButton cell = new CraftingButton(this,bookLeft +14 + 24 * i + offset, bookTop + FULL_HEIGHT - 50, i, this::onCraftingSlotClick);
            //GlyphButton glyphButton = new GlyphButton(this,bookLeft + 10 + 28 * i, bookTop + FULL_HEIGHT - 24, )
            addButton(cell);
            craftingCells.add(cell);
        }
        updateCraftingSlots(selected_slot_ind);

        addSpellParts();
        addButton(new GuiImageButton(bookRight - 70, bookBottom - 28, 0,0,46, 18, 46, 18, "textures/gui/create_button.png", this::onCreateClick));
        spell_name = new TextFieldWidget(minecraft.fontRenderer, bookLeft + 16, bookTop + FULL_HEIGHT - 25, 115, 12, null, "Spell Name");
        spell_name.setText(SpellBook.getSpellName(spell_book_tag, 1));
        if(spell_name.getText().isEmpty())
            spell_name.setSuggestion("My Spell");
//
        addButton(spell_name);
        // Add spell slots
        for(int i = 1; i <= 10; i++){
            GuiSpellSlot slot = new GuiSpellSlot(this,bookLeft + 261, bookTop - 3 + 15 * i, i);
            if(i == selected_slot_ind) {
                selected_slot = slot;
                slot.isSelected = true;
            }
            addButton(slot);
        }
    }

    public void addSpellParts(){
        Set<String> keys = this.api.getSpell_map().keySet();
        Collections.sort(unlockedSpells);

        //Adding spell parts
        int numCast = 0;
        int numEffect = 0;
        int numAugment = 0;
        for(AbstractSpellPart key  : unlockedSpells){
            AbstractSpellPart spell = this.api.getSpell_map().get(key.tag);
            GlyphButton cell;
            if(spell.getTier().ordinal() > max_spell_tier)
                continue; //Skip spells too high of a tier

            if(spell instanceof AbstractCastMethod) {
//                int xOffset = numCast % 2 == 0 ? 18 * (numCast/2 -1) : 18*(numCast/2);
                int xOffset = 18 * (numCast % 6 );
                int yOffset = (numCast / 6) * 20 ;
                cell = new GlyphButton(this, bookLeft + 15 + xOffset, bookTop + 20 + yOffset, false, spell.getIcon(), spell.tag);
                numCast++;
            }else if(spell instanceof AbstractEffect){
//                int xOffset = numEffect % 2 == 0 ? 18 * (numEffect/2 -1) : 18*(numEffect/2);
//                int yOffset = (numEffect % 2 == 0 ? 20 : 0);

                int xOffset = 20 * (numEffect % 6 );
                int yOffset = (numEffect / 6) * 20 ;
                cell = new GlyphButton(this, bookLeft + 140 + xOffset, bookTop + 20 +  yOffset, false, spell.getIcon(), spell.tag);
                numEffect ++;
            }else{
                int xOffset = 20 * (numAugment % 6 );
                int yOffset = (numAugment / 6) * 20 ;
                cell = new GlyphButton(this, bookLeft + 15 + xOffset, bookTop + 70 +  yOffset, false, spell.getIcon(), spell.tag);
                numAugment++;
            }
            addButton(cell);
        }
    }

    public void onCraftingSlotClick(Button button){
        ((CraftingButton) button).spellTag = "";
        ((CraftingButton) button).resourceIcon = "";
    }

    public void onGlyphClick(Button button){
        GlyphButton button1 = (GlyphButton) button;
        for(CraftingButton b : craftingCells){
            if(b.resourceIcon.equals("")){
                b.resourceIcon = button1.resourceIcon;
                b.spellTag = button1.spell_id;
                return;
            }
        }
    }

    public void onSlotChange(Button button){
        this.selected_slot.isSelected = false;
        this.selected_slot = (GuiSpellSlot) button;
        this.selected_slot.isSelected = true;
        this.selected_cast_slot = this.selected_slot.slotNum;
        updateCraftingSlots(this.selected_cast_slot);
        spell_name.setText(SpellBook.getSpellName(spell_book_tag, this.selected_cast_slot));
    }

    public void updateCraftingSlots(int bookSlot){
        //Crafting slots
        ArrayList<AbstractSpellPart> spell_recipe = this.spell_book_tag != null ? SpellBook.getRecipeFromTag(spell_book_tag, bookSlot) : null;
        for (int i = 0; i < craftingCells.size(); i++) {
            CraftingButton slot = craftingCells.get(i);
            slot.spellTag = "";
            slot.resourceIcon = "";
            if (spell_recipe != null && i < spell_recipe.size()){
                slot.spellTag = spell_recipe.get(i).getTag();
                slot.resourceIcon = spell_recipe.get(i).getIcon();
            }
        }
    }

    public void onCreateClick(Button button){
        List<String> ids = new ArrayList<>();
        for(CraftingButton slot : craftingCells){
            ids.add(slot.spellTag);
        }
        Networking.INSTANCE.sendToServer(new PacketUpdateSpellbook(ids.toString(), this.selected_cast_slot, this.spell_name.getText()));

    }

    public static void open(ArsNouveauAPI api, CompoundNBT spell_book_tag, int tier, String unlockedSpells){ Minecraft.getInstance().displayGuiScreen(new GuiSpellBook(api, spell_book_tag, tier, unlockedSpells)); }

    final void drawScreenAfterScale(int mouseX, int mouseY, float partialTicks) {
        resetTooltip();
        renderBackground();
        GlStateManager.pushMatrix();
        GlStateManager.translatef(bookLeft, bookTop, 0);
        RenderSystem.color3f(1F, 1F, 1F);
        drawBackgroundElements(mouseX, mouseY, partialTicks);
        drawForegroundElements(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
        super.render(mouseX, mouseY, partialTicks);
        drawTooltip(mouseX, mouseY);
    }

    final void drawBackgroundElements(int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().textureManager.bindTexture(background);
        int png_width = FULL_WIDTH;
        int png_height = FULL_HEIGHT;
        drawFromTexture(background,0, 0, 0, 0, FULL_WIDTH, FULL_HEIGHT, png_width, png_height);
        minecraft.fontRenderer.drawSplitString("Form", 15, 10, 116, 0);
        minecraft.fontRenderer.drawSplitString("Effect", 140, 10, 116, 0);
        minecraft.fontRenderer.drawSplitString("Augment", 15, 60, 116, 0);
        //minecraft.fontRenderer.drawSplitString("Slot", 220, 10, 116, 0);
        minecraft.fontRenderer.drawSplitString("Create", 208, 157, 116, 0);
    }

    public static void drawFromTexture(ResourceLocation resourceLocation, int x, int y, int u, int v, int w, int h) {
        Minecraft.getInstance().textureManager.bindTexture(resourceLocation);
        blit(x, y, u, v, w, h, w, h);
    }

    public static void drawFromTexture(ResourceLocation resourceLocation, int x, int y, int u, int v, int w, int h, int fileWidth, int fileHeight) {
        Minecraft.getInstance().textureManager.bindTexture(resourceLocation);
        blit(x, y, u, v, w, h, fileWidth, fileHeight);
    }

    private void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }


    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if(spell_name.getText().isEmpty()) {
            spell_name.setSuggestion("My Spell Name");
        }else
            spell_name.setSuggestion("");

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