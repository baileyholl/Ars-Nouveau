package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.GlyphRecipeTooltip;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.SchoolTooltip;
import com.hollingsworth.arsnouveau.client.gui.buttons.*;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetScribeRecipe;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GlyphUnlockMenu extends BaseBook {


    public List<RecipeHolder<GlyphRecipe>> displayedGlyphs = new ArrayList<>();
    public List<RecipeHolder<GlyphRecipe>> allParts = new ArrayList<>();
    public int page = 0;
    public PageButton nextButton;
    public PageButton previousButton;
    public List<UnlockGlyphButton> glyphButtons = new ArrayList<>();
    public NoShadowTextField searchBar;
    public String previousString = "";
    int maxPerPage = 78;
    int tier1Row = 0;
    int tier2Row = 0;
    int tier3Row = 0;
    BlockPos scribesPos;
    Filter filterSelected = Filter.ALL;
    public RecipeHolder<GlyphRecipe> hoveredRecipe;
    public RecipeHolder<GlyphRecipe> selectedRecipe;

    enum Filter {
        ALL,
        TIER1,
        TIER2,
        TIER3
    }

    String orderingTitle = "";

    List<ItemButton> itemButtons = new ArrayList<>();
    List<SelectableButton> filterButtons = new ArrayList<>();

    public GlyphUnlockMenu(BlockPos pos) {
        super();
        allParts = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.GLYPH_TYPE.get());
        this.displayedGlyphs = new ArrayList<>(allParts);
        this.scribesPos = pos;
    }

    public static void open(BlockPos scribePos) {
        Minecraft.getInstance().setScreen(new GlyphUnlockMenu(scribePos));
    }

    SelectableButton all;
    SelectableButton tier1;
    SelectableButton tier2;
    SelectableButton tier3;

    @Override
    public void init() {
        super.init();
        this.orderingTitle = Component.translatable("ars_nouveau.all_glyphs").getString();

        searchBar = new NoShadowTextField(minecraft.font, bookRight - 73, bookTop + 2,
                54, 12, null, Component.translatable("ars_nouveau.spell_book_gui.search"));
        searchBar.setBordered(false);
        searchBar.setTextColor(12694931);
        searchBar.onClear = (val) -> {
            this.onSearchChanged("");
            return null;
        };
        if (searchBar.getValue().isEmpty())
            searchBar.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
        searchBar.setResponder((val) -> this.onSearchChanged(val));
        addRenderableWidget(searchBar);
        addRenderableWidget(new GuiImageButton(bookRight - 71, bookBottom - 11, 50, 12, ArsNouveau.prefix("textures/gui/create_icon.png"), this::onSelectClick));
        this.nextButton = addRenderableWidget(new PageButton(bookRight - 20, bookBottom - 10, true, this::onPageIncrease, true));
        this.previousButton = addRenderableWidget(new PageButton(bookLeft - 5, bookBottom - 10, false, this::onPageDec, true));
        updateNextPageButtons();
        previousButton.active = false;
        previousButton.visible = false;
        layoutAllGlyphs(0);

        //Crafting slots
        for(ItemButton button : itemButtons){
            removeWidget(button);
        }
        itemButtons = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int offset = i >= 5 ? 14 : 0;
            ItemButton cell = new ItemButton(this, bookLeft + 19 + 24 * i + offset, bookBottom - 43);
            addRenderableWidget(cell);
            itemButtons.add(cell);
        }

        all = (SelectableButton) new SelectableButton(bookRight - 8, bookTop + 22, 0, 0, 23, 20, 23, 20, ArsNouveau.prefix("textures/gui/filter_tab_all.png"),
                ArsNouveau.prefix("textures/gui/filter_tab_all_selected.png"), (b) -> this.setFilter(Filter.ALL, (SelectableButton) b, Component.translatable("ars_nouveau.all_glyphs").getString())).withTooltip(Component.translatable("ars_nouveau.all_glyphs"));
        all.isSelected = true;
        tier1 = (SelectableButton) new SelectableButton(bookRight - 8, bookTop + 46, 0, 0, 23, 20, 23, 20, ArsNouveau.prefix("textures/gui/filter_tab_tier1.png"),
                ArsNouveau.prefix("textures/gui/filter_tab_tier1_selected.png"), (b) -> this.setFilter(Filter.TIER1, (SelectableButton) b, Component.translatable("ars_nouveau.tier", 1).getString())).withTooltip(Component.translatable("ars_nouveau.tier", 1));

        tier2 = (SelectableButton) new SelectableButton(bookRight - 8, bookTop + 70, 0, 0, 23, 20, 23, 20, ArsNouveau.prefix("textures/gui/filter_tab_tier2.png"),
                ArsNouveau.prefix("textures/gui/filter_tab_tier2_selected.png"), (b) -> this.setFilter(Filter.TIER2, (SelectableButton) b, Component.translatable("ars_nouveau.tier", 2).getString())).withTooltip(Component.translatable("ars_nouveau.tier", 2));
        tier3 = (SelectableButton) new SelectableButton(bookRight - 8, bookTop + 94, 0, 0, 23, 20, 23, 20, ArsNouveau.prefix("textures/gui/filter_tab_tier3.png"),
                ArsNouveau.prefix("textures/gui/filter_tab_tier3_selected.png"), (b) -> this.setFilter(Filter.TIER3, (SelectableButton) b, Component.translatable("ars_nouveau.tier", 3).getString())).withTooltip(Component.translatable("ars_nouveau.tier", 3));
        filterButtons = new ArrayList<>();
        filterButtons.add(all);
        filterButtons.add(tier2);
        filterButtons.add(tier1);
        filterButtons.add(tier3);
        for (SelectableButton button : filterButtons) {
            addRenderableWidget(button);
        }
        updateRecipeButtons();
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
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

    public void setFilter(Filter filter, SelectableButton button, String displayTitle) {
        displayedGlyphs = allParts;
        for (SelectableButton b : filterButtons) {
            b.isSelected = false;
        }
        this.filterSelected = filter;
        button.isSelected = true;
        this.orderingTitle = displayTitle;
        onSearchChanged(this.searchBar.value);
        resetPageState();
    }

    private void onSelectClick(Button button) {
        if (selectedRecipe != null) {
            Networking.sendToServer(new PacketSetScribeRecipe(scribesPos, selectedRecipe.id()));
            Minecraft.getInstance().setScreen(null);
        }
    }

    public void updateNextPageButtons() {
        if (displayedGlyphs.size() < maxPerPage) {
            nextButton.visible = false;
            nextButton.active = false;
        } else {
            nextButton.visible = true;
            nextButton.active = true;
        }
    }

    public void onSearchChanged(String str) {
        previousString = str;
        if (!str.isEmpty()) {
            searchBar.setSuggestion("");
            displayedGlyphs = new ArrayList<>();

            for (RecipeHolder<GlyphRecipe> spellPart : allParts) {
                if (spellPart.value().getSpellPart().getLocaleName().toLowerCase().contains(searchBar.value.toLowerCase())) {
                    displayedGlyphs.add(spellPart);
                }
            }

            for (Renderable w : renderables) {
                if (w instanceof GlyphButton glyphButton) {
                    if (glyphButton.abstractSpellPart.getRegistryName() != null) {
                        AbstractSpellPart part = GlyphRegistry.getSpellpartMap().get(glyphButton.abstractSpellPart.getRegistryName());
                        if (part != null) {
                            glyphButton.visible = part.getLocaleName().toLowerCase().contains(searchBar.value.toLowerCase());
                        }
                    }
                }
            }
        } else {
            // Reset our book on clear
            searchBar.setSuggestion(Component.translatable("ars_nouveau.spell_book_gui.search").getString());
            displayedGlyphs = allParts;
            for (Renderable w : renderables) {
                if (w instanceof GlyphButton) {
                    ((GlyphButton) w).visible = true;
                }
            }
        }
        displayedGlyphs = applyFilter(displayedGlyphs);
        resetPageState();
    }

    public void resetPageState() {
        updateNextPageButtons();
        this.page = 0;
        previousButton.active = false;
        previousButton.visible = false;
        layoutAllGlyphs(page);
    }


    public void layoutAllGlyphs(int page) {
        clearButtons(glyphButtons);
        tier1Row = -1;
        tier2Row = -1;
        tier3Row = -1;

        final int PER_ROW = 6;
        final int MAX_ROWS = 6;
        boolean nextPage = false;
        int xStart = nextPage ? bookLeft + 154 : bookLeft + 20;
        int adjustedRowsPlaced = 0;
        int yStart = bookTop + 20;
        List<RecipeHolder<GlyphRecipe>> sorted = new ArrayList<>(displayedGlyphs);
        sorted.sort(CreativeTabRegistry.COMPARE_TIER_THEN_NAME);
        sorted = sorted.subList(maxPerPage * page, Math.min(sorted.size(), maxPerPage * (page + 1)));
        int adjustedXPlaced = 0;
        tier1Row = 0;
        adjustedRowsPlaced++;
        for (int i = 0; i < sorted.size(); i++) {
            var holder = sorted.get(i);
            var spellPart = holder.value().getSpellPart();
            if (adjustedXPlaced >= PER_ROW) {
                adjustedRowsPlaced++;

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
            UnlockGlyphButton cell = new UnlockGlyphButton(xStart + xOffset, yPlace, false, holder, this::onGlyphClick);
            IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(Minecraft.getInstance().player);
            if (cap != null) {
                if (cap.knowsGlyph(spellPart) || GlyphRegistry.getDefaultStartingSpells().contains(spellPart)) {
                    cell.playerKnows = true;
                }
            }
            addRenderableWidget(cell);
            glyphButtons.add(cell);
            adjustedXPlaced++;
        }
    }

    public List<RecipeHolder<GlyphRecipe>> applyFilter(List<RecipeHolder<GlyphRecipe>> spellParts) {
        if (filterSelected == Filter.ALL)
            return spellParts;
        if (filterSelected == Filter.TIER1) {
            return spellParts.stream().filter(a -> a.value().getSpellPart().getConfigTier().value == 1).collect(Collectors.toList());
        }
        if (filterSelected == Filter.TIER2) {
            return spellParts.stream().filter(a -> a.value().getSpellPart().getConfigTier().value == 2).collect(Collectors.toList());
        }
        return spellParts.stream().filter(a -> a.value().getSpellPart().getConfigTier().value == 3).collect(Collectors.toList());
    }

    public void onGlyphClick(Button button) {
        for (UnlockGlyphButton button1 : glyphButtons) {
            button1.selected = false;
        }
        if (button instanceof UnlockGlyphButton unlockGlyphButton) {
            this.selectedRecipe = unlockGlyphButton.recipe;
            unlockGlyphButton.selected = true;
            updateRecipeButtons();
        }
    }

    public void updateRecipeButtons(){
        if (selectedRecipe == null)
            return;
        for (ItemButton itemButton : itemButtons) {
            itemButton.visible = false;
            itemButton.ingredient = Ingredient.EMPTY;
        }

        for (int i = 0; i < selectedRecipe.value().inputs.size(); i++) {
            if (i > itemButtons.size())
                break;
            itemButtons.get(i).visible = true;
            itemButtons.get(i).ingredient = selectedRecipe.value().inputs.get(i);
        }
    }

    public void onPageIncrease(Button button) {
        page++;
        if (displayedGlyphs.size() < maxPerPage * (page + 1)) {
            nextButton.visible = false;
            nextButton.active = false;
        }
        previousButton.active = true;
        previousButton.visible = true;
        layoutAllGlyphs(page);
    }

    public void onPageDec(Button button) {
        page--;
        if (page == 0) {
            previousButton.active = false;
            previousButton.visible = false;
        }

        if (displayedGlyphs.size() > maxPerPage * (page + 1)) {
            nextButton.visible = true;
            nextButton.active = true;
        }
        layoutAllGlyphs(page);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.hoveredRecipe = null;
        if (getHoveredRenderable(mouseX, mouseY) instanceof UnlockGlyphButton button) {
            this.hoveredRecipe = button.recipe;
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);

        graphics.drawString(font, orderingTitle, tier1Row > 7 ? 154 : 20, 5 + 18 * (tier1Row + (tier1Row == 1 ? 0 : 1)), -8355712, false);

        graphics.blit(ArsNouveau.prefix("textures/gui/create_paper.png"), 216, 175, 0, 0, 56, 15, 56, 15);

        graphics.blit(ArsNouveau.prefix("textures/gui/search_paper.png"), 203, 0, 0, 0, 72, 15, 72, 15);
        graphics.drawString(font, Component.translatable("ars_nouveau.spell_book_gui.select"), 233, 179, -8355712, false);
    }

    public void drawTooltip(GuiGraphics stack, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        super.collectTooltips(stack, mouseX, mouseY, tooltip);
        if (hoveredRecipe != null) {
            MutableComponent component = Component.translatable("ars_nouveau.levels_required", ScribesTile.getLevelsFromExp(hoveredRecipe.value().exp)).withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
            tooltip.add(component);
        }
        List<ClientTooltipComponent> components = new ArrayList<>(net.neoforged.neoforge.client.ClientHooks.gatherTooltipComponents(ItemStack.EMPTY, tooltip, mouseX, width, height, this.font));
        if (hoveredRecipe != null)
            components.add(new GlyphRecipeTooltip(hoveredRecipe.value().inputs));
        if (getHoveredRenderable(mouseX, mouseY) instanceof UnlockGlyphButton button && !button.spellPart.spellSchools.isEmpty())
            components.add(1, new SchoolTooltip.SchoolTooltipRenderer(button.spellPart));

        renderTooltipInternal(stack, components, mouseX, mouseY);
    }

    public void renderTooltipInternal(GuiGraphics graphics, List<ClientTooltipComponent> pClientTooltipComponents, int pMouseX, int pMouseY) {
        if (!pClientTooltipComponents.isEmpty()) {
            PoseStack pPoseStack = graphics.pose();
            net.neoforged.neoforge.client.event.RenderTooltipEvent.Pre preEvent = net.neoforged.neoforge.client.ClientHooks.onRenderTooltipPre(ItemStack.EMPTY, graphics, pMouseX, pMouseY, width, height, pClientTooltipComponents, this.font, DefaultTooltipPositioner.INSTANCE);
            if (preEvent.isCanceled()) return;
            int i = 0;
            int j = pClientTooltipComponents.size() == 1 ? -2 : 0;

            for (ClientTooltipComponent clienttooltipcomponent : pClientTooltipComponents) {
                int k = clienttooltipcomponent.getWidth(preEvent.getFont());
                if (k > i) {
                    i = k;
                }

                j += clienttooltipcomponent.getHeight();
            }

            int j2 = preEvent.getX() + 12;
            int k2 = preEvent.getY() - 12;
            if (j2 + i > this.width) {
                j2 -= 28 + i;
            }

            if (k2 + j + 6 > this.height) {
                k2 = this.height - j - 6;
            }
            pPoseStack.pushPose();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            Matrix4f matrix4f = pPoseStack.last().pose();
            net.neoforged.neoforge.client.event.RenderTooltipEvent.Color colorEvent = net.neoforged.neoforge.client.ClientHooks.onRenderTooltipColor(ItemStack.EMPTY, graphics, j2, k2, preEvent.getFont(), pClientTooltipComponents);

            graphics.fillGradient(j2 - 3, k2 - 4, j2 + i + 3, k2 - 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundStart());
            graphics.fillGradient(j2 - 3, k2 + j + 3, j2 + i + 3, k2 + j + 4, 400, colorEvent.getBackgroundEnd(), colorEvent.getBackgroundEnd());
            graphics.fillGradient(j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
            graphics.fillGradient(j2 - 4, k2 - 3, j2 - 3, k2 + j + 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
            graphics.fillGradient(j2 + i + 3, k2 - 3, j2 + i + 4, k2 + j + 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
            graphics.fillGradient(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, 400, colorEvent.getBorderStart(), colorEvent.getBorderEnd());
            graphics.fillGradient(j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, 400, colorEvent.getBorderStart(), colorEvent.getBorderEnd());
            graphics.fillGradient(j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, 400, colorEvent.getBorderStart(), colorEvent.getBorderStart());
            graphics.fillGradient(j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, 400, colorEvent.getBorderEnd(), colorEvent.getBorderEnd());

            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            MultiBufferSource.BufferSource multibuffersource$buffersource = graphics.bufferSource();
            pPoseStack.translate(0.0D, 0.0D, 400.0D);
            int l1 = k2;

            for (int i2 = 0; i2 < pClientTooltipComponents.size(); ++i2) {
                ClientTooltipComponent clienttooltipcomponent1 = pClientTooltipComponents.get(i2);
                clienttooltipcomponent1.renderText(preEvent.getFont(), j2, l1, matrix4f, multibuffersource$buffersource);
                l1 += clienttooltipcomponent1.getHeight() + (i2 == 0 ? 2 : 0);
            }
            l1 = k2;

            pPoseStack.translate(0, 0, 600);
            for (int l2 = 0; l2 < pClientTooltipComponents.size(); ++l2) {
                ClientTooltipComponent clienttooltipcomponent2 = pClientTooltipComponents.get(l2);
                clienttooltipcomponent2.renderImage(preEvent.getFont(), j2, l1, graphics);
                l1 += clienttooltipcomponent2.getHeight() + (l2 == 0 ? 2 : 0);
            }
            pPoseStack.popPose();

//            this.itemRenderer.blitOffset = f;
        }
    }
}
