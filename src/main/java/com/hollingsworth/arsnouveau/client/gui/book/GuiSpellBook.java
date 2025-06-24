package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.registry.FamiliarRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.*;
import com.hollingsworth.arsnouveau.client.gui.buttons.*;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.*;
import com.hollingsworth.arsnouveau.common.spell.validation.CombinedSpellValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.GlyphKnownValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.GlyphMaxTierValidator;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static com.hollingsworth.arsnouveau.api.util.ManaUtil.getPlayerDiscounts;
import static com.hollingsworth.arsnouveau.api.util.SpellUtil.spellToBinaryBase64;
import static com.hollingsworth.arsnouveau.api.util.SpellUtil.spellToJson;

public class GuiSpellBook extends SpellSlottedScreen {

    public Spell clipboard = new Spell();
    public ClipboardWidget clipboardW;

    public int numLinks = 10;

    public EnterTextField spellNameBox;
    public SearchBar searchBar;
    public List<CraftingButton> craftingCells = new ArrayList<>();
    public List<AbstractSpellPart> unlockedSpells;
    public List<AbstractSpellPart> displayedGlyphs;

    public List<GlyphButton> glyphButtons = new ArrayList<>();
    public int page = 0;
    public PageButton nextButton;
    public PageButton previousButton;
    public ISpellValidator spellValidator;
    public String previousString = "";

    public int formTextRow = 0;
    public int augmentTextRow = 0;
    public int effectTextRow = 0;
    public int glyphsPerPage = 58;

    public int maxManaCache = 0;
    int currentCostCache = 0;


    public Renderable hoveredWidget = null;

    public List<AbstractSpellPart> spell = new ArrayList<>();
    public PageButton nextGlyphButton;
    public PageButton prevGlyphButton;
    public int spellWindowOffset = 0;
    public int bonusSlots = 0;
    public String spellname = "";

    public long timeOpened;


    public GuiSpellBook(InteractionHand hand) {
        super(hand);
        List<AbstractSpellPart> parts = playerCap == null ? new ArrayList<>() : new ArrayList<>(playerCap.getKnownGlyphs().stream().filter(AbstractSpellPart::shouldShowInSpellBook).toList());
        maxManaCache = ManaUtil.getMaxMana(player);
        parts.addAll(GlyphRegistry.getDefaultStartingSpells());
        int tier = 1;
        if (bookStack.getItem() instanceof SpellBook book) {
            tier = book.getTier().value;
            if (book.getTier() == SpellTier.CREATIVE) {
                parts = new ArrayList<>(GlyphRegistry.getSpellpartMap().values().stream().filter(AbstractSpellPart::shouldShowInSpellBook).toList());
            }
        }
        if (SpellCasterRegistry.hasCaster(bookStack)) {
            AbstractCaster<?> caster = SpellCasterRegistry.from(bookStack);
            if (caster != null) {
                bonusSlots = caster.getBonusGlyphSlots();
            }
        }
        this.unlockedSpells = parts;
        this.displayedGlyphs = new ArrayList<>(this.unlockedSpells);
        this.validationErrors = new LinkedList<>();
        this.spellValidator = new CombinedSpellValidator(
                ArsNouveauAPI.getInstance().getSpellCraftingSpellValidator(),
                new GlyphMaxTierValidator(tier),
                new GlyphKnownValidator(player.isCreative() || bookStack.is(ItemsRegistry.CREATIVE_SPELLBOOK.asItem()) ? null : playerCap)
        );
        spell = SpellCasterRegistry.from(bookStack).getSpell(selectedSpellSlot).mutable().recipe;
    }

    public void onBookstackUpdated(ItemStack stack) {
        super.onBookstackUpdated(stack);
        onSetCaster(selectedSpellSlot);
        rebuildWidgets();
    }

    private void onSetCaster(int slot) {
        this.selectedSpellSlot = slot;
        if (spellNameBox != null) {
            this.spellNameBox.setValue(caster.getSpellName(slot));
        }
        spell = SpellCasterRegistry.from(bookStack).getSpell(selectedSpellSlot).mutable().recipe;
    }

    @Override
    public void init() {
        super.init();
        timeOpened = System.currentTimeMillis();
        craftingCells = new ArrayList<>();
        resetCraftingCells();

        layoutAllGlyphs(page);
        addRenderableWidget(new CreateSpellButton(bookRight - 74, bookBottom - 13, this::onCreateClick, () -> this.validationErrors));
        addRenderableWidget(new ClearButton(bookRight - 129, bookBottom - 13, Component.translatable("ars_nouveau.spell_book_gui.clear"), this::clear));

        String previousSearch = "";
        if (searchBar != null) {
            previousSearch = searchBar.getValue();
        }

        searchBar = new SearchBar(Minecraft.getInstance().font, bookRight - 130, bookTop - 3);
        searchBar.onClear = (val) -> {
            this.onSearchChanged("");
            return null;
        };

        searchBar.setValue(previousSearch);
        searchBar.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
        searchBar.setResponder(this::onSearchChanged);

        spellNameBox = new EnterTextField(minecraft.font, bookLeft + 16, bookBottom - 13);

        spellNameBox.setValue(caster.getSpellName(selectedSpellSlot));
        addRenderableWidget(spellNameBox);
        addRenderableWidget(searchBar);

        // clipboard, copy and paste buttons
        clipboardW = addRenderableWidget(
                new ClipboardWidget(this)
        );
        addRenderableWidget(new CopyButton(this).withTooltip(Component.translatable("ars_nouveau.spell_book_gui.copy")));
        addRenderableWidget(new PasteButton(this).withTooltip(Component.translatable("ars_nouveau.spell_book_gui.paste")));

        initSpellSlots((slotButton) -> {
            onSetCaster(selectedSpellSlot);
            resetCraftingCells();
            updateWindowOffset(0); //includes validation
            rebuildWidgets();
        });

        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 22, DocAssets.DOCUMENTATION_TAB, this::onDocumentationClick)
                .withTooltip(Component.translatable("ars_nouveau.gui.notebook")));

        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 44, DocAssets.SPELL_STYLE_TAB, (b) -> {
            ParticleOverviewScreen.openScreen(this, bookStack, selectedSpellSlot, this.hand);
        }).withTooltip(Component.translatable("ars_nouveau.gui.spell_style")));
        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 68, DocAssets.FAMILIAR_TAB, this::onFamiliarClick)
                .withTooltip(Component.translatable("ars_nouveau.gui.familiar")));
        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 92, DocAssets.SETTINGS_TAB, (b) -> {
            Minecraft.getInstance().setScreen(new GuiSettingsScreen(this));
        }).withTooltip(Component.translatable("ars_nouveau.gui.settings")));

        addRenderableWidget(new GuiImageButton(bookLeft - 15, bookTop + 116, DocAssets.DISCORD_TAB, (b) -> {
            try {
                Util.getPlatform().openUri(new URI("https://discord.com/invite/y7TMXZu"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }).withTooltip(Component.translatable("ars_nouveau.gui.discord")));


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
            case AbstractAugment ignored -> 3;
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
                adjustedRowsPlaced = (adjustedRowsPlaced - 1) % MAX_ROWS;
            }
            int xOffset = 20 * (adjustedXPlaced % PER_ROW) + (nextPage ? 134 : 0);

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
        return (ServerConfig.INFINITE_SPELLS.get() ? ServerConfig.INF_SPELLS_LENGHT_MODIFIER.get() : 0) + bonusSlots;
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

    public void onDocumentationClick(Button button) {
        GuiUtils.openWiki(ArsNouveau.proxy.getPlayer());
    }

    public void onFamiliarClick(Button button) {
        Collection<ResourceLocation> familiarHolders = new ArrayList<>();
        IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(ArsNouveau.proxy.getPlayer());
        if (cap != null) {
            familiarHolders = cap.getUnlockedFamiliars().stream().map(s -> s.familiarHolder.getRegistryName()).collect(Collectors.toList());
        }
        Collection<ResourceLocation> finalFamiliarHolders = familiarHolders;
        Minecraft.getInstance().setScreen(new GuiFamiliarScreen(FamiliarRegistry.getFamiliarHolderMap().values().stream().filter(f -> finalFamiliarHolders.contains(f.getRegistryName())).collect(Collectors.toList()), this));
    }

    public void onCraftingSlotClick(Button button) {
        if (button instanceof CraftingButton craftingButton) {
            craftingButton.clear();
            if (craftingButton.slotNum < spell.size()) {
                spell.set(craftingButton.slotNum, null);
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
                    for (int i = spell.size(); i <= Math.max(button.slotNum, idx); i++) {
                        spell.add(null);
                    }
                    Collections.swap(spell, button.slotNum, idx);

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

        if (super.charTyped(pCodePoint, pModifiers)) {
            return true;
        }

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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        // only react if ctrl is pressed
        if (hasControlDown() && !spellNameBox.isFocused() && !searchBar.isFocused()) {
            if (isCopy(keyCode)) {
                onCopyOrExport(null);
                return true;
            } else if (isPaste(keyCode)) {
                onPasteOrImport(null);
                return true;
            } else if (isCut(keyCode)) {
                onCopyOrExport(null);
                clear(null);
                return true;
            }
        }

        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (!(keyCode >= GLFW.GLFW_KEY_LEFT_SHIFT && keyCode <= GLFW.GLFW_KEY_MENU) && !searchBar.isFocused() || !searchBar.active) {
            var prevFocus = this.getFocused();
            this.clearFocus();
            this.setFocused(searchBar);
            searchBar.active = true;
            if (!searchBar.keyPressed(keyCode, scanCode, modifiers)) {
                searchBar.active = false;
                this.clearFocus();
                this.setFocused(prevFocus);
                return false;
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_3 && hoveredWidget instanceof CraftingButton craftingCell) {
            int idx = -1;
            int emptySpace = -1;
            for (int i = 0; i < craftingCells.size(); i++) {
                CraftingButton cell = craftingCells.get(i);
                if (cell.slotNum == craftingCell.slotNum) {
                    while (cell.getAbstractSpellPart() == null && i < craftingCells.size()) {
                        i++;
                        cell = craftingCells.get(i);
                    }
                    idx = i;
                    continue;
                }

                if (idx != -1 && cell.getAbstractSpellPart() == null) {
                    emptySpace = i;
                    break;
                }
            }

            if (idx == -1 || emptySpace == -1) {
                return true;
            }

            for (int i = spell.size(); i <= emptySpace; i++) {
                spell.add(null);
            }
            spell.remove(emptySpace);
            for (int i = emptySpace; i >= idx + 1; i--) {
                CraftingButton cell = craftingCells.get(i);
                CraftingButton prev = craftingCells.get(i - 1);

                cell.setAbstractSpellPart(prev.getAbstractSpellPart());
            }

            spell.add(idx, null);
            craftingCells.get(idx).setAbstractSpellPart(null);
            this.setFocused(craftingCell);
            validate();
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

        if (allWereEmpty) spellNameBox.setValue("");

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
            Networking.sendToServer(new PacketUpdateCaster(spell.immutable(), this.selectedSpellSlot, this.spellNameBox.getValue(), hand == InteractionHand.MAIN_HAND));
            ParticleOverviewScreen.LAST_SELECTED_PART = null;
        }
    }

    public static void open(InteractionHand hand) {
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(hand);
        var caster = SpellCasterRegistry.from(Minecraft.getInstance().player.getItemInHand(hand));
        if (lastOpenedScreen == null) {
            Minecraft.getInstance().setScreen(new GuiSpellBook(hand));
        } else if (lastOpenedScreen instanceof ParticleOverviewScreen particleOverviewScreen) {
            ParticleOverviewScreen.openScreen(particleOverviewScreen.previousScreen, stack, caster.getCurrentSlot(), hand);
        } else {
            Minecraft.getInstance().setScreen(new GuiSpellBook(hand));
        }
    }

    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        int formOffset = 0;
        if (formTextRow >= 1) {
            graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.form").getString(), formTextRow > 6 ? 154 : 20, 5 + 18 * (formTextRow + (formTextRow == 1 ? 0 : 1)), -8355712, false);
            formOffset = 1;
        }

        if (effectTextRow >= 1) {
            graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.effect").getString(), effectTextRow > 6 ? 154 : 20, 5 + 18 * (effectTextRow % 7 + formOffset), -8355712, false);
        }
        if (augmentTextRow >= 1) {
            graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.augment").getString(), augmentTextRow > 6 ? 154 : 20, 5 + 18 * (augmentTextRow + formOffset), -8355712, false);
        }

        int manaLength = 96;
        if (maxManaCache > 0) {
            //keep the mana bar length between -1 and 96 to avoid over/underflow
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
        int manaOffset = (int) ((ClientInfo.ticksInGame + partialTicks) / 3 % 33) * 6;

        // default length is 96
        // rainbow effect for perfect match is currently disabled by the >=
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
            int offset = offsetLeft - maxWidth / 2 + maxWidth - minecraft.font.width(text);

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


        List<AbstractSpellPart> slicedSpell = spell.subList(0, spell.isEmpty() ? 0 : lastGlyphNoGap + 1);
        // Set validation errors on all the glyph buttons
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

    }

    @Override
    protected TooltipComponent getClientImageTooltip(int mouseX, int mouseY) {
        for (Renderable renderable : renderables) {

            if (renderable instanceof AbstractWidget widget && !GuiUtils.isMouseInRelativeRange(mouseX, mouseY, widget)) {
                continue;
            }

            if (renderable instanceof GlyphButton widget) {
                return widget.abstractSpellPart.spellSchools.isEmpty() ? null : new SchoolTooltip(widget.abstractSpellPart);
            } else if (renderable instanceof GuiSpellSlot spellSlot) {
                if (spellSlot.isSelected) {
                    if (spell.isEmpty()) {
                        return null;
                    }
                    return new SpellTooltip(new Spell(spell), false);
                }

                Spell spellInSlot = caster.getSpell(spellSlot.slotNum);
                if (spellInSlot.isEmpty())
                    return null;
                return new SpellTooltip(spellInSlot, false);
            }
        }
        return null;
    }


    public void onCopyOrExport(Button ignoredB) {
        if (hasShiftDown() && clipboard != null && !clipboard.isEmpty()) {
            // copy the spell to the clipboard
            Spell spell = fetchCurrentSpell();
            getMinecraft().keyboardHandler.setClipboard(hasAltDown() ? spellToBinaryBase64(spell) : spellToJson(spell));
        } else if (spell != null && !spell.isEmpty()) {
            clipboard = fetchCurrentSpell();
            clipboardW.setClipboard(clipboard.mutable());
        }
    }

    public void onPasteOrImport(Button ignoredB) {
        if (Screen.hasShiftDown()) {
            String clipboardString = Minecraft.getInstance().keyboardHandler.getClipboard();
            if (!clipboardString.isEmpty()) {
                Spell spell = hasAltDown() ? Spell.fromBinaryBase64(clipboardString) : Spell.fromJson(clipboardString);
                if (spell.isValid()) {
                    clipboard = spell;
                    spellNameBox.setValue(spell.name());
                    clipboardW.setClipboard(clipboard.mutable());
                }
            }
        } else {
            if (clipboard == null || clipboard.isEmpty()) {
                return;
            }
            // before pasting, trim the spell to the max size supported by the spell book
            int maxSize = 10 + getExtraGlyphSlots();
            Spell oldSpell = fetchCurrentSpell();
            Spell.Mutable clipSpell = clipboard.mutable();
            spell = clipboard.size() > maxSize ? clipSpell.recipe.subList(0, maxSize) : clipSpell.recipe;

            // validate the spell
            validate();

            if (validationErrors.isEmpty()) {
                spell.removeIf(Objects::isNull);
                if (clipboard.color() != ParticleColor.DEFAULT) // if color is default, it's likely absent, keep the old one
                    Networking.sendToServer(new PacketUpdateSpellColors(this.selectedSpellSlot, clipboard.color(), this.hand == InteractionHand.MAIN_HAND));
                if (clipboard.sound() != ConfiguredSpellSound.DEFAULT) // if sound is default, it's likely absent, keep the old one
                    Networking.sendToServer(new PacketSetSound(this.selectedSpellSlot, clipboard.sound(), this.hand == InteractionHand.MAIN_HAND));
                if (!clipboard.particleTimeline().timelines().isEmpty()) {
                    Networking.sendToServer(new PacketUpdateParticleTimeline(this.selectedSpellSlot, clipboard.particleTimeline(), this.hand == InteractionHand.MAIN_HAND));
                }
                Networking.sendToServer(new PacketUpdateCaster(new Spell(spell), this.selectedSpellSlot, this.spellNameBox.getValue(), hand == InteractionHand.MAIN_HAND));
            } else {
                // if the spell is invalid, set the spell back to the old one
                spell = oldSpell.mutable().recipe;
            }
        }
    }


    public Spell fetchCurrentSpell() {
        if (caster != null) {
            return caster.getSpell(selectedSpellSlot);
        }
        return new Spell(spell, spellname);
    }
}
