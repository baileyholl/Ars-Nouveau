package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.buttons.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateCaster;
import com.hollingsworth.arsnouveau.common.spell.validation.CombinedSpellValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.GlyphMaxTierValidator;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.PatchouliAPI;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GuiSpellBook extends BaseBook {

    public int numLinks = 10;
    public SpellBook spellBook;
    public ArsNouveauAPI api;

    private int selectedSpellSlot;
    public EditBox spell_name;
    public NoShadowTextField searchBar;
    public GuiSpellSlot selected_slot;
    List<CraftingButton> craftingCells;
    public List<AbstractSpellPart> unlockedSpells = new ArrayList<>();
    public List<AbstractSpellPart> displayedGlyphs;

    public List<GlyphButton> glyphButtons = new ArrayList<>();
    public int page = 0;
    PageButton nextButton;
    PageButton previousButton;
    ISpellValidator spellValidator;
    public String previousString = "";
    public ItemStack bookStack;

    int formTextRow = 0;
    int augmentTextRow = 0;
    int effectTextRow = 0;

    public GuiSpellBook(ItemStack bookStack, int tier, List<AbstractSpellPart> unlockedSpells) {
        super();
        this.bookStack = bookStack;
        this.api = ArsNouveauAPI.getInstance();
        this.selectedSpellSlot = 0;
        craftingCells = new ArrayList<>();
        this.unlockedSpells = unlockedSpells;
        this.displayedGlyphs = new ArrayList<>(this.unlockedSpells);
        this.validationErrors = new LinkedList<>();
        this.spellValidator = new CombinedSpellValidator(
                api.getSpellCraftingSpellValidator(),
                new GlyphMaxTierValidator(tier)
        );
    }

    @Override
    public void init() {
        super.init();
        ISpellCaster caster = CasterUtil.getCaster(bookStack);
        int selectedSlot = caster.getCurrentSlot();
        //Crafting slots
        for (int i = 0; i < numLinks; i++) {
            int offset = i >= 5 ? 14 : 0;
            CraftingButton cell = new CraftingButton(this, bookLeft + 19 + 24 * i + offset, bookTop + FULL_HEIGHT - 47, i, this::onCraftingSlotClick);
            addRenderableWidget(cell);
            craftingCells.add(cell);
        }
        updateCraftingSlots(selectedSlot);

        layoutAllGlyphs(0);
        addRenderableWidget(new CreateSpellButton(this, bookRight - 71, bookBottom - 13, this::onCreateClick));
        addRenderableWidget(new GuiImageButton(bookRight - 126, bookBottom - 13, 0, 0, 41, 12, 41, 12, "textures/gui/clear_icon.png", this::clear));

        spell_name = new NoShadowTextField(minecraft.font, bookLeft + 32, bookTop + FULL_HEIGHT - 11,
                88, 12, null, Component.translatable("ars_nouveau.spell_book_gui.spell_name"));
        spell_name.setBordered(false);
        spell_name.setTextColor(12694931);

        searchBar = new NoShadowTextField(minecraft.font, bookRight - 73, bookTop + 2,
                54, 12, null, Component.translatable("ars_nouveau.spell_book_gui.search"));
        searchBar.setBordered(false);
        searchBar.setTextColor(12694931);
        searchBar.onClear = (val) -> {
            this.onSearchChanged("");
            return null;
        };

        spell_name.setValue(caster.getSpellName(caster.getCurrentSlot()));
        if (spell_name.getValue().isEmpty())
            spell_name.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.spell_name").getString());

        if (searchBar.getValue().isEmpty())
            searchBar.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
        searchBar.setResponder(this::onSearchChanged);
        addRenderableWidget(spell_name);
        addRenderableWidget(searchBar);
        // Add spell slots
        for (int i = 0; i < 10; i++) {
            GuiSpellSlot slot = new GuiSpellSlot(this, bookLeft + 281, bookTop + 1 + 15 * (i + 1), i);
            if (i == selectedSlot) {
                selected_slot = slot;
                selectedSpellSlot = i;
                slot.isSelected = true;
            }
            addRenderableWidget(slot);
        }

        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 22, 0, 0, 23, 20, 23, 20, "textures/gui/worn_book_bookmark.png", this::onDocumentationClick)
                .withTooltip(this, Component.translatable("ars_nouveau.gui.notebook")));
        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 46, 0, 0, 23, 20, 23, 20, "textures/gui/color_wheel_bookmark.png", this::onColorClick)
                .withTooltip(this, Component.translatable("ars_nouveau.gui.color")));
        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 70, 0, 0, 23, 20, 23, 20, "textures/gui/summon_circle_bookmark.png", this::onFamiliarClick)
                .withTooltip(this, Component.translatable("ars_nouveau.gui.familiar")));
        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 94, 0, 0, 23, 20, 23, 20, "textures/gui/sounds_tab.png", this::onSoundsClick)
                .withTooltip(this, Component.translatable("ars_nouveau.gui.sounds")));
        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 118, 0, 0, 23, 20, 23, 20, "textures/gui/settings_tab.png", (b) -> {
            Minecraft.getInstance().setScreen(new GuiSettingsScreen(this));
        }).withTooltip(this, Component.translatable("ars_nouveau.gui.settings")));

        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 142, 0, 0, 23, 20, 23, 20, "textures/gui/discord_tab.png", (b) -> {
            try {
                Util.getPlatform().openUri(new URI("https://discord.com/invite/y7TMXZu"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }).withTooltip(this, Component.translatable("ars_nouveau.gui.discord")));

        this.nextButton = addRenderableWidget(new PageButton(bookRight - 20, bookBottom - 10, true, this::onPageIncrease, true));
        this.previousButton = addRenderableWidget(new PageButton(bookLeft - 5, bookBottom - 10, false, this::onPageDec, true));

        updateNextPageButtons();
        previousButton.active = false;
        previousButton.visible = false;

        validate();
    }


    private void layoutAllGlyphs(int page) {
        clearButtons(glyphButtons);
        formTextRow = 0;
        augmentTextRow = 0;
        effectTextRow = 0;
        final int PER_ROW = 6;
        final int MAX_ROWS = 6;
        boolean nextPage = false;
        int xStart = nextPage ? bookLeft + 154 : bookLeft + 20;
        int adjustedRowsPlaced = 0;
        int yStart = bookTop + 20;
        boolean foundForms = false;
        boolean foundAugments = false;
        boolean foundEffects = false;
        List<AbstractSpellPart> sorted = new ArrayList<>();
        sorted.addAll(displayedGlyphs.stream().filter(s -> s instanceof AbstractCastMethod).toList());
        sorted.addAll(displayedGlyphs.stream().filter(s -> s instanceof AbstractAugment).toList());
        sorted.addAll(displayedGlyphs.stream().filter(s -> s instanceof AbstractEffect).toList());
        int perPage = 58;
        sorted.sort(COMPARE_TYPE_THEN_NAME);
        sorted = sorted.subList(perPage * page, Math.min(sorted.size(), perPage * (page + 1)));
        int adjustedXPlaced = 0;
        int totalRowsPlaced = 0;
        int row_offset = page == 0 ? 2 : 0;


        for (int i = 0; i < sorted.size(); i++) {
            AbstractSpellPart part = sorted.get(i);
            if (!foundForms && part instanceof AbstractCastMethod) {
                foundForms = true;
                adjustedRowsPlaced += 1;
                totalRowsPlaced += 1;
                formTextRow = page != 0 ? 0 : totalRowsPlaced;
                adjustedXPlaced = 0;
            }

            if (!foundAugments && part instanceof AbstractAugment) {
                foundAugments = true;
                adjustedRowsPlaced += row_offset;
                totalRowsPlaced += row_offset;
                augmentTextRow = page != 0 ? 0 : totalRowsPlaced - 1;
                adjustedXPlaced = 0;
            } else if (!foundEffects && part instanceof AbstractEffect) {
                foundEffects = true;
                adjustedRowsPlaced += row_offset;
                totalRowsPlaced += row_offset;
                effectTextRow = page != 0 ? 0 : totalRowsPlaced - 1;
                adjustedXPlaced = 0;
            } else {

                if (adjustedXPlaced >= PER_ROW) {
                    adjustedRowsPlaced++;
                    totalRowsPlaced++;
                    adjustedXPlaced = 0;
                }
            }
            if (adjustedRowsPlaced > MAX_ROWS) {
                if (nextPage) {
                    break;
                }
                nextPage = true;
                adjustedXPlaced = 0;
                adjustedRowsPlaced = 0;
            }
            int xOffset = 20 * ((adjustedXPlaced) % PER_ROW) + (nextPage ? 134 : 0);
            int yPlace = adjustedRowsPlaced * 18 + yStart;

            GlyphButton cell = new GlyphButton(this, xStart + xOffset, yPlace, false, part);
            addRenderableWidget(cell);
            glyphButtons.add(cell);
            adjustedXPlaced++;
        }
    }

    public void resetPageState() {
        updateNextPageButtons();
        this.page = 0;
        previousButton.active = false;
        previousButton.visible = false;
        layoutAllGlyphs(page);
        validate();
    }

    public void onSearchChanged(String str) {
        if (str.equals(previousString))
            return;
        previousString = str;

        if (!str.isEmpty()) {
            searchBar.setSuggestion("");
            displayedGlyphs = new ArrayList<>();

            for (AbstractSpellPart spellPart : unlockedSpells) {
                if (spellPart.getLocaleName().toLowerCase().contains(str.toLowerCase())) {
                    displayedGlyphs.add(spellPart);
                }
            }
            // Set visibility of Cast Methods and Augments
            for (Widget w : renderables) {
                if (w instanceof GlyphButton glyphButton) {
                    if (glyphButton.abstractSpellPart.getRegistryName() != null) {
                        AbstractSpellPart part = api.getSpellpartMap().get(glyphButton.abstractSpellPart.getRegistryName());
                        if (part != null) {
                            glyphButton.visible = part.getLocaleName().toLowerCase().contains(str.toLowerCase());
                        }
                    }
                }
            }
        } else {
            // Reset our book on clear
            searchBar.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
            displayedGlyphs = unlockedSpells;
            for (Widget w : renderables) {
                if (w instanceof GlyphButton) {
                    ((GlyphButton) w).visible = true;
                }
            }
        }
        resetPageState();
    }

    public void updateNextPageButtons() {
        if (displayedGlyphs.size() < 58) {
            nextButton.visible = false;
            nextButton.active = false;
        } else {
            nextButton.visible = true;
            nextButton.active = true;
        }
    }

    public void clearButtons(List<GlyphButton> glyphButtons) {
        for (GlyphButton b : glyphButtons) {
            renderables.remove(b);
            children().remove(b);
        }
        glyphButtons.clear();
    }

    public void onPageIncrease(Button button) {
        page++;
        if (displayedGlyphs.size() < 58 * (page + 1)) {
            nextButton.visible = false;
            nextButton.active = false;
        }
        previousButton.active = true;
        previousButton.visible = true;
        layoutAllGlyphs(page);
        validate();
    }

    public void onPageDec(Button button) {
        page--;
        if (page == 0) {
            previousButton.active = false;
            previousButton.visible = false;
        }

        if (displayedGlyphs.size() > 58 * (page + 1)) {
            nextButton.visible = true;
            nextButton.active = true;
        }
        layoutAllGlyphs(page);
        validate();
    }

    public void onDocumentationClick(Button button) {
        PatchouliAPI.get().openBookGUI(ForgeRegistries.ITEMS.getKey(ItemsRegistry.WORN_NOTEBOOK.asItem()));
    }

    public void onColorClick(Button button) {
        ParticleColor.IntWrapper color = CasterUtil.getCaster(bookStack).getColor(selectedSpellSlot).toWrapper();
        Minecraft.getInstance().setScreen(new GuiColorScreen(color.r, color.g, color.b, selectedSpellSlot));
    }

    public void onSoundsClick(Button button) {
        ConfiguredSpellSound spellSound = CasterUtil.getCaster(bookStack).getSound(selectedSpellSlot);
        Minecraft.getInstance().setScreen(new SoundScreen(spellSound, selectedSpellSlot));
    }

    public void onFamiliarClick(Button button) {
        Collection<ResourceLocation> familiarHolders = new ArrayList<>();
        IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(ArsNouveau.proxy.getPlayer()).orElse(null);
        if (cap != null) {
            familiarHolders = cap.getUnlockedFamiliars().stream().map(s -> s.familiarHolder.getRegistryName()).collect(Collectors.toList());
        }
        Collection<ResourceLocation> finalFamiliarHolders = familiarHolders;
        Minecraft.getInstance().setScreen(new GuiFamiliarScreen(api, ArsNouveauAPI.getInstance().getFamiliarHolderMap().values().stream().filter(f -> finalFamiliarHolders.contains(f.getRegistryName())).collect(Collectors.toList()), this));
    }

    public void onCraftingSlotClick(Button button) {
        ((CraftingButton) button).clear();
        validate();
    }

    public void onGlyphClick(Button button) {
        GlyphButton button1 = (GlyphButton) button;

        if (button1.validationErrors.isEmpty()) {
            for (CraftingButton b : craftingCells) {
                if (b.abstractSpellPart == null) {
                    b.abstractSpellPart = button1.abstractSpellPart;
                    b.spellTag = button1.abstractSpellPart.getRegistryName();
                    validate();
                    return;
                }
            }
        }
    }

    public void onSlotChange(Button button) {
        this.selected_slot.isSelected = false;
        this.selected_slot = (GuiSpellSlot) button;
        this.selected_slot.isSelected = true;
        this.selectedSpellSlot = this.selected_slot.slotNum;
        updateCraftingSlots(this.selectedSpellSlot);
        spell_name.setValue(CasterUtil.getCaster(bookStack).getSpellName(selectedSpellSlot));
        validate();
    }

    public void updateCraftingSlots(int bookSlot) {
        //Crafting slots
        List<AbstractSpellPart> recipe = CasterUtil.getCaster(bookStack).getSpell(bookSlot).recipe;
        for (int i = 0; i < craftingCells.size(); i++) {
            CraftingButton slot = craftingCells.get(i);
            slot.spellTag = ArsNouveauAPI.EMPTY_KEY;
            slot.abstractSpellPart = null;
            if (recipe != null && i < recipe.size()) {
                slot.spellTag = recipe.get(i).getRegistryName();
                slot.abstractSpellPart = recipe.get(i);
            }
        }
    }

    public void clear(Button button) {
        boolean allWereEmpty = true;

        for (CraftingButton slot : craftingCells) {
            if (!slot.spellTag.equals(ArsNouveauAPI.EMPTY_KEY))
                allWereEmpty = false;
            slot.clear();
        }

        if (allWereEmpty) spell_name.setValue("");

        validate();
    }

    public void onCreateClick(Button button) {
        validate();
        if (validationErrors.isEmpty()) {
            Spell spell = new Spell();
            for (CraftingButton slot : craftingCells) {
                AbstractSpellPart spellPart = ArsNouveauAPI.getInstance().getSpellpartMap().get(slot.spellTag);
                if (spellPart != null) {
                    spell.add(spellPart);
                }
            }
            Networking.INSTANCE.sendToServer(new PacketUpdateCaster(spell, this.selectedSpellSlot, this.spell_name.getValue()));
        }
    }

    public static void open(ItemStack stack, int tier) {
        IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(Minecraft.getInstance().player).orElse(null);
        List<AbstractSpellPart> parts = cap == null ? new ArrayList<>() : new ArrayList<>(cap.getKnownGlyphs());
        parts.addAll(ArsNouveauAPI.getInstance().getDefaultStartingSpells());
        if (stack.getItem() == ItemsRegistry.CREATIVE_SPELLBOOK.get())
            parts = new ArrayList<>(ArsNouveauAPI.getInstance().getSpellpartMap().values());
        Minecraft.getInstance().setScreen(new GuiSpellBook(stack, tier, parts));
    }

    public void drawBackgroundElements(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        if (formTextRow >= 1) {
            minecraft.font.draw(stack, Component.translatable("ars_nouveau.spell_book_gui.form").getString(), formTextRow > 6 ? 154 : 20, 5 + 18 * (formTextRow + (formTextRow == 1 ? 0 : 1)), -8355712);
        }
        if (effectTextRow >= 1) {

            minecraft.font.draw(stack, Component.translatable("ars_nouveau.spell_book_gui.effect").getString(), effectTextRow > 6 ? 154 : 20, 5 + 18 * (effectTextRow + 1), -8355712);
        }
        if (augmentTextRow >= 1) {
            minecraft.font.draw(stack, Component.translatable("ars_nouveau.spell_book_gui.augment").getString(), augmentTextRow > 6 ? 154 : 20, 5 + 18 * (augmentTextRow + 1), -8355712);
        }
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/spell_name_paper.png"), 16, 179, 0, 0, 109, 15, 109, 15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/search_paper.png"), 203, 0, 0, 0, 72, 15, 72, 15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/clear_paper.png"), 161, 179, 0, 0, 47, 15, 47, 15, stack);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_paper.png"), 216, 179, 0, 0, 56, 15, 56, 15, stack);
        if (validationErrors.isEmpty()) {
            minecraft.font.draw(stack, Component.translatable("ars_nouveau.spell_book_gui.create"), 233, 183, -8355712);
        } else {
            // Color code chosen to match GL11.glColor4f(1.0F, 0.7F, 0.7F, 1.0F);
            Component textComponent = Component.translatable("ars_nouveau.spell_book_gui.create")
                    .withStyle(s -> s.withStrikethrough(true).withColor(TextColor.parseColor("#FFB2B2")));
            // The final argument to draw desaturates the above color from the text component
            minecraft.font.draw(stack, textComponent, 233, 183, -8355712);
        }
        minecraft.font.draw(stack, Component.translatable("ars_nouveau.spell_book_gui.clear").getString(), 177, 183, -8355712);
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
            if (b.spellTag == ArsNouveauAPI.EMPTY_KEY) {
                // The validator can cope with null. Insert it to preserve glyph indices.
                recipe.add(null);
                // Also note where we found the first blank.  Used later for the glyph buttons.
                if (firstBlankSlot < 0) firstBlankSlot = i;
            } else {
                recipe.add(api.getSpellpartMap().get(b.spellTag));
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
        for (GlyphButton button : glyphButtons) {
            validateGlyphButton(recipe, button);
        }
    }

    private void validateGlyphButton(List<AbstractSpellPart> recipe, GlyphButton glyphButton) {
        // Start from a clean slate
        glyphButton.validationErrors.clear();

        // Simulate adding the glyph to the current spell
        recipe.add(api.getSpellpartMap().get(glyphButton.abstractSpellPart.getRegistryName()));

        // Filter the errors to ones referring to the simulated glyph
        glyphButton.validationErrors.addAll(
                spellValidator.validate(recipe).stream()
                        .filter(ve -> ve.getPosition() >= recipe.size() - 1).toList()
        );

        // Remove the simulated glyph to make room for the next one
        recipe.remove(recipe.size() - 1);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);
        spell_name.setSuggestion(spell_name.getValue().isEmpty() ? Component.translatable("ars_nouveau.spell_book_gui.spell_name").getString() : "");
    }

}
