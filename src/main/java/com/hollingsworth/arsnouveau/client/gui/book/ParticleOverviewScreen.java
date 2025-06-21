package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
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

public class ParticleOverviewScreen extends SpellSlottedScreen {
    TimelineMap.MutableTimelineMap timelineMap;

    public IParticleTimelineType<?> selectedTimeline = null;


    List<AbstractWidget> rightPageWidgets = new ArrayList<>();
    List<AbstractWidget> leftPageWidgets = new ArrayList<>();

    ParticleConfigWidgetProvider propertyWidgetProvider;
    DocEntryButton timelineButton;
    int rowOffset = 0;
    boolean hasMoreElements = false;
    boolean hasPreviousElements = false;
    public static IParticleTimelineType<?> LAST_SELECTED_PART = null;
    public static int lastOpenedHash;
    public static ParticleOverviewScreen lastScreen;
    BaseProperty selectedProperty;

    SelectableButton currentlySelectedButton;
    GuiSpellBook previousScreen;
    GuiImageButton upButton;
    GuiImageButton downButton;

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
        addBackButton(previousScreen, b -> {
            if (this.previousScreen instanceof GuiSpellBook guiSpellBook) {
                guiSpellBook.selectedSpellSlot = selectedSpellSlot;
                guiSpellBook.onBookstackUpdated(bookStack);
            }
        });
        addSaveButton((b) -> {
            int hash = timelineMap.immutable().hashCode();
            ParticleOverviewScreen.lastOpenedHash = hash;
            Networking.sendToServer(new PacketUpdateParticleTimeline(selectedSpellSlot, timelineMap.immutable(), this.hand == InteractionHand.MAIN_HAND));
        });
        timelineButton = addRenderableWidget(new DocEntryButton(bookLeft + LEFT_PAGE_OFFSET, bookTop + 36, selectedTimeline.getSpellPart().glyphItem.getDefaultInstance(), Component.translatable(selectedTimeline.getSpellPart().getLocaleName()), (b) -> onTimelineSelectorHit()));
        if (currentlySelectedButton == null) {
            setSelectedButton(timelineButton);
        }
        if (selectedProperty == null) {
            addTimelineSelectionWidgets();
        } else {
            if (propertyWidgetProvider != null) {
                List<AbstractWidget> propertyWidgets = new ArrayList<>();
                propertyWidgetProvider.x = bookLeft + RIGHT_PAGE_OFFSET;
                propertyWidgetProvider.y = bookTop + PAGE_TOP_OFFSET;
                propertyWidgetProvider.addWidgets(propertyWidgets);

                for (AbstractWidget widget : propertyWidgets) {
                    addRightPageWidget(widget);
                }
            } else {
                onPropertySelected(selectedProperty);
            }
        }
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

        initLeftSideButtons();

        initSpellSlots((slotButton) -> {
            initSlotChange();
            rebuildWidgets();
        });
    }

    public void onTimelineSelectorHit() {
        addTimelineSelectionWidgets();
        setSelectedButton(timelineButton);
        selectedProperty = null;
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

    public void setSelectedButton(SelectableButton selectedButton) {
        if (currentlySelectedButton != null) {
            currentlySelectedButton.isSelected = false;
        }
        currentlySelectedButton = selectedButton;
        currentlySelectedButton.isSelected = true;
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
        clearList(leftPageWidgets);

        IParticleTimeline<?> timeline = timelineMap.getOrCreate(selectedTimeline);
        List<PropertyButton> propertyButtons = getPropButtons(timeline.getProperties(), 0);
        for (AbstractWidget widget : propertyButtons) {
            addLeftPageWidget(widget);
        }

        layoutLeftPage();
    }

    public void layoutLeftPage() {
        if (rowOffset >= leftPageWidgets.size()) {
            rowOffset = 0;
        }
        int propIndex = 0;
        for (AbstractWidget widget : leftPageWidgets) {
            widget.active = false;
            widget.visible = false;
//            widget.y = -1000;
            if (widget instanceof PropertyButton button) {
                button.index = propIndex;
                propIndex++;
            }
        }
        List<AbstractWidget> slicedWidgets = leftPageWidgets.subList(rowOffset, leftPageWidgets.size());
        int LEFT_PAGE_SLICE = 7;
        for (int i = 0; i < Math.min(slicedWidgets.size(), LEFT_PAGE_SLICE); i++) {
            AbstractWidget widget = slicedWidgets.get(i);
            widget.y = bookTop + 51 + 15 * i;
            widget.active = true;
            widget.visible = true;
        }
        hasMoreElements = rowOffset + LEFT_PAGE_SLICE < leftPageWidgets.size();
        hasPreviousElements = rowOffset > 0;

        upButton.visible = hasPreviousElements;
        upButton.active = hasPreviousElements;
        downButton.active = hasMoreElements;
        downButton.visible = hasMoreElements;
    }

    public List<PropertyButton> getPropButtons(List<BaseProperty<?>> props, int depth) {
        List<PropertyButton> buttons = new ArrayList<>();
        if (depth > 3) {
            return buttons;
        }
        for (BaseProperty<?> property : props) {
            PropertyButton propertyButton = buildPropertyButton(property, depth);
            buttons.add(propertyButton);
            List<PropertyButton> childrenButtons = getPropButtons(property.subProperties(), depth + 1);
            propertyButton.children = new ArrayList<>(childrenButtons);
            buttons.addAll(childrenButtons);
        }

        for (int i = 0; i < buttons.size(); i++) {
            PropertyButton propButton = buttons.get(i);
            int finalI = i;
            propButton.index = i;
            propButton.property.setChangedListener(() -> {
                List<AbstractWidget> toRemove = new ArrayList<>();
                for (PropertyButton widget : propButton.children) {
                    toRemove.add(leftPageWidgets.get(widget.index));
                }
                for (AbstractWidget widget : toRemove) {
                    leftPageWidgets.remove(widget);
                    removeWidget(widget);
                }
                propButton.children = getPropButtons(propButton.property.subProperties(), propButton.nestLevel + 1);
                if (!propButton.children.isEmpty()) {
                    leftPageWidgets.addAll(propButton.index + 1, propButton.children);
                    for (PropertyButton button : propButton.children) {
                        addRenderableWidget(button);
                    }
                }
                layoutLeftPage();
            });
        }
        return buttons;
    }

    public PropertyButton buildPropertyButton(BaseProperty<?> property, int nestLevel) {
        DocAssets.BlitInfo texture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON;
        DocAssets.BlitInfo selectedTexture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON_SELECTED;
        switch (nestLevel) {
            case 0 -> {
                texture = DocAssets.NESTED_ENTRY_BUTTON;
                selectedTexture = DocAssets.NESTED_ENTRY_BUTTON_SELECTED;
            }
            case 1 -> {
                texture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON;
                selectedTexture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON_SELECTED;
            }
            case 2 -> {
                texture = DocAssets.TRIPLE_NESTED_ENTRY_BUTTON;
                selectedTexture = DocAssets.TRIPLE_NESTED_ENTRY_BUTTON_SELECTED;
            }
            default -> {
            }
        }
        var widgetProvider = property.buildWidgets(bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, ONE_PAGE_HEIGHT);
        PropertyButton propButton = new PropertyButton(bookLeft + LEFT_PAGE_OFFSET + 13 + nestLevel * 13, 0, texture, selectedTexture, property, widgetProvider, nestLevel, (button) -> {
            onPropertySelected(property);
            if (button instanceof PropertyButton propertyButton) {
                propertyButton.widgetProvider = propertyWidgetProvider;
                setSelectedButton(propertyButton);
            }
            selectedProperty = property;
        });
        return propButton;
    }

    public void onPropertySelected(BaseProperty property) {
        clearRightPage();
        propertyWidgetProvider = property.buildWidgets(bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, ONE_PAGE_HEIGHT);

        List<AbstractWidget> propertyWidgets = new ArrayList<>();
        propertyWidgetProvider.addWidgets(propertyWidgets);

        for (AbstractWidget widget : propertyWidgets) {
            addRightPageWidget(widget);
        }
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

    public void addLeftPageWidget(AbstractWidget widget) {
        leftPageWidgets.add(widget);
        addRenderableWidget(widget);
    }

    public void addRightPageWidget(AbstractWidget widget) {
        rightPageWidgets.add(widget);
        addRenderableWidget(widget);
    }
}
