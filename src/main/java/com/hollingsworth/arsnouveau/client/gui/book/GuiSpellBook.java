package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.hollingsworth.arsnouveau.client.gui.GuiUtils;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.SchoolTooltip;
import com.hollingsworth.arsnouveau.client.gui.buttons.*;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateCaster;
import com.hollingsworth.arsnouveau.common.spell.validation.CombinedSpellValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.GlyphMaxTierValidator;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.hollingsworth.arsnouveau.api.util.ManaUtil.getPlayerDiscounts;

public class GuiSpellBook extends BaseBook {

    public int numLinks = 10;

    public int selectedSpellSlot = 0;
    public EditBox spell_name;
    public NoShadowTextField searchBar;
    public GuiSpellSlot selected_slot;
    public List<CraftingButton> craftingCells = new ArrayList<>();
    public List<AbstractSpellPart> unlockedSpells;
    public List<AbstractSpellPart> displayedGlyphs;

    public List<GlyphButton> glyphButtons = new ArrayList<>();
    public int page = 0;
    public PageButton nextButton;
    public PageButton previousButton;
    public ISpellValidator spellValidator;
    public String previousString = "";
    public ItemStack bookStack;

    public int formTextRow = 0;
    public int augmentTextRow = 0;
    public int effectTextRow = 0;
    public int glyphsPerPage = 58;
    public InteractionHand hand;

    public int maxManaCache = 0;
    int currentCostCache = 0;

    public CreateSpellButton createSpellButton;

    public Renderable hoveredWidget = null;

    public List<AbstractSpellPart> spell = new ArrayList<>();
    public PageButton nextGlyphButton;
    public PageButton prevGlyphButton;
    public int spellWindowOffset = 0;
    public int bonusSlots = 0;
    public String spellname = "";
    public AbstractCaster<?> caster;

    public long timeOpened;

    public GuiSpellBook(InteractionHand hand) {
        super();
        this.hand = hand;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(player);
        ItemStack heldStack = player.getItemInHand(hand);
        List<AbstractSpellPart> parts = cap == null ? new ArrayList<>() : new ArrayList<>(cap.getKnownGlyphs().stream().filter(AbstractSpellPart::shouldShowInSpellBook).toList());
        maxManaCache = ManaUtil.getMaxMana(player);
        parts.addAll(GlyphRegistry.getDefaultStartingSpells());
        int tier = 1;
        if (heldStack.getItem() instanceof SpellBook book) {
            tier = book.getTier().value;
            if (book.getTier() == SpellTier.CREATIVE) {
                parts = new ArrayList<>(GlyphRegistry.getSpellpartMap().values().stream().filter(AbstractSpellPart::shouldShowInSpellBook).toList());
            }
        }
        if (SpellCasterRegistry.hasCaster(heldStack)) {
            AbstractCaster<?> caster = SpellCasterRegistry.from(heldStack);
            if (caster != null) {
                bonusSlots = caster.getBonusGlyphSlots();
            }
        }
        this.bookStack = heldStack;
        this.unlockedSpells = parts;
        this.displayedGlyphs = new ArrayList<>(this.unlockedSpells);
        this.validationErrors = new LinkedList<>();
        this.spellValidator = new CombinedSpellValidator(
                ArsNouveauAPI.getInstance().getSpellCraftingSpellValidator(),
                new GlyphMaxTierValidator(tier)
        );
        caster = SpellCasterRegistry.from(bookStack);
        this.selectedSpellSlot = caster.getCurrentSlot();
        this.spellname = caster.getSpellName(caster.getCurrentSlot());
        List<AbstractSpellPart> recipe = SpellCasterRegistry.from(bookStack).getSpell(selectedSpellSlot).mutable().recipe;
        spell = new ArrayList<>(recipe);
    }

    public void onBookstackUpdated(ItemStack stack) {
        this.bookStack = stack;
        this.caster = SpellCasterRegistry.from(stack);
        if (caster == null) {
            Minecraft.getInstance().setScreen(null);
        }
    }

    @Override
    public void init() {
        super.init();
        timeOpened = System.currentTimeMillis();
        craftingCells = new ArrayList<>();
        resetCraftingCells();

        layoutAllGlyphs(page);
        createSpellButton = addRenderableWidget(new CreateSpellButton(bookRight - 71, bookBottom - 11, this::onCreateClick, () -> !this.validationErrors.isEmpty()));
        addRenderableWidget(new GuiImageButton(bookRight - 126, bookBottom - 11, 0, 0, 41, 12, 41, 12, "textures/gui/clear_icon.png", this::clear));

        spell_name = new NoShadowTextField(minecraft.font, bookLeft + 32, bookBottom - 9,
                88, 12, null, Component.translatable("ars_nouveau.spell_book_gui.spell_name"));
        spell_name.setBordered(false);
        spell_name.setTextColor(12694931);

        searchBar = new NoShadowTextField(minecraft.font, bookRight - 73, bookTop,
                54, 12, null, Component.translatable("ars_nouveau.spell_book_gui.search"));
        searchBar.setBordered(false);
        searchBar.setTextColor(12694931);
        searchBar.onClear = (val) -> {
            this.onSearchChanged("");
            return null;
        };

        spell_name.setValue(spellname);
        if (spell_name.getValue().isEmpty())
            spell_name.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.spell_name").getString());

        if (searchBar.getValue().isEmpty())
            searchBar.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
        searchBar.setResponder(this::onSearchChanged);
        addRenderableWidget(spell_name);
        addRenderableWidget(searchBar);
        // Add spell slots
        for (int i = 0; i < 10; i++) {
            String name = caster.getSpellName(i);
            GuiSpellSlot slot = new GuiSpellSlot(bookLeft + 281, bookTop - 1 + 15 * (i + 1), i, name, this::onSlotChange);
            if (i == selectedSpellSlot) {
                selected_slot = slot;
                slot.isSelected = true;
            }
            addRenderableWidget(slot);
        }

        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 46, 0, 0, 23, 20, 23, 20, "textures/gui/color_wheel_bookmark.png", this::onColorClick)
                .withTooltip(Component.translatable("ars_nouveau.gui.color")));
        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 94, 0, 0, 23, 20, 23, 20, "textures/gui/sounds_tab.png", this::onSoundsClick)
                .withTooltip(Component.translatable("ars_nouveau.gui.sounds")));


        this.nextButton = addRenderableWidget(new PageButton(bookRight - 20, bookBottom - 6, true, this::onPageIncrease, true));
        this.previousButton = addRenderableWidget(new PageButton(bookLeft - 5, bookBottom - 6, false, this::onPageDec, true));

        updateNextPageButtons();
        previousButton.active = false;
        previousButton.visible = false;

        //infinite spells
        if (getExtraGlyphSlots() > 0) {
            this.nextGlyphButton = addRenderableWidget(new PageButton(bookRight - 25, bookBottom - 26, true, i -> updateWindowOffset(spellWindowOffset + 1), true));
            this.prevGlyphButton = addRenderableWidget(new PageButton(bookLeft, bookBottom - 26, false, i -> updateWindowOffset(spellWindowOffset - 1), true));
            updateWindowOffset(0);
        }
        validate();
    }

    public int getNumPages() {
        return (int) Math.ceil((double) displayedGlyphs.size() / 58);
    }

    private void layoutAllGlyphs(int page) {
        clearButtons(glyphButtons);
        formTextRow = 0;
        augmentTextRow = 0;
        effectTextRow = 0;

        if (displayedGlyphs.isEmpty()) {
            return;
        }

        final int PER_ROW = 6;
        final int MAX_ROWS = 6;
        boolean nextPage = false;
        int xStart = nextPage ? bookLeft + 154 : bookLeft + 20;
        int adjustedRowsPlaced = 0;
        boolean foundForms = false;
        boolean foundAugments = false;
        boolean foundEffects = false;

        List<AbstractSpellPart> sorted = new ArrayList<>(displayedGlyphs);
        sorted.sort(Comparator.comparingInt((AbstractSpellPart p) -> switch (p) {
            case AbstractAugment ignored -> 5;
            default -> p.getTypeIndex();
        }).thenComparing(AbstractSpellPart::getLocaleName));

        sorted = sorted.subList(glyphsPerPage * page, Math.min(sorted.size(), glyphsPerPage * (page + 1)));
        int adjustedXPlaced = 0;
        int totalRowsPlaced = 0;
        int rowOffset = page == 0 ? 2 : 0;

        int yStart = bookTop + 2 + (page != 0 || sorted.getFirst() instanceof AbstractCastMethod ? 18 : 0);

        for (AbstractSpellPart part : sorted) {
            if (!foundForms && part instanceof AbstractCastMethod) {
                foundForms = true;
                adjustedRowsPlaced += 1;
                totalRowsPlaced += 1;
                formTextRow = page != 0 ? 0 : totalRowsPlaced;
                adjustedXPlaced = 0;
            } else if (!foundAugments && part instanceof AbstractAugment) {
                foundAugments = true;
                adjustedRowsPlaced += rowOffset;
                totalRowsPlaced += rowOffset;
                augmentTextRow = page != 0 ? 0 : totalRowsPlaced - 1;
                adjustedXPlaced = 0;
            } else if (!foundEffects && part instanceof AbstractEffect) {
                foundEffects = true;
                adjustedRowsPlaced += rowOffset;
                totalRowsPlaced += rowOffset;
                effectTextRow = page != 0 ? 0 : totalRowsPlaced - 1;
                adjustedXPlaced = 0;
            } else if (adjustedXPlaced >= PER_ROW) {
                adjustedRowsPlaced++;
                totalRowsPlaced++;
                adjustedXPlaced = 0;
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

            GlyphButton cell = new GlyphButton(xStart + xOffset, yPlace, part, this::onGlyphClick);
            addRenderableWidget(cell);
            glyphButtons.add(cell);
            adjustedXPlaced++;
        }
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
            for (Renderable w : renderables) {
                if (w instanceof GlyphButton glyphButton) {
                    if (glyphButton.abstractSpellPart.getRegistryName() != null) {
                        AbstractSpellPart part = GlyphRegistry.getSpellpartMap().get(glyphButton.abstractSpellPart.getRegistryName());
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
            for (Renderable w : renderables) {
                if (w instanceof GlyphButton) {
                    ((GlyphButton) w).visible = true;
                }
            }
        }
        updateNextPageButtons();
        this.page = 0;
        previousButton.active = false;
        previousButton.visible = false;
        layoutAllGlyphs(page);
        validate();
    }

    public void updateNextPageButtons() {
        if (displayedGlyphs.size() < glyphsPerPage) {
            nextButton.visible = false;
            nextButton.active = false;
        } else {
            nextButton.visible = true;
            nextButton.active = true;
        }
    }

    public void onPageIncrease(Button button) {
        if (page + 1 >= getNumPages())
            return;
        page++;
        if (displayedGlyphs.size() < glyphsPerPage * (page + 1)) {
            nextButton.visible = false;
            nextButton.active = false;
        }
        previousButton.active = true;
        previousButton.visible = true;
        layoutAllGlyphs(page);
        validate();
    }

    public void onPageDec(Button button) {
        if (page <= 0) {
            page = 0;
            return;
        }
        page--;
        if (page == 0) {
            previousButton.active = false;
            previousButton.visible = false;
        }

        if (displayedGlyphs.size() > glyphsPerPage * (page + 1)) {
            nextButton.visible = true;
            nextButton.active = true;
        }
        layoutAllGlyphs(page);
        validate();
    }

    public int getExtraGlyphSlots() {
        return (ServerConfig.INFINITE_SPELLS.get() ? ServerConfig.NOT_SO_INFINITE_SPELLS.get() : 0) + bonusSlots;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        boolean isShiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), Minecraft.getInstance().options.keyShift.getKey().getValue());
        if (getExtraGlyphSlots() > 0 && isShiftDown) {
            if (pScrollY < 0 && nextGlyphButton.active) {
                updateWindowOffset(spellWindowOffset + 1);
            } else if (pScrollY > 0 && prevGlyphButton.active) {
                updateWindowOffset(spellWindowOffset - 1);
            }
            return true;
        }
        SoundManager manager = Minecraft.getInstance().getSoundManager();
        if (pScrollY < 0 && nextButton.active) {
            onPageIncrease(nextButton);
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        } else if (pScrollY > 0 && previousButton.active) {
            onPageDec(previousButton);
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }

        return true;
    }

    public void onColorClick(Button button) {
        ParticleColor.IntWrapper color = SpellCasterRegistry.from(bookStack).getColor(selectedSpellSlot).toWrapper();
        Minecraft.getInstance().setScreen(new GuiColorScreen(color.r, color.g, color.b, selectedSpellSlot, this.hand, this));
    }

    public void onSoundsClick(Button button) {
        ConfiguredSpellSound spellSound = SpellCasterRegistry.from(bookStack).getSound(selectedSpellSlot);
        Minecraft.getInstance().setScreen(new SoundScreen(spellSound, selectedSpellSlot, this.hand, this));
    }

    public void onCraftingSlotClick(Button button) {
        if (button instanceof CraftingButton craftingButton) {
            craftingButton.clear();
            if (craftingButton.slotNum < spell.size()) {
                spell.set(((CraftingButton) button).slotNum, null);
            }
        }
        //sanitize the spell if manually cleared
        if (spell.stream().allMatch(Objects::isNull)) {
            spell.clear();
        }
        if (nextGlyphButton != null) updateNextGlyphArrow();
        validate();
    }

    public void onGlyphClick(Button button) {
        GlyphButton button1 = (GlyphButton) button;
        if (!button1.validationErrors.isEmpty()) {
            return;
        }
        for (CraftingButton b : craftingCells.subList(spellWindowOffset, Math.min(spellWindowOffset + 10, craftingCells.size()))) {
            if (b.getAbstractSpellPart() != null) {
                continue;
            }

            b.setAbstractSpellPart(button1.abstractSpellPart);

            if (b.slotNum >= spell.size()) {
                spell.add(button1.abstractSpellPart);
            } else {
                spell.set(b.slotNum, button1.abstractSpellPart);
            }

            if (nextGlyphButton != null) updateNextGlyphArrow();
            validate();
            return;
        }
    }


    private void updateNextGlyphArrow() {
        if (spellWindowOffset >= getExtraGlyphSlots() || spellWindowOffset > spell.size() - 1) {
            nextGlyphButton.active = false;
            nextGlyphButton.visible = false;
        } else {
            nextGlyphButton.active = true;
            nextGlyphButton.visible = true;
        }
    }

    public void onSlotChange(Button button) {
        this.selected_slot.isSelected = false;
        this.selected_slot = (GuiSpellSlot) button;
        this.selected_slot.isSelected = true;
        this.selectedSpellSlot = this.selected_slot.slotNum;
        this.spellname = caster.getSpellName(selectedSpellSlot);
        spell_name.setValue(spellname);
        this.spell = new ArrayList<>(caster.getSpell(selectedSpellSlot).unsafeList());
        resetCraftingCells();
        updateWindowOffset(0); //includes validation
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (pCodePoint >= '0' && pCodePoint <= '9') {
            int idx = Integer.parseInt(String.valueOf(pCodePoint));
            if (idx == 0) {
                idx = 10;
            }
            idx = idx - 1 + spellWindowOffset;

            switch (hoveredWidget) {
                case GlyphButton button -> {
                    if (!button.validationErrors.isEmpty()) {
                        return true;
                    }

                    CraftingButton currentCell = craftingCells.get(idx);
                    currentCell.setAbstractSpellPart(button.abstractSpellPart);
                    for (int i = spell.size(); i <= idx; i++) {
                        spell.add(null);
                    }
                    spell.set(idx, button.abstractSpellPart);
                    validate();
                    this.setFocused(button);

                    return true;
                }
                case CraftingButton button -> {
                    if (idx < this.spell.size()) {
                        Collections.swap(spell, button.slotNum, idx);
                    } else {
                        spell.add(button.getAbstractSpellPart());
                    }

                    int left = -1;
                    int right = -1;

                    for (CraftingButton cell : craftingCells) {
                        if (cell.slotNum == button.slotNum) {
                            left = cell.slotNum;
                        }
                        if (cell.slotNum == idx) {
                            right = cell.slotNum;
                        }

                        if (left != -1 && right != -1) {
                            break;
                        }
                    }

                    if (left == -1 || right == -1) {
                        return true;
                    }

                    Collections.swap(craftingCells, left, right);
                    craftingCells.get(left).slotNum = right;
                    craftingCells.get(right).slotNum = left;
                    validate();
                    this.setFocused(button);

                    return true;
                }
                case null, default -> {
                }
            }
        }

        if (!super.charTyped(pCodePoint, pModifiers)) {
            if ((!searchBar.isFocused() || !searchBar.active) && System.currentTimeMillis() - timeOpened > 30) {
                this.clearFocus();
                this.setFocused(searchBar);
                searchBar.active = true;
                this.searchBar.setValue("");
                this.searchBar.onClear.apply("");
                return searchBar.charTyped(pCodePoint, pModifiers);
            }
            return false;
        }

        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_3 && hoveredWidget instanceof CraftingButton craftingCell) {
            int idx = -1;
            for (int i = 0; i < craftingCells.size(); i++) {
                CraftingButton cell = craftingCells.get(i);
                if (cell.slotNum == craftingCell.slotNum) {
                    idx = i;
                    break;
                }
            }

            if (idx == -1 || craftingCells.getLast().getAbstractSpellPart() != null) {
                return true;
            }

            for (int i = craftingCells.size() - 1; i >= idx + 1; i--) {
                CraftingButton cell = craftingCells.get(i);
                CraftingButton prev = craftingCells.get(i - 1);

                cell.setAbstractSpellPart(prev.getAbstractSpellPart());
            }

            spell.add(idx, null);
            craftingCells.get(idx).setAbstractSpellPart(null);
            this.setFocused(craftingCell);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void resetCraftingCells() {
        for (CraftingButton button : craftingCells) {
            removeWidget(button);
        }
        craftingCells = new ArrayList<>();
        for (int i = 0; i < numLinks + getExtraGlyphSlots(); i++) {
            CraftingButton cell = new CraftingButton(0, 0, this::onCraftingSlotClick, i);
            addRenderableWidget(cell);
            craftingCells.add(cell);
            cell.visible = false;
            AbstractSpellPart spellPart = i < this.spell.size() ? this.spell.get(i) : null;
            cell.setAbstractSpellPart(spellPart);
        }

        for (int i = 0; i < 10; i++) {
            int placementOffset = i % 10;
            int offset = placementOffset >= 5 ? 14 : 0;

            if (i + spellWindowOffset >= craftingCells.size()) {
                break;
            }
            var cell = craftingCells.get(spellWindowOffset + i);
            cell.setX(bookLeft + 19 + 24 * placementOffset + offset);
            cell.setY(bookBottom - 43);
            cell.visible = true;
        }
    }

    public void updateWindowOffset(int offset) {
        //do nothing if the spell is empty and nextGlyphButton is clicked
        int extraSlots = getExtraGlyphSlots();
        if (extraSlots > 0) {
            if (spellWindowOffset != 0 || offset <= 0 || !spell.stream().allMatch(Objects::isNull)) {
                this.spellWindowOffset = Mth.clamp(offset, 0, extraSlots);
                if (spellWindowOffset <= 0) {
                    prevGlyphButton.active = false;
                    prevGlyphButton.visible = false;

                } else {
                    prevGlyphButton.active = true;
                    prevGlyphButton.visible = true;
                }
                updateNextGlyphArrow();
            }
        }
        validate();
    }

    public void clear(Button button) {
        boolean allWereEmpty = spell.isEmpty();
        spell.clear();

        if (allWereEmpty) spell_name.setValue("");

        validate();
    }

    public void onCreateClick(Button button) {
        validate();
        if (validationErrors.isEmpty()) {
            Spell.Mutable spell = new Spell().mutable();
            for (AbstractSpellPart spellPart : this.spell) {
                if (spellPart != null) {
                    spell.add(spellPart);
                }
            }
            Networking.sendToServer(new PacketUpdateCaster(spell.immutable(), this.selectedSpellSlot, this.spell_name.getValue(), hand == InteractionHand.MAIN_HAND));
        }
    }

    public static void open(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new GuiSpellBook(hand));
    }

    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        int formOffset = 0;
        if (formTextRow >= 1) {
            graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.form").getString(), formTextRow > 6 ? 154 : 20, 5 + 18 * (formTextRow + (formTextRow == 1 ? 0 : 1)), -8355712, false);
            formOffset = 1;
        }

        if (effectTextRow >= 1) {
            graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.effect").getString(), effectTextRow > 6 ? 154 : 20, 5 + 18 * (effectTextRow + formOffset), -8355712, false);
        }
        if (augmentTextRow >= 1) {
            graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.augment").getString(), augmentTextRow > 6 ? 154 : 20, 5 + 18 * (augmentTextRow + formOffset), -8355712, false);
        }
        graphics.blit(ArsNouveau.prefix("textures/gui/spell_name_paper.png"), 16, 175, 0, 0, 109, 15, 109, 15);
        graphics.blit(ArsNouveau.prefix("textures/gui/search_paper.png"), 203, -3, 0, 0, 72, 15, 72, 15);
        graphics.blit(ArsNouveau.prefix("textures/gui/clear_paper.png"), 161, 175, 0, 0, 47, 15, 47, 15);
        graphics.blit(ArsNouveau.prefix("textures/gui/create_paper.png"), 216, 175, 0, 0, 56, 15, 56, 15);
        if (validationErrors.isEmpty()) {
            graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.create"), 233, 179, -8355712, false);
        } else {
            // Color code chosen to match GL11.glColor4f(1.0F, 0.7F, 0.7F, 1.0F);
            Component textComponent = Component.translatable("ars_nouveau.spell_book_gui.create")
                    .withStyle(s -> s.withStrikethrough(true).withColor(TextColor.parseColor("#FFB2B2").getOrThrow()));
            // The final argument to draw desaturates the above color from the text component
            graphics.drawString(font, textComponent, 233, 183, -8355712, false);
        }
        graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.clear").getString(), 177, 179, -8355712, false);

        //manabar
        int manaLength = 96;
        if (maxManaCache > 0) {
            //keep the mana bar lenght between -1 and 96 to avoid over/underflow
            manaLength = (int) Mth.clamp(manaLength * ((float) (maxManaCache - currentCostCache) / maxManaCache), -1, 96);
        } else manaLength = 0;

        int offsetLeft = 89;
        int yOffset = 210;

        //scale the manabar to fit the gui
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale(1.2F, 1.2F, 1.2F);
        poseStack.translate(-25, -30, 0);
        graphics.blit(ArsNouveau.prefix("textures/gui/manabar_gui_border.png"), offsetLeft, yOffset - 18, 0, 0, 108, 18, 256, 256);
        int manaOffset = (int) (((ClientInfo.ticksInGame + partialTicks) / 3 % (33))) * 6;

        // default length is 96
        if (manaLength > 0) {
            graphics.blit(ArsNouveau.prefix("textures/gui/manabar_gui_mana.png"), offsetLeft + 9, yOffset - 9, 0, manaOffset, manaLength, 6, 256, 256);
        } else {
            //color rainbow if mana cost = max mana, red if mana cost > max mana
            RenderSystem.setShaderTexture(0, ArsNouveau.prefix("textures/gui/manabar_gui_grayscale.png"));
            RenderUtils.colorBlit(graphics.pose(), offsetLeft + 8, yOffset - 10, 0, manaOffset, 100, 8, 256, 256, manaLength < 0 ? Color.RED : Color.rainbowColor(ClientInfo.ticksInGame));
        }
        if (ArsNouveauAPI.ENABLE_DEBUG_NUMBERS && minecraft != null) {
            String text = currentCostCache + "  /  " + maxManaCache;
            int maxWidth = minecraft.font.width(maxManaCache + "  /  " + maxManaCache);
            int offset = offsetLeft - maxWidth / 2 + (maxWidth - minecraft.font.width(text));

            graphics.drawString(minecraft.font, text, offset + 55, yOffset - 10, 0xFFFFFF, false);
        }

        graphics.blit(ArsNouveau.prefix("textures/gui/manabar_gui_border.png"), offsetLeft, yOffset - 17, 0, 18, 108, 20, 256, 256);
        poseStack.popPose();
    }

    private int getCurrentManaCost() {
        Spell spell = new Spell();
        for (AbstractSpellPart part : this.spell) {
            if (part != null) {
                spell = spell.add(part);
            }
        }
        int cost = spell.getCost() - getPlayerDiscounts(Minecraft.getInstance().player, spell, bookStack);
        return Math.max(cost, 0);
    }

    /**
     * Validates the current spell as well as the potential for adding each glyph.
     */
    private void validate() {
        resetCraftingCells();
        //update mana cache
        currentCostCache = getCurrentManaCost();
        maxManaCache = ManaUtil.getMaxMana(Minecraft.getInstance().player);

        // Reset the crafting slots and build the recipe to validate
        for (CraftingButton b : craftingCells) {
            b.validationErrors.clear();
        }

        // Validate the crafting slots
        List<SpellValidationError> errors = spellValidator.validate(spell);
        for (SpellValidationError ve : errors) {
            CraftingButton b = craftingCells.get(ve.getPosition());
            b.validationErrors.add(ve);
        }
        this.validationErrors = errors;

        for (CraftingButton craftingButton : craftingCells) {
            craftingButton.setAugmenting(null);
        }
        AbstractSpellPart parent = null;
        for (int i = 0; i < Math.max(spell.size(), craftingCells.size()); i++) {
            AbstractSpellPart part = i < spell.size() ? spell.get(i) : null;
            if (!(part instanceof AbstractAugment)) {
                parent = part;
            }
            for (CraftingButton craftingButton : craftingCells) {
                if (craftingButton.slotNum == i) {
                    craftingButton.setAugmenting(parent);
                }
            }
        }
        // Find the last effect before an empty space
        AbstractSpellPart lastEffect = null;
        int lastGlyphNoGap = 0;
        for (int i = 0; i < spell.size(); i++) {
            AbstractSpellPart effect = spell.get(i);
            if (effect == null) {
                break;
            }
            if (!(effect instanceof AbstractAugment)) {
                lastEffect = effect;
            }
            lastGlyphNoGap = i;
        }


        List<AbstractSpellPart> slicedSpell = spell.subList(0, spell.isEmpty() ? 0 : (lastGlyphNoGap + 1));
        // Set validation errors on all of the glyph buttons
        for (GlyphButton glyphButton : glyphButtons) {
            glyphButton.validationErrors.clear();
            glyphButton.augmentingParent = lastEffect;
            // Simulate adding the glyph to the current spell
            slicedSpell.add(GlyphRegistry.getSpellpartMap().get(glyphButton.abstractSpellPart.getRegistryName()));

            // Filter the errors to ones referring to the simulated glyph
            glyphButton.validationErrors.addAll(
                    spellValidator.validate(slicedSpell).stream()
                            .filter(ve -> ve.getPosition() >= slicedSpell.size() - 1).toList()
            );

            // Remove the simulated glyph to make room for the next one
            slicedSpell.removeLast();
        }
    }

    @Override
    public void render(GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);
        hoveredWidget = null;
        for (Renderable widget : renderables) {
            if (widget instanceof AbstractWidget abstractWidget && GuiUtils.isMouseInRelativeRange(mouseX, mouseY, abstractWidget)) {
                hoveredWidget = widget;
                break;
            }
        }
        spell_name.setSuggestion(spell_name.getValue().isEmpty() ? Component.translatable("ars_nouveau.spell_book_gui.spell_name").getString() : "");
    }

    @Override
    public void collectTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip) {
        if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, createSpellButton)) {
            if (!validationErrors.isEmpty()) {
                boolean foundGlyphErrors = false;
                tooltip.add(Component.translatable("ars_nouveau.spell.validation.crafting.invalid").withStyle(ChatFormatting.RED));

                // Add any spell-wide errors
                for (SpellValidationError error : validationErrors) {
                    if (error.getPosition() < 0) {
                        tooltip.add(error.makeTextComponentExisting());
                    } else {
                        foundGlyphErrors = true;
                    }
                }
                // Show a single placeholder for all the per-glyph errors
                if (foundGlyphErrors) {
                    tooltip.add(Component.translatable("ars_nouveau.spell.validation.crafting.invalid_glyphs"));
                }
            }
        } else {
            super.collectTooltips(stack, mouseX, mouseY, tooltip);
        }
    }

    public void drawTooltip(GuiGraphics stack, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        collectTooltips(stack, mouseX, mouseY, tooltip);
        if (!tooltip.isEmpty()) {
            stack.renderTooltip(font, tooltip, Optional.ofNullable(collectComponent(mouseX, mouseY)), mouseX, mouseY);
        }
    }

    protected TooltipComponent collectComponent(int mouseX, int mouseY) {
        for (Renderable renderable : renderables) {
            if (renderable instanceof GlyphButton widget) {
                if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, widget)) {
                    return widget.abstractSpellPart.spellSchools.isEmpty() ? null : new SchoolTooltip(widget.abstractSpellPart);
                }
            }
        }
        return null;
    }
}
