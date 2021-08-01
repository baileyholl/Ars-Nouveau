package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.buttons.CraftingButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.CreateSpellButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiSpellSlot;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellbook;
import com.hollingsworth.arsnouveau.common.spell.validation.CombinedSpellValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.GlyphMaxTierValidator;
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
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;
import java.util.stream.Collectors;

public class GuiSpellBook extends BaseBook {

    public int numLinks = 10;
    public SpellBook spellBook;
    public ArsNouveauAPI api;

    private int selected_cast_slot;
    public TextFieldWidget spell_name;
    public NoShadowTextField searchBar;
    public CompoundNBT spell_book_tag;
    public GuiSpellSlot selected_slot;
    public int max_spell_tier; // Used to load spells that are appropriate tier
    List<CraftingButton> craftingCells;
    public List<AbstractSpellPart> unlockedSpells;
    public List<AbstractSpellPart> castMethods;
    public List<AbstractSpellPart> augments;
    public List<AbstractSpellPart> displayedEffects;
    public List<AbstractSpellPart> allEffects;
    public List<GlyphButton> castMethodButtons;
    public List<GlyphButton> augmentButtons;
    public List<GlyphButton> effectButtons;
    public int page = 0;
    public List<SpellValidationError> validationErrors;
    ChangePageButton nextButton;
    ChangePageButton previousButton;
    ISpellValidator spellValidator;
    public String previousString = "";
    public GuiSpellBook(ArsNouveauAPI api, CompoundNBT tag, int tier, String unlockedSpells) {
        super();
        this.api = api;
        this.selected_cast_slot = 1;
        craftingCells = new ArrayList<>();
        this.max_spell_tier = tier;
        this.spell_book_tag = tag;
        this.unlockedSpells = SpellRecipeUtil.getSpellsFromString(unlockedSpells);

        this.castMethods = new ArrayList<>();
        this.augments = new ArrayList<>();
        this.displayedEffects = new ArrayList<>();
        allEffects = new ArrayList<>();

        // Pre-partition the known spell glyphs
        for (AbstractSpellPart part : this.unlockedSpells) {
            if (part instanceof AbstractCastMethod) {
                this.castMethods.add(part);
            } else if (part instanceof AbstractAugment) {
                this.augments.add(part);
            } else if (part instanceof AbstractEffect) {
                this.displayedEffects.add(part);
                allEffects.add(part);
            }
        }

        this.castMethodButtons = new ArrayList<>();
        this.augmentButtons = new ArrayList<>();
        this.effectButtons = new ArrayList<>();
        this.validationErrors = new LinkedList<>();
        this.spellValidator = new CombinedSpellValidator(
                api.getSpellCraftingSpellValidator(),
                new GlyphMaxTierValidator(tier)
        );
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

        addCastMethodParts();
        addAugmentParts();
        addEffectParts(0);
        addButton(new CreateSpellButton(this, bookRight - 71, bookBottom - 13, this::onCreateClick));
        addButton(new GuiImageButton(bookRight - 126, bookBottom - 13, 0,0,41, 12, 41, 12, "textures/gui/clear_icon.png", this::clear));

        spell_name = new NoShadowTextField(minecraft.font, bookLeft + 32, bookTop + FULL_HEIGHT - 11,
                88, 12, null, new TranslationTextComponent("ars_nouveau.spell_book_gui.spell_name"));
        spell_name.setBordered(false);
        spell_name.setTextColor(12694931);

        searchBar = new NoShadowTextField(minecraft.font, bookRight - 73, bookTop +2,
                54, 12, null, new TranslationTextComponent("ars_nouveau.spell_book_gui.search"));
        searchBar.setBordered(false);
        searchBar.setTextColor(12694931);
        searchBar.onClear = (val) -> {
            this.onSearchChanged("");
            return null;
        };


        int mode = SpellBook.getMode(spell_book_tag);
        mode = mode == 0 ? 1 : mode;
        spell_name.setValue(SpellBook.getSpellName(spell_book_tag, mode));
        if(spell_name.getValue().isEmpty())
            spell_name.setSuggestion(new TranslationTextComponent("ars_nouveau.spell_book_gui.spell_name").getString());

        if(searchBar.getValue().isEmpty())
            searchBar.setSuggestion(new TranslationTextComponent("ars_nouveau.spell_book_gui.search").getString());
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

        updateNextPageButtons();
        previousButton.active = false;
        previousButton.visible = false;

        validate();
    }

    public void resetPageState(){
        updateNextPageButtons();
        this.page = 0;
        previousButton.active = false;
        previousButton.visible = false;
        addEffectParts(0);
        validate();
    }

    public void onSearchChanged(String str){
        if(str.equals(previousString))
            return;
        previousString = str;

        if (!str.isEmpty()) {
            searchBar.setSuggestion("");
            displayedEffects = new ArrayList<>();
            // Filter Effects
            for (AbstractSpellPart spellPart : unlockedSpells) {
                if (spellPart instanceof AbstractEffect && spellPart.getLocaleName().toLowerCase().contains(str.toLowerCase())) {
                    displayedEffects.add(spellPart);
                }
            }
            // Set visibility of Cast Methods and Augments
            for(Widget w : buttons) {
                if(w instanceof GlyphButton) {
                    if (((GlyphButton) w).spell_id != null) {
                        AbstractSpellPart part = api.getSpell_map().get(((GlyphButton) w).spell_id);
                        if (part != null) {
                            w.visible = part.getLocaleName().toLowerCase().contains(str.toLowerCase());
                        }
                    }
                }
            }
        } else {
            // Reset our book on clear
            searchBar.setSuggestion(new TranslationTextComponent("ars_nouveau.spell_book_gui.search").getString());
            displayedEffects = allEffects;
            for(Widget w : buttons){
                if(w instanceof GlyphButton ) {
                    w.visible = true;
                }
            }
        }
        resetPageState();
    }

    public void updateNextPageButtons(){
        if(displayedEffects.size() < 36){
            nextButton.visible = false;
            nextButton.active = false;
        }else{
            nextButton.visible = true;
            nextButton.active = true;
        }
    }

    private void addCastMethodParts() {
        layoutParts(castMethods, castMethodButtons, bookLeft + 20, bookTop + 34, 2);
    }

    private void addAugmentParts() {
        layoutParts(augments, augmentButtons, bookLeft + 20, bookTop + 88, 3);
    }

    private void addEffectParts(int page) {
        List<AbstractSpellPart> displayedEffects = this.displayedEffects.subList(36 * page, Math.min(this.displayedEffects.size(), 36 * (page + 1)));
        layoutParts(displayedEffects, effectButtons, bookLeft + 154, bookTop + 34, 6);
    }

    public void clearButtons( List<GlyphButton> glyphButtons){
        for (GlyphButton b : glyphButtons) {
            buttons.remove(b);
            children.remove(b);
        }
        glyphButtons.clear();
    }

    private void layoutParts(List<AbstractSpellPart> parts, List<GlyphButton> glyphButtons, int xStart, int yStart, int maxRows) {
        // Clear out the old buttons
        clearButtons(glyphButtons);
        final int PER_ROW = 6;
        int toLayout = Math.min(parts.size(), PER_ROW * maxRows);
        for (int i = 0; i < toLayout; i++) {
            AbstractSpellPart part = parts.get(i);
            int xOffset = 20 * (i % PER_ROW);
            int yOffset = (i / PER_ROW) * 18;
            GlyphButton cell = new GlyphButton(this, xStart + xOffset, yStart + yOffset, false, part.getIcon(), part.tag);
            glyphButtons.add(cell);
            addButton(cell);
        }
    }

    public void onPageIncrease(Button button){
        page++;
        if(displayedEffects.size() < 36 * (page + 1)){
            nextButton.visible = false;
            nextButton.active = false;
        }
        previousButton.active = true;
        previousButton.visible = true;
        addEffectParts(page);
        validate();
    }

    public void onPageDec(Button button){
        page--;
        if(page == 0){
            previousButton.active = false;
            previousButton.visible = false;
        }

        if(displayedEffects.size() > 36 * (page + 1)){
            nextButton.visible = true;
            nextButton.active = true;
        }
        addEffectParts(page);
        validate();
    }

    public void onDocumentationClick(Button button){
        PatchouliAPI.get().openBookGUI(Registry.ITEM.getKey(ItemsRegistry.wornNotebook));
    }

    public void onColorClick(Button button){
        ParticleColor.IntWrapper color = SpellBook.getSpellColor(spell_book_tag, selected_cast_slot);
        Minecraft.getInstance().setScreen(new GuiColorScreen(color.r, color.g, color.b, selected_cast_slot));
    }

    public void onCraftingSlotClick(Button button){
        ((CraftingButton) button).clear();
        validate();
    }

    public void onGlyphClick(Button button){
        GlyphButton button1 = (GlyphButton) button;

        if (button1.validationErrors.isEmpty()) {
            for (CraftingButton b : craftingCells) {
                if (b.resourceIcon.equals("")) {
                    b.resourceIcon = button1.resourceIcon;
                    b.spellTag = button1.spell_id;
                    validate();
                    return;
                }
            }
        }
    }

    public void onSlotChange(Button button){
        this.selected_slot.isSelected = false;
        this.selected_slot = (GuiSpellSlot) button;
        this.selected_slot.isSelected = true;
        this.selected_cast_slot = this.selected_slot.slotNum;
        updateCraftingSlots(this.selected_cast_slot);
        spell_name.setValue(SpellBook.getSpellName(spell_book_tag, this.selected_cast_slot));
        validate();
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
        for (CraftingButton slot : craftingCells) {
            slot.clear();
        }
        validate();
    }

    public void onCreateClick(Button button) {
        validate();
        if (validationErrors.isEmpty()) {
            List<String> ids = new ArrayList<>();
            for (CraftingButton slot : craftingCells) {
                ids.add(slot.spellTag);
            }
            Networking.INSTANCE.sendToServer(new PacketUpdateSpellbook(ids.toString(), this.selected_cast_slot, this.spell_name.getValue()));
        }
    }

    public static void open(ArsNouveauAPI api, CompoundNBT spell_book_tag, int tier, String unlockedSpells){
        Minecraft.getInstance().setScreen(new GuiSpellBook(api, spell_book_tag, tier, unlockedSpells));
    }

    public void drawBackgroundElements(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        minecraft.font.draw(stack,new TranslationTextComponent("ars_nouveau.spell_book_gui.form").getString(), 20, 24, -8355712);
        minecraft.font.draw(stack,new TranslationTextComponent("ars_nouveau.spell_book_gui.effect").getString(), 154, 24, -8355712);
        minecraft.font.draw(stack,new TranslationTextComponent("ars_nouveau.spell_book_gui.augment").getString(), 20, 78, -8355712);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/spell_name_paper.png"), 16, 179, 0, 0, 109, 15,109,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/search_paper.png"), 203, 0, 0, 0, 72, 15,72,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/clear_paper.png"), 161, 179, 0, 0, 47, 15,47,15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_paper.png"), 216, 179, 0, 0, 56, 15,56,15, stack);
        if (validationErrors.isEmpty()) {
            minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.spell_book_gui.create"), 233, 183, -8355712);
        } else {
            // Color code chosen to match GL11.glColor4f(1.0F, 0.7F, 0.7F, 1.0F);
            ITextComponent textComponent = new TranslationTextComponent("ars_nouveau.spell_book_gui.create")
                    .withStyle(s -> s.setStrikethrough(true).withColor(Color.parseColor("#FFB2B2")));
            // The final argument to draw desaturates the above color from the text component
            minecraft.font.draw(stack, textComponent, 233, 183, -8355712);
        }
        minecraft.font.draw(stack,new TranslationTextComponent("ars_nouveau.spell_book_gui.clear").getString(), 177, 183, -8355712);
    }

    /**
     * Validates the current spell as well as the potential for adding each glyph.
     */
    private void validate() {
        List<AbstractSpellPart> recipe = new LinkedList<>();
        int firstBlankSlot = -1;

        // Reset the crafting slots and build the recipe to validate
        for (int i = 0; i < craftingCells.size(); i++) {
            CraftingButton b = craftingCells.get(i);
            b.validationErrors.clear();
            if (b.spellTag.isEmpty()) {
                // The validator can cope with null. Insert it to preserve glyph indices.
                recipe.add(null);
                // Also note where we found the first blank.  Used later for the glyph buttons.
                if (firstBlankSlot < 0) firstBlankSlot = i;
            } else {
                recipe.add(api.getSpell_map().get(b.spellTag));
            }
        }

        // Validate the crafting slots
        List<SpellValidationError> errors = spellValidator.validate(recipe);
        for (SpellValidationError ve : errors) {
            // Attach errors to the corresponding crafting slot (when applicable)
            if (ve.getPosition() >= 0 && ve.getPosition() <= craftingCells.size()) {
                CraftingButton b = craftingCells.get(ve.getPosition());
                b.validationErrors.add(ve);
            }
        }
        this.validationErrors = errors;

        // Validate the glyph buttons
        // Trim the spell to the first gap, if there is a gap
        if (firstBlankSlot >= 0) {
            recipe = new ArrayList<>(recipe.subList(0, firstBlankSlot));
        }

        for (GlyphButton button : castMethodButtons) {
            validateGlyphButton(recipe, button);
        }
        for (GlyphButton button : augmentButtons) {
            validateGlyphButton(recipe, button);
        }
        for (GlyphButton button : effectButtons) {
            validateGlyphButton(recipe, button);
        }
    }

    private void validateGlyphButton(List<AbstractSpellPart> recipe, GlyphButton glyphButton) {
        // Start from a clean slate
        glyphButton.validationErrors.clear();

        // Simulate adding the glyph to the current spell
        recipe.add(api.getSpell_map().get(glyphButton.spell_id));

        // Filter the errors to ones referring to the simulated glyph
        glyphButton.validationErrors.addAll(
                spellValidator.validate(recipe).stream()
                        .filter(ve -> ve.getPosition() >= recipe.size() - 1).collect(Collectors.toList())
        );

        // Remove the simulated glyph to make room for the next one
        recipe.remove(recipe.size() - 1);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);
        spell_name.setSuggestion(spell_name.getValue().isEmpty() ? new TranslationTextComponent("ars_nouveau.spell_book_gui.spell_name").getString() : "");
    }

}
