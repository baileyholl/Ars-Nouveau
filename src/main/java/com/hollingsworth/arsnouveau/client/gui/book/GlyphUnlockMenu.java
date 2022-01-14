package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.GlyphRecipeTooltip;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.UnlockGlyphButton;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GlyphUnlockMenu extends BaseBook{


    public List<AbstractSpellPart> displayedGlyphs = new ArrayList<>();
    public List<AbstractSpellPart> allParts = new ArrayList<>();
    public int page = 0;
    public PageButton nextButton;
    public PageButton previousButton;
    public List<UnlockGlyphButton> glyphButtons = new ArrayList<>();
    public NoShadowTextField searchBar;
    public String previousString = "";
    public ArsNouveauAPI api = ArsNouveauAPI.getInstance();
    int maxPerPage = 96;
    int tier1Row = 0;
    int tier2Row = 0;
    int tier3Row = 0;
    BlockPos scribesPos;
    Filter filterSelected = Filter.ALL;
    enum Filter{
        ALL,
        TIER1,
        TIER2,
        TIER3
    }

    public GlyphUnlockMenu(BlockPos pos){
        super();
        allParts = new ArrayList<>(ArsNouveauAPI.getInstance().getSpellpartMap().values());
        this.displayedGlyphs = new ArrayList<>(allParts);
        this.scribesPos = pos;
    }

    @Override
    public void init() {
        super.init();
        searchBar = new NoShadowTextField(minecraft.font, bookRight - 73, bookTop +2,
                54, 12, null, new TranslatableComponent("ars_nouveau.spell_book_gui.search"));
        searchBar.setBordered(false);
        searchBar.setTextColor(12694931);
        searchBar.onClear = (val) -> {
            this.onSearchChanged("");
            return null;
        };
        if(searchBar.getValue().isEmpty())
            searchBar.setSuggestion(new TranslatableComponent("ars_nouveau.spell_book_gui.search").getString());
        searchBar.setResponder(this::onSearchChanged);
        addRenderableWidget(searchBar);

        this.nextButton = addRenderableWidget(new PageButton(bookRight -20, bookBottom -10, true, this::onPageIncrease, true));
        this.previousButton = addRenderableWidget(new PageButton(bookLeft - 5 , bookBottom -10, false, this::onPageDec, true));
        updateNextPageButtons();
        previousButton.active = false;
        previousButton.visible = false;
        layoutAllGlyphs(0);
    }


    public void updateNextPageButtons(){
        if(displayedGlyphs.size() < maxPerPage){
            nextButton.visible = false;
            nextButton.active = false;
        }else{
            nextButton.visible = true;
            nextButton.active = true;
        }
    }

    public static void open(BlockPos scribePos){
        Minecraft.getInstance().setScreen(new GlyphUnlockMenu(scribePos));
    }

    public void onSearchChanged(String str){
        if(str.equals(previousString))
            return;
        previousString = str;

        if (!str.isEmpty()) {
            searchBar.setSuggestion("");
            displayedGlyphs = new ArrayList<>();

            for (AbstractSpellPart spellPart : allParts) {
                if (spellPart.getLocaleName().toLowerCase().contains(str.toLowerCase())) {
                    displayedGlyphs.add(spellPart);
                }
            }
            // Set visibility of Cast Methods and Augments
            for(Widget w : renderables) {
                if(w instanceof GlyphButton glyphButton) {
                    if (glyphButton.spell_id != null) {
                        AbstractSpellPart part = api.getSpellpartMap().get(glyphButton.spell_id);
                        if (part != null) {
                            glyphButton.visible = part.getLocaleName().toLowerCase().contains(str.toLowerCase());
                        }
                    }
                }
            }
        } else {
            // Reset our book on clear
            searchBar.setSuggestion(new TranslatableComponent("ars_nouveau.spell_book_gui.search").getString());
            displayedGlyphs = allParts;
            for(Widget w : renderables){
                if(w instanceof GlyphButton ) {
                    ((GlyphButton) w).visible = true;
                }
            }
        }
        resetPageState();
    }

    public void resetPageState(){
        updateNextPageButtons();
        this.page = 0;
        previousButton.active = false;
        previousButton.visible = false;
        layoutAllGlyphs(page);
    }

    public void layoutAllGlyphs(int page){
        clearButtons(glyphButtons);
        tier1Row = -1;
        tier2Row = -1;
        tier3Row = -1;
//        formTextRow = 0;
//        augmentTextRow = 0;
//        effectTextRow = 0;
        final int PER_ROW = 6;
        final int MAX_ROWS = 7;
        boolean nextPage = false;
        int xStart = nextPage ? bookLeft + 154 : bookLeft + 20;
        int adjustedRowsPlaced = 0;
        int yStart = bookTop + 20;
        boolean foundTier1 = false;
        boolean foundTier2 = false;
        boolean foundTier3 = false;
        List<AbstractSpellPart> sorted = new ArrayList<>();
        Comparator<AbstractSpellPart> spellPartComparator = new Comparator<AbstractSpellPart>() {
            @Override
            public int compare(AbstractSpellPart o1, AbstractSpellPart o2) {

                return fromType(o1) - fromType(o2);
            }

            public int fromType(AbstractSpellPart spellPart){
                if(spellPart instanceof AbstractCastMethod)
                    return 1;
                if(spellPart instanceof AbstractAugment)
                    return 2;
                return 3;
            }
        }.thenComparingInt(o -> o.getTier().value)
                .thenComparing(AbstractSpellPart::getLocaleName);

//        sorted.addAll(displayedGlyphs.stream().filter(s -> s.getTier().value == 1).collect(Collectors.toList()));
//        sorted.addAll(displayedGlyphs.stream().filter(s -> s.getTier().value == 2).collect(Collectors.toList()));
//        sorted.addAll(displayedGlyphs.stream().filter(s -> s.getTier().value == 3).collect(Collectors.toList()));
        sorted.addAll(displayedGlyphs);
        sorted.sort(spellPartComparator);
        sorted = sorted.subList(maxPerPage * page, Math.min(sorted.size(), maxPerPage * (page + 1)));
        int adjustedXPlaced = 0;
        int totalRowsPlaced = 0;
        int row_offset = page == 0 ? 2 : 0;
        tier1Row = 0;
        adjustedRowsPlaced++;
        for(int i = 0; i < sorted.size(); i++){
            AbstractSpellPart part = sorted.get(i);

            if (adjustedXPlaced >= PER_ROW) {
                adjustedRowsPlaced++;
                totalRowsPlaced++;
                adjustedXPlaced = 0;
            }

            if(adjustedRowsPlaced > MAX_ROWS){
                if(nextPage){
                    break;
                }
                nextPage = true;
                adjustedXPlaced = 0;
                adjustedRowsPlaced = 0;
            }
            int xOffset = 20 * ((adjustedXPlaced ) % PER_ROW) + (nextPage ? 134 :0);
            int yPlace = adjustedRowsPlaced * 18 + yStart;
            UnlockGlyphButton cell = new UnlockGlyphButton(this, xStart + xOffset, yPlace, false, part.getIcon(), part.getId());
            addRenderableWidget(cell);
            glyphButtons.add(cell);
            adjustedXPlaced++;
        }
    }

    public void onGlyphClick(Button button){
//        GlyphButton button1 = (GlyphButton) button;
//
//        if (button1.validationErrors.isEmpty()) {
//            for (CraftingButton b : craftingCells) {
//                if (b.resourceIcon.equals("")) {
//                    b.resourceIcon = button1.resourceIcon;
//                    b.spellTag = button1.spell_id;
//                    validate();
//                    return;
//                }
//            }
//        }
    }

    public void clearButtons( List<UnlockGlyphButton> glyphButtons){
        for (UnlockGlyphButton b : glyphButtons) {
            renderables.remove(b);
            children().remove(b);
        }
        glyphButtons.clear();
    }

    public void onPageIncrease(Button button){
        page++;
        if(displayedGlyphs.size() < maxPerPage * (page + 1)){
            nextButton.visible = false;
            nextButton.active = false;
        }
        previousButton.active = true;
        previousButton.visible = true;
        layoutAllGlyphs(page);
    }

    public void onPageDec(Button button){
        page--;
        if(page == 0){
            previousButton.active = false;
            previousButton.visible = false;
        }

        if(displayedGlyphs.size() > maxPerPage * (page + 1)){
            nextButton.visible = true;
            nextButton.active = true;
        }
        layoutAllGlyphs(page);
    }


    @Override
    public void drawBackgroundElements(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        if(tier1Row != -1) {
            minecraft.font.draw(stack, new TranslatableComponent("ars_nouveau.tier", 1).getString(), tier1Row > 7 ? 154 : 20 ,  5 + 18 * (tier1Row + (tier1Row == 1 ? 0 : 1)), -8355712);
        }
        if(tier2Row != -1) {
            minecraft.font.draw(stack, new TranslatableComponent("ars_nouveau.tier", 2).getString(), tier2Row > 7 ? 154 : 20,  5 + 18 * (tier2Row  + 1), -8355712);
        }
        if(tier3Row >= 1) {
            minecraft.font.draw(stack, new TranslatableComponent("ars_nouveau.tier", 3).getString(), tier3Row > 7 ? 154 : 20,  5 + 18 * (tier3Row + 1), -8355712);
        }
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_paper.png"), 216, 179, 0, 0, 56, 15,56,15, stack);

        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/search_paper.png"), 203, 0, 0, 0, 72, 15,72,15, stack);
        minecraft.font.draw(stack, new TranslatableComponent("ars_nouveau.spell_book_gui.create"), 233, 183, -8355712);
    }

    public void drawTooltip(PoseStack stack, int mouseX, int mouseY) {
        if (tooltip != null && !tooltip.isEmpty()) {

            List<ClientTooltipComponent> components = new ArrayList<>(net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(ItemStack.EMPTY, tooltip, mouseX, width, height, this.font, this.font));
            components.add(new GlyphRecipeTooltip(new ArrayList<>()));
            renderTooltipInternal(stack, components, mouseX, mouseY);
        }
    }
    private void renderTooltipInternal(PoseStack pPoseStack, List<ClientTooltipComponent> pClientTooltipComponents, int pMouseX, int pMouseY) {
        if (!pClientTooltipComponents.isEmpty()) {
            net.minecraftforge.client.event.RenderTooltipEvent.Pre preEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipPre(ItemStack.EMPTY, pPoseStack, pMouseX, pMouseY, width, height, pClientTooltipComponents, this.font, this.font);
            if (preEvent.isCanceled()) return;
            int i = 0;
            int j = pClientTooltipComponents.size() == 1 ? -2 : 0;

            for(ClientTooltipComponent clienttooltipcomponent : pClientTooltipComponents) {
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
            int l = -267386864;
            int i1 = 1347420415;
            int j1 = 1344798847;
            int k1 = 400;
            float f = this.itemRenderer.blitOffset;
            this.itemRenderer.blitOffset = 400.0F;
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            Matrix4f matrix4f = pPoseStack.last().pose();
            net.minecraftforge.client.event.RenderTooltipEvent.Color colorEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipColor(ItemStack.EMPTY, pPoseStack, j2, k2, preEvent.getFont(), pClientTooltipComponents);
            fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 4, j2 + i + 3, k2 - 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundStart());
            fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + j + 3, j2 + i + 3, k2 + j + 4, 400, colorEvent.getBackgroundEnd(), colorEvent.getBackgroundEnd());
            fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
            fillGradient(matrix4f, bufferbuilder, j2 - 4, k2 - 3, j2 - 3, k2 + j + 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
            fillGradient(matrix4f, bufferbuilder, j2 + i + 3, k2 - 3, j2 + i + 4, k2 + j + 3, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
            fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, 400, colorEvent.getBorderStart(), colorEvent.getBorderEnd());
            fillGradient(matrix4f, bufferbuilder, j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, 400, colorEvent.getBorderStart(), colorEvent.getBorderEnd());
            fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, 400, colorEvent.getBorderStart(), colorEvent.getBorderStart());
            fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, 400, colorEvent.getBorderEnd(), colorEvent.getBorderEnd());
            RenderSystem.enableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            bufferbuilder.end();
            BufferUploader.end(bufferbuilder);
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            pPoseStack.translate(0.0D, 0.0D, 400.0D);
            int l1 = k2;

            for(int i2 = 0; i2 < pClientTooltipComponents.size(); ++i2) {
                ClientTooltipComponent clienttooltipcomponent1 = pClientTooltipComponents.get(i2);
                clienttooltipcomponent1.renderText(preEvent.getFont(), j2, l1, matrix4f, multibuffersource$buffersource);
                l1 += clienttooltipcomponent1.getHeight() + (i2 == 0 ? 2 : 0);
            }

            multibuffersource$buffersource.endBatch();
            pPoseStack.popPose();
            l1 = k2;

            for(int l2 = 0; l2 < pClientTooltipComponents.size(); ++l2) {
                ClientTooltipComponent clienttooltipcomponent2 = pClientTooltipComponents.get(l2);
                clienttooltipcomponent2.renderImage(preEvent.getFont(), j2, l1, pPoseStack, this.itemRenderer, 400);
                l1 += clienttooltipcomponent2.getHeight() + (l2 == 0 ? 2 : 0);
            }

            this.itemRenderer.blitOffset = f;
        }
    }
}
