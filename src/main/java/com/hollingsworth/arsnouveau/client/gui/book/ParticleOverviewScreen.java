package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.timelines.IParticleTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.IParticleTimelineType;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.gui.buttons.ClearButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.PropertyButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectableButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateParticleTimeline;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ParticleOverviewScreen extends SpellSlottedScreen {
    TimelineMap.MutableTimelineMap timelineMap;

    public List<IParticleTimelineType<?>> selectedTimeline = null;

    List<AbstractWidget> rightPageWidgets = new ArrayList<>();
    private final Map<AbstractWidget, Boolean> previewWidgetVisibility = new IdentityHashMap<>();

    ParticleConfigWidgetProvider propertyWidgetProvider;
    int rowOffset = 0;
    boolean hasMoreElements = false;
    boolean hasPreviousElements = false;
    public static int lastOpenedHash;
    public static ParticleOverviewScreen lastScreen;

    GuiSpellBook previousScreen;
    GuiImageButton upButton;
    GuiImageButton downButton;
    GuiImageButton previewButton;
    boolean allExpanded = false;
    List<PropWidgetList> propLists = new ArrayList<>();
    ParticlePreviewWidget previewWidget;

    public ParticleOverviewScreen(GuiSpellBook previousScreen, int slot, InteractionHand stackHand) {
        super(stackHand);
        this.previousScreen = previousScreen;
        this.selectedSpellSlot = slot;
        this.timelineMap = caster.getParticles(slot).mutable();
        selectedTimeline = findTimelineFromSlot();
    }

    public List<IParticleTimelineType<?>> findTimelineFromSlot() {

        List<IParticleTimelineType<?>> timelines = new ArrayList<>();
        for (AbstractSpellPart spellPart : caster.getSpell(selectedSpellSlot).recipe()) {
            IParticleTimelineType<?> timeline = null;
            var allTimelines = ParticleTimelineRegistry.PARTICLE_TIMELINE_REGISTRY.entrySet();
            for (var entry : allTimelines) {
                if (entry.getValue().getSpellPart() == spellPart) {
                    timeline = entry.getValue();
                }
            }
            if (timeline != null) {
                timelines.add(timeline);
            }
        }
        if (timelines.isEmpty()) {
            timelines.add(ParticleTimelineRegistry.PROJECTILE_TIMELINE.get());
        }
        return timelines;
    }

    public void initSlotChange() {
        this.timelineMap = caster.getParticles(selectedSpellSlot).mutable();
        selectedTimeline = findTimelineFromSlot();
        updatePreviewButton();
        clearRightPage();
        rowOffset = 0;
        previewWidget.dismiss();
        for (PropWidgetList propWidgetList : propLists) {
            propWidgetList.resetSelected();
        }
        initPropListForTimeline();
        initLeftSideButtons();
    }

    public void initPropListForTimeline() {
        clearProplistWidgets();
        propLists = new ArrayList<>();
        for (IParticleTimelineType<?> timelineType : selectedTimeline) {
            PropWidgetList widgetList = new PropWidgetList(timelineType, bookLeft + LEFT_PAGE_OFFSET + 13, bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, this::onPropertySelected, this::onDependenciesChanged, null);
            propLists.add(widgetList);
        }
    }

    @Override
    public void init() {
        super.init();
        if (!propLists.isEmpty()) {
            List<PropWidgetList> oldLists = new ArrayList<>(propLists);
            propLists.clear();
            for (PropWidgetList oldList : oldLists) {
                PropWidgetList widgetList = new PropWidgetList(oldList.forTimeline, bookLeft + LEFT_PAGE_OFFSET + 13, bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, this::onPropertySelected, this::onDependenciesChanged, oldList);
                propLists.add(widgetList);
            }
        } else {
            initPropListForTimeline();
        }

        upButton = new GuiImageButton(bookLeft + LEFT_PAGE_OFFSET + 87, bookBottom - 30, DocAssets.BUTTON_UP, (button) -> {
            rowOffset = Math.max(rowOffset - 1, 0);
            layoutLeftPage();
        }).withHoverImage(DocAssets.BUTTON_UP_HOVER);
        downButton = new GuiImageButton(bookLeft + LEFT_PAGE_OFFSET + 103, bookBottom - 30, DocAssets.BUTTON_DOWN, (button) -> {
            rowOffset = rowOffset + 1;
            layoutLeftPage();
        }).withHoverImage(DocAssets.BUTTON_DOWN_HOVER);
        addRenderableWidget(new ClearButton(bookRight - 129, bookBottom - 13, Component.translatable("ars_nouveau.spell_book_gui.clear"), this::reset));

        addRenderableWidget(upButton);
        addRenderableWidget(downButton);

        addBackButton(previousScreen, b -> {
            if (this.previousScreen instanceof GuiSpellBook guiSpellBook) {
                guiSpellBook.selectedSpellSlot = selectedSpellSlot;
                guiSpellBook.onBookstackUpdated(bookStack);
            }
        });
        addSaveButton((b) -> {
            updateScreenHash(timelineMap.immutable(), selectedSpellSlot, caster.getSpell(selectedSpellSlot));
            Networking.sendToServer(new PacketUpdateParticleTimeline(selectedSpellSlot, timelineMap.immutable(), this.hand == InteractionHand.MAIN_HAND));
        });

        initLeftSideButtons();

        SelectableButton expandButton = new SelectableButton(bookLeft + LEFT_PAGE_OFFSET + 12, bookBottom - 30, DocAssets.EXPAND_ICON, DocAssets.COLLAPSE_ICON, (button) -> {
            allExpanded = !allExpanded;
            if (button instanceof SelectableButton selectableButton) {
                selectableButton.isSelected = allExpanded;
            }
            layoutLeftPage();
        });
        expandButton.withTooltip(Component.translatable("ars_nouveau.expand_button"));
        expandButton.isSelected = allExpanded;

        addRenderableWidget(expandButton);

        if (previewWidget == null) {
            previewWidget = new ParticlePreviewWidget(bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, ONE_PAGE_HEIGHT);
        } else {
            previewWidget.x = bookLeft + RIGHT_PAGE_OFFSET;
            previewWidget.y = bookTop + PAGE_TOP_OFFSET;
        }

        previewButton = new GuiImageButton(bookLeft + LEFT_PAGE_OFFSET + 35, bookBottom - 31, DocAssets.TEST_ICON, (b) -> {
            if (selectedTimeline != null) {
                List<IParticleTimeline<?>> timelinesToPreview = selectedTimeline.stream()
                        .filter(IParticleTimelineType::hasPreview)
                        .map(timelineMap::getOrCreate)
                        .collect(Collectors.toList());
                previewWidget.playAll(timelinesToPreview);
            }
        }).withTooltip(Component.translatable("ars_nouveau.preview"));
        addRenderableWidget(previewButton);
        updatePreviewButton();

        initSpellSlots((slotButton) -> {
            initSlotChange();
            rebuildWidgets();
        });

        for (PropWidgetList propList : propLists) {
            PropertyButton lastClickedButton = propList.getSelectedButton();
            if (lastClickedButton != null) {
                lastClickedButton.onPress();
                break;
            }
        }
    }

    public void reset(Button button) {
        if (selectedTimeline == null)
            return;
        for (IParticleTimelineType<?> timeline : selectedTimeline) {
            timelineMap.remove(timeline);
        }
        initLeftSideButtons();
    }

    public void onDependenciesChanged(PropertyButton propButton) {
        initLeftSideButtons();
    }

    public static void updateScreenHash(TimelineMap timelineMap, int slot, Spell spell) {
        ParticleOverviewScreen.lastOpenedHash = Objects.hash(timelineMap, slot, spell.recipe());
    }

    public static void openScreen(GuiSpellBook parentScreen, ItemStack stack, int slot, InteractionHand stackHand) {
        AbstractCaster<?> caster = SpellCasterRegistry.from(stack);
        if (ParticleOverviewScreen.lastOpenedHash != Objects.hash(caster.getParticles(slot), slot, caster.getSpell(slot).recipe()) || ParticleOverviewScreen.lastScreen == null) {
            updateScreenHash(caster.getParticles(slot), slot, caster.getSpell(slot));
            Minecraft.getInstance().setScreen(new ParticleOverviewScreen(parentScreen, slot, stackHand));
        } else {
            ParticleOverviewScreen screen = ParticleOverviewScreen.lastScreen;
            screen.previousScreen = parentScreen;
            screen.hand = stackHand;
            screen.onBookstackUpdated(stack);
            if (screen.selectedSpellSlot != slot) {
                screen.selectedSpellSlot = slot;
                screen.initSlotChange();
            }
            parentScreen.selectedSpellSlot = slot;
            Minecraft.getInstance().setScreen(screen);
        }
    }


    @Override
    public void onClose() {
        super.onClose();
        ParticleOverviewScreen.lastScreen = this;
    }

    @Override
    public void removed() {
        super.removed();
        ParticleOverviewScreen.lastScreen = this;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {

        if (propertyWidgetProvider != null && GuiHelpers.isMouseInRelativeRange((int) pMouseX, (int) pMouseY, propertyWidgetProvider.x,
                propertyWidgetProvider.y, propertyWidgetProvider.width, propertyWidgetProvider.height)) {
            if (propertyWidgetProvider.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY)) {
                return true;
            }
        }
        SoundManager manager = Minecraft.getInstance().getSoundManager();
        if (pScrollY < 0 && hasMoreElements) {
            rowOffset = rowOffset + 1;
            layoutLeftPage();
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        } else if (pScrollY > 0 && hasPreviousElements) {
            rowOffset = rowOffset - 1;
            layoutLeftPage();
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }

        return true;
    }

    public void clearProplistWidgets() {
        for (PropWidgetList propWidgetList : propLists) {
            removeWidget(propWidgetList.timelineButton);
            clearList(propWidgetList.allButtons);
        }
    }

    public void initLeftSideButtons() {
        clearProplistWidgets();
        for (PropWidgetList propWidgetList : propLists) {
            propWidgetList.init(timelineMap);
            addRenderableWidget(propWidgetList.timelineButton);
            for (AbstractWidget widget : propWidgetList.allButtons) {
                addRenderableWidget(widget);
            }
        }
        layoutLeftPage();

    }

    public void layoutLeftPage() {
        List<AbstractWidget> allButtons = new ArrayList<>();
        for (PropWidgetList propList : propLists) {
            allButtons.add(propList.timelineButton);
            allButtons.addAll(propList.allButtons);
        }
        List<AbstractWidget> visibleWidgets = allButtons.stream().filter(widget -> {
            if (!(widget instanceof PropertyButton propertyButton)) {
                return true;
            }
            return allExpanded || propertyButton.isExpanded() || propertyButton.nestLevel == 0;
        }).collect(Collectors.toList());

        if (rowOffset >= visibleWidgets.size()) {
            rowOffset = 0;
        }

        for (AbstractWidget widget : allButtons) {
            widget.active = false;
            widget.visible = false;
            if (widget instanceof PropertyButton button) {
                button.showMarkers = !allExpanded;
            }
        }
        List<AbstractWidget> slicedWidgets = visibleWidgets.subList(rowOffset, visibleWidgets.size());
        int LEFT_PAGE_SLICE = 9;
        for (int i = 0; i < Math.min(slicedWidgets.size(), LEFT_PAGE_SLICE); i++) {
            AbstractWidget widget = slicedWidgets.get(i);
            widget.y = bookTop + 36 + 13 * i;
            widget.active = true;
            widget.visible = true;
        }
        hasMoreElements = rowOffset + LEFT_PAGE_SLICE < visibleWidgets.size();
        hasPreviousElements = rowOffset > 0;

        upButton.visible = hasPreviousElements;
        upButton.active = hasPreviousElements;
        downButton.active = hasMoreElements;
        downButton.visible = hasMoreElements;
    }

    public void onPropertySelected(PropertyButton propertyButton) {
        previewWidget.dismiss();
        clearRightPage();
        propertyWidgetProvider = propertyButton.property.buildWidgets(bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, ONE_PAGE_HEIGHT);

        List<AbstractWidget> propertyWidgets = new ArrayList<>();
        propertyWidgetProvider.addWidgets(propertyWidgets);

        for (AbstractWidget widget : propertyWidgets) {
            addRightPageWidget(widget);
        }
        layoutLeftPage();
    }

    private void updatePreviewButton() {
        if (previewButton == null) {
            return;
        }
        boolean hasPreview = selectedTimeline != null && selectedTimeline.stream().anyMatch(IParticleTimelineType::hasPreview);
        previewButton.visible = hasPreview;
        previewButton.active = hasPreview;
    }

    private void clearRightPage() {
        clearList(rightPageWidgets);
        propertyWidgetProvider = null;
    }

    private void clearList(List<? extends AbstractWidget> list) {
        for (AbstractWidget widget : list) {
            this.removeWidget(widget);
        }
        list.clear();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        boolean previewActive = updatePreviewVisibility();
        super.render(graphics, mouseX, mouseY, partialTicks);
        previewWidget.render(graphics, mouseX, mouseY, partialTicks);
        DocClientUtils.drawHeader(Component.translatable("ars_nouveau.spell_styles"), graphics, bookLeft + LEFT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, mouseX, mouseY, partialTicks);
        if (propertyWidgetProvider != null && !previewActive) {
            propertyWidgetProvider.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    private boolean updatePreviewVisibility() {
        boolean previewActive = previewWidget.isPlaying();
        if (previewActive) {
            for (AbstractWidget widget : rightPageWidgets) {
                previewWidgetVisibility.putIfAbsent(widget, widget.visible);
                widget.visible = false;
            }
        } else {
            for (AbstractWidget widget : rightPageWidgets) {
                Boolean visible = previewWidgetVisibility.get(widget);
                if (visible != null) {
                    widget.visible = visible;
                }
            }
            previewWidgetVisibility.clear();
        }
        return previewActive;
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        if (propertyWidgetProvider != null && !previewWidget.isPlaying()) {
            propertyWidgetProvider.renderBg(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (propertyWidgetProvider != null) {
            propertyWidgetProvider.tick();
        }
        previewWidget.tick();
    }

    public void addRightPageWidget(AbstractWidget widget) {
        rightPageWidgets.add(widget);
        addRenderableWidget(widget);
    }
}
