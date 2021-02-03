package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.buttons.CraftingButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiSpellSlot;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellbook;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GuiSpellBook extends BaseBook {

    public int numLinks = 10;
    public SpellBook spellBook;
    public ArsNouveauAPI api;

    private int selected_cast_slot;
    public TextFieldWidget spell_name;
    public TextFieldWidget searchBar;
    public CompoundNBT spell_book_tag;
    public GuiSpellSlot selected_slot;
    public int max_spell_tier; // Used to load spells that are appropriate tier
    List<CraftingButton> craftingCells;
    public List<AbstractSpellPart>unlockedSpells;
    public List<AbstractSpellPart> effects;
    public List<Widget> effectButtons;
    public int page = 0;
    ChangePageButton nextButton;
    ChangePageButton previousButton;
    public GuiSpellBook(ArsNouveauAPI api, CompoundNBT tag, int tier, String unlockedSpells) {
        super();
        this.api = api;
        this.selected_cast_slot = 1;
        craftingCells = new ArrayList<>();
        this.max_spell_tier = tier;
        this.spell_book_tag = tag;
        this.unlockedSpells = SpellRecipeUtil.getSpellsFromString(unlockedSpells);
        this.effects = this.unlockedSpells.stream().filter(a -> a instanceof AbstractEffect).collect(Collectors.toList());
        effectButtons = new ArrayList<>();
    }

    @Override
    public void init() {
        super.init();
        int selected_slot_ind = SpellBook.getMode(spell_book_tag);
        if(selected_slot_ind == 0) selected_slot_ind = 1;

        //Crafting slots
        for (int i = 0; i < numLinks; i++) {
            String icon = null;
            String spell_id = "";
            int offset = i >= 5 ? 14 : 0;
            CraftingButton cell = new CraftingButton(this,bookLeft + 19 + 24 * i + offset, bookTop + FULL_HEIGHT - 47, i, this::onCraftingSlotClick);
            //GlyphButton glyphButton = new GlyphButton(this,bookLeft + 10 + 28 * i, bookTop + FULL_HEIGHT - 24, )
            addButton(cell);
            craftingCells.add(cell);
        }
        updateCraftingSlots(selected_slot_ind);

        addSpellParts(0);
        addButton(new GuiImageButton(bookRight - 71, bookBottom - 13, 0,0,50, 12, 50, 12, "textures/gui/create_icon.png", this::onCreateClick));
        addButton(new GuiImageButton(bookRight - 126, bookBottom - 13, 0,0,41, 12, 41, 12, "textures/gui/clear_icon.png", this::clear));

        spell_name = new NoShadowTextField(minecraft.fontRenderer, bookLeft + 32, bookTop + FULL_HEIGHT - 11,
                88, 12, null, new StringTextComponent("Spell Name"));
        spell_name.setEnableBackgroundDrawing(false);
        spell_name.setTextColor(12694931);

        searchBar = new NoShadowTextField(minecraft.fontRenderer, bookRight - 73, bookTop +2,
                54, 12, null, new StringTextComponent("Search"));
        searchBar.setEnableBackgroundDrawing(false);
        searchBar.setTextColor(12694931);


        int mode = SpellBook.getMode(spell_book_tag);
        mode = mode == 0 ? 1 : mode;
        spell_name.setText(SpellBook.getSpellName(spell_book_tag, mode));
        if(spell_name.getText().isEmpty())
            spell_name.setSuggestion("My Spell");

        if(searchBar.getText().isEmpty())
            searchBar.setSuggestion("Search");
        searchBar.setResponder(this::onSearchChanged);
//
        addButton(spell_name);
        addButton(searchBar);
        // Add spell slots
        for(int i = 1; i <= 10; i++){
            GuiSpellSlot slot = new GuiSpellSlot(this,bookLeft + 281, bookTop +1 + 15 * i, i);
            if(i == selected_slot_ind) {
                selected_slot = slot;
                selected_cast_slot = i;
                slot.isSelected = true;
            }
            addButton(slot);
        }

        addButton(new GuiImageButton(bookLeft - 15, bookTop + 22, 0, 0, 23, 20, 23,20, "textures/gui/worn_book_bookmark.png",this::onDocumentationClick));
        addButton(new GuiImageButton(bookLeft - 15, bookTop + 46, 0, 0, 23, 20, 23,20, "textures/gui/color_wheel_bookmark.png",this::onColorClick));
        this.nextButton = addButton(new ChangePageButton(bookRight -20, bookBottom -10, true, this::onPageIncrease, true));
        this.previousButton = addButton(new ChangePageButton(bookLeft - 5 , bookBottom -10, false, this::onPageDec, true));
        if(effects.size() < 36){
            nextButton.visible = false;
            nextButton.active = false;
        }else{
            nextButton.visible = true;
            nextButton.active = true;
        }
        previousButton.active = false;
        previousButton.visible = false;
    }

    public void onSearchChanged(String str){
        if(!str.isEmpty()){
            searchBar.setSuggestion("");
        }else
            searchBar.setSuggestion("Search");

        for(Widget w : buttons){
            if(w instanceof GlyphButton ){
                w.visible = api.getSpell_map().get(((GlyphButton) w).spell_id).getLocaleName().toLowerCase().contains(str.toLowerCase());
            }
        }
    }

    public void addSpellParts(int page){
        for(Widget w : effectButtons) {
            buttons.remove(w);
            children.remove(w);
        }
        effectButtons.clear();
        Collections.sort(unlockedSpells);

        List<AbstractSpellPart> displayedEffects = effects.subList(36 * page, Math.min(effects.size(), 36 * (page + 1)));
        //Adding spell parts
        int numCast = 0;
        int numEffect = 0;
        int numAugment = 0;
        for(AbstractSpellPart key  : unlockedSpells){
            AbstractSpellPart spell = this.api.getSpell_map().get(key.tag);
            GlyphButton cell = null;
            if(spell.getTier().ordinal() > max_spell_tier)
                continue; //Skip spells too high of a tier

            if(spell instanceof AbstractCastMethod) {
                int xOffset = 20 * (numCast % 6 );
                int yOffset = (numCast / 6) * 18 ;
                cell = new GlyphButton(this, bookLeft + 20 + xOffset, bookTop + 34 + yOffset, false, spell.getIcon(), spell.tag);
                numCast++;
            }else if(spell instanceof AbstractAugment){
                int xOffset = 20 * (numAugment % 6 );
                int yOffset = (numAugment / 6) * 18 ;
                cell = new GlyphButton(this, bookLeft + 20 + xOffset, bookTop + 88 +  yOffset, false, spell.getIcon(), spell.tag);
                numAugment++;
            }else{
                continue;
            }
            addButton(cell);
        }
        for(AbstractSpellPart s : displayedEffects){
            AbstractEffect spell = (AbstractEffect)s;
            if(!Config.isSpellEnabled(s.tag) || spell.getTier().ordinal() > max_spell_tier)
                continue;
            GlyphButton cell;
            int xOffset = 20 * (numEffect % 6 );
            int yOffset = (numEffect / 6) * 18 ;
            cell = new GlyphButton(this, bookLeft + 154 + xOffset, bookTop + 34 +  yOffset, false, spell.getIcon(), spell.tag);
            numEffect ++;
            effectButtons.add(addButton(cell));
        }
    }

    public void onPageIncrease(Button button){
        page++;
        if(effects.size() < 36 * (page + 1)){
            nextButton.visible = false;
            nextButton.active = false;
        }
        previousButton.active = true;
        previousButton.visible = true;
        addSpellParts(page);
    }

    public void onPageDec(Button button){
        page--;
        if(page == 0){
            previousButton.active = false;
            previousButton.visible = false;
        }

        if(effects.size() > 36 * (page + 1)){
            nextButton.visible = true;
            nextButton.active = true;
        }
        addSpellParts(page);
    }

    public void onDocumentationClick(Button button){
        PatchouliAPI.instance.openBookGUI(Registry.ITEM.getKey(ItemsRegistry.wornNotebook));
    }

    public void onColorClick(Button button){
        ParticleColor.IntWrapper color = SpellBook.getSpellColor(spell_book_tag, selected_cast_slot);
        Minecraft.getInstance().displayGuiScreen(new GuiColorScreen(color.r, color.g, color.b, selected_cast_slot));
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
        List<AbstractSpellPart> spell_recipe = this.spell_book_tag != null ? SpellBook.getRecipeFromTag(spell_book_tag, bookSlot).recipe : null;
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

    public void clear(Button button){
        for (int i = 0; i < craftingCells.size(); i++) {
            CraftingButton slot = craftingCells.get(i);
            slot.spellTag = "";
            slot.resourceIcon = "";
        }
    }

    public void onCreateClick(Button button){
        List<String> ids = new ArrayList<>();
        for(CraftingButton slot : craftingCells){
            ids.add(slot.spellTag);
        }
        Networking.INSTANCE.sendToServer(new PacketUpdateSpellbook(ids.toString(), this.selected_cast_slot, this.spell_name.getText()));
    }

    public static void open(ArsNouveauAPI api, CompoundNBT spell_book_tag, int tier, String unlockedSpells){
        Minecraft.getInstance().displayGuiScreen(new GuiSpellBook(api, spell_book_tag, tier, unlockedSpells));
    }

    public void drawBackgroundElements(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString(stack,"Form", 20, 24, -8355712);
        minecraft.fontRenderer.drawString(stack,"Effect", 154, 24, -8355712);
        minecraft.fontRenderer.drawString(stack,"Augment", 20, 78, -8355712);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/spell_name_paper.png"), 16, 179, 0, 0, 109, 15,109,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/search_paper.png"), 203, 0, 0, 0, 72, 15,72,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/clear_paper.png"), 161, 179, 0, 0, 47, 15,47,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_paper.png"), 216, 179, 0, 0, 56, 15,56,15, stack);
        minecraft.fontRenderer.drawString(stack,"Create", 233, 183, -8355712);
        minecraft.fontRenderer.drawString(stack,"Clear", 177, 183, -8355712);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);
        spell_name.setSuggestion(spell_name.getText().isEmpty() ? "My Spell Name" : "");
    }

}