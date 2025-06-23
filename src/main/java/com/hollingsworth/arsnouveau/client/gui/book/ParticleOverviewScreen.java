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
import com.hollingsworth.arsnouveau.client.gui.HeaderWidget;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.PropertyButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectableButton;
import com.hollingsworth.arsnouveau.client.gui.documentation.DocEntryButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateParticleTimeline;
import com.hollingsworth.arsnouveau.setup.registry.CreativeTabRegistry;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParticleOverviewScreen extends SpellSlottedScreen {
    TimelineMap.MutableTimelineMap timelineMap;

    public IParticleTimelineType<?> selectedTimeline = null;


    List<AbstractWidget> rightPageWidgets = new ArrayList<>();

    ParticleConfigWidgetProvider propertyWidgetProvider;
    DocEntryButton timelineButton;
    int rowOffset = 0;
    boolean hasMoreElements = false;
    boolean hasPreviousElements = false;
    public static IParticleTimelineType<?> LAST_SELECTED_PART = null;
    public static int lastOpenedHash;
    public static ParticleOverviewScreen lastScreen;

    GuiSpellBook previousScreen;
    GuiImageButton upButton;
    GuiImageButton downButton;
    boolean allExpanded = false;
    PropWidgetList propWidgetList;

    public ParticleOverviewScreen(GuiSpellBook previousScreen, int slot, InteractionHand stackHand) {
        super(stackHand);
        this.previousScreen = previousScreen;
        this.selectedSpellSlot = slot;
        this.timelineMap = caster.getParticles(slot).mutable();
        selectedTimeline = LAST_SELECTED_PART == null ? findTimelineFromSlot() : LAST_SELECTED_PART;
        LAST_SELECTED_PART = selectedTimeline;
    }

    public IParticleTimelineType<?> findTimelineFromSlot() {
        IParticleTimelineType<?> timeline = null;
        for (AbstractSpellPart spellPart : caster.getSpell(selectedSpellSlot).recipe()) {
            var allTimelines = ParticleTimelineRegistry.PARTICLE_TIMELINE_REGISTRY.entrySet();
            for (var entry : allTimelines) {
                if (entry.getValue().getSpellPart() == spellPart) {
                    timeline = entry.getValue();
                }
            }
            if (timeline != null) {
                break;
            }
        }
        if (timeline == null) {
            timeline = ParticleTimelineRegistry.PROJECTILE_TIMELINE.get();
        }
        return timeline;
    }

    public void initSlotChange() {
        this.timelineMap = caster.getParticles(selectedSpellSlot).mutable();
        selectedTimeline = findTimelineFromSlot();
        LAST_SELECTED_PART = selectedTimeline;
        rowOffset = 0;
        onTimelineSelectorHit();
    }

    @Override
    public void init() {
        super.init();

        propWidgetList = new PropWidgetList(bookLeft + LEFT_PAGE_OFFSET + 13, bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, this::onPropertySelected, this::onDependenciesChanged, propWidgetList);

        upButton = new GuiImageButton(bookLeft + LEFT_PAGE_OFFSET + 87, bookBottom - 30, DocAssets.BUTTON_UP, (button) -> {
            rowOffset = Math.max(rowOffset - 1, 0);
            layoutLeftPage();
        }).withHoverImage(DocAssets.BUTTON_UP_HOVER);
        downButton = new GuiImageButton(bookLeft + LEFT_PAGE_OFFSET + 103, bookBottom - 30, DocAssets.BUTTON_DOWN, (button) -> {
            rowOffset = rowOffset + 1;
            layoutLeftPage();
        }).withHoverImage(DocAssets.BUTTON_DOWN_HOVER);

        addRenderableWidget(upButton);
        addRenderableWidget(downButton);

        addBackButton(previousScreen, b -> {
            if (this.previousScreen instanceof GuiSpellBook guiSpellBook) {
                guiSpellBook.selectedSpellSlot = selectedSpellSlot;
                guiSpellBook.onBookstackUpdated(bookStack);
            }
        });
        addSaveButton((b) -> {
            ParticleOverviewScreen.lastOpenedHash = timelineMap.immutable().hashCode();
            Networking.sendToServer(new PacketUpdateParticleTimeline(selectedSpellSlot, timelineMap.immutable(), this.hand == InteractionHand.MAIN_HAND));
        });
        timelineButton = addRenderableWidget(new DocEntryButton(bookLeft + LEFT_PAGE_OFFSET, bookTop + 36, selectedTimeline.getSpellPart().glyphItem.getDefaultInstance(), Component.translatable(selectedTimeline.getSpellPart().getLocaleName()), (b) -> onTimelineSelectorHit()));

        timelineButton.isSelected = true;

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


        initSpellSlots((slotButton) -> {
            initSlotChange();
            rebuildWidgets();
        });
        PropertyButton lastClickedButton = propWidgetList.getSelectedButton();
        if (lastClickedButton != null) {
            lastClickedButton.onPress();
        } else {
            addTimelineSelectionWidgets();
        }
    }

    public void onDependenciesChanged(PropertyButton propButton) {
        initLeftSideButtons();
    }

    public void onTimelineSelectorHit() {
        timelineButton.isSelected = true;
        propWidgetList.resetSelected();
        addTimelineSelectionWidgets();
    }

    public static void openScreen(GuiSpellBook parentScreen, ItemStack stack, int slot, InteractionHand stackHand) {
        AbstractCaster<?> caster = SpellCasterRegistry.from(stack);
        int hash = caster.getSpell(slot).particleTimeline().hashCode();
        if (LAST_SELECTED_PART == null || ParticleOverviewScreen.lastOpenedHash != hash || ParticleOverviewScreen.lastScreen == null) {
            LAST_SELECTED_PART = null;
            ParticleOverviewScreen.lastOpenedHash = hash;
            Minecraft.getInstance().setScreen(new ParticleOverviewScreen(parentScreen, slot, stackHand));
        } else {
            ParticleOverviewScreen screen = ParticleOverviewScreen.lastScreen;
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

    public void initLeftSideButtons() {
        clearList(propWidgetList.allButtons);

        IParticleTimeline<?> timeline = timelineMap.getOrCreate(selectedTimeline);
        propWidgetList.init(timeline.getProperties());
        for (AbstractWidget widget : propWidgetList.allButtons) {
            addRenderableWidget(widget);
        }

        layoutLeftPage();
    }

    public void layoutLeftPage() {
        List<AbstractWidget> expandedWidgets = propWidgetList.allButtons.stream().filter(widget -> {
            if (!(widget instanceof PropertyButton propertyButton)) {
                return true;
            }
            return allExpanded || propertyButton.isExpanded() || propertyButton.nestLevel == 0;
        }).collect(Collectors.toList());

        if (rowOffset >= expandedWidgets.size()) {
            rowOffset = 0;
        }

        int propIndex = 0;
        for (AbstractWidget widget : propWidgetList.allButtons) {
            widget.active = false;
            widget.visible = false;
            if (widget instanceof PropertyButton button) {
                button.index = propIndex;
                button.showMarkers = !allExpanded;
                propIndex++;
            }
        }
        List<AbstractWidget> slicedWidgets = expandedWidgets.subList(rowOffset, expandedWidgets.size());
        int LEFT_PAGE_SLICE = 7;
        for (int i = 0; i < Math.min(slicedWidgets.size(), LEFT_PAGE_SLICE); i++) {
            AbstractWidget widget = slicedWidgets.get(i);
            widget.y = bookTop + 51 + 15 * i;
            widget.active = true;
            widget.visible = true;
        }
        hasMoreElements = rowOffset + LEFT_PAGE_SLICE < expandedWidgets.size();
        hasPreviousElements = rowOffset > 0;

        upButton.visible = hasPreviousElements;
        upButton.active = hasPreviousElements;
        downButton.active = hasMoreElements;
        downButton.visible = hasMoreElements;
    }

    public void onPropertySelected(PropertyButton propertyButton) {
        clearRightPage();
        timelineButton.isSelected = false;
        propertyWidgetProvider = propertyButton.property.buildWidgets(bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, ONE_PAGE_HEIGHT);

        List<AbstractWidget> propertyWidgets = new ArrayList<>();
        propertyWidgetProvider.addWidgets(propertyWidgets);

        for (AbstractWidget widget : propertyWidgets) {
            addRightPageWidget(widget);
        }
        layoutLeftPage();
    }

    public void addTimelineSelectionWidgets() {
        clearRightPage();
        rightPageWidgets.add(addRenderableWidget(new HeaderWidget(bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, 20, Component.translatable("ars_nouveau.particle_timelines"))));
        var timelineList = new ArrayList<>(ParticleTimelineRegistry.PARTICLE_TIMELINE_REGISTRY.entrySet());
        timelineList.sort((o1, o2) -> CreativeTabRegistry.COMPARE_SPELL_TYPE_NAME.compare(o1.getValue().getSpellPart(), o2.getValue().getSpellPart()));
        for (int i = 0; i < timelineList.size(); i++) {
            var entry = timelineList.get(i);
            var widget = new GlyphButton(bookLeft + RIGHT_PAGE_OFFSET + 2 + 20 * (i % 6), bookTop + 40 + 20 * (i / 6), entry.getValue().getSpellPart(), (button) -> {
                selectedTimeline = entry.getValue();
                rowOffset = 0;
                LAST_SELECTED_PART = selectedTimeline;
                AbstractSpellPart spellPart = selectedTimeline.getSpellPart();
                timelineButton.title = Component.translatable(spellPart.getLocaleName());
                timelineButton.renderStack = (spellPart.glyphItem.getDefaultInstance());
                clearList(propWidgetList.allButtons);
                propWidgetList.resetSelected();
                initLeftSideButtons();
            });
            rightPageWidgets.add(widget);
            addRenderableWidget(widget);
        }
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
        super.render(graphics, mouseX, mouseY, partialTicks);
        DocClientUtils.drawHeader(Component.translatable("ars_nouveau.spell_styles"), graphics, bookLeft + LEFT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, mouseX, mouseY, partialTicks);
        if (propertyWidgetProvider != null) {
            propertyWidgetProvider.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (propertyWidgetProvider != null) {
            propertyWidgetProvider.tick();
        }
    }

    public void addRightPageWidget(AbstractWidget widget) {
        rightPageWidgets.add(widget);
        addRenderableWidget(widget);
    }
}
