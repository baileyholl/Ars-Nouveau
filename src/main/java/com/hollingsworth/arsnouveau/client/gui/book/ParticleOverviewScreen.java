package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleMotion;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.Property;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.SubProperty;
import com.hollingsworth.arsnouveau.api.particle.timelines.IParticleTimelineType;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineOption;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.HeaderWidget;
import com.hollingsworth.arsnouveau.client.gui.buttons.DropdownParticleButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.PropertyButton;
import com.hollingsworth.arsnouveau.client.gui.documentation.DocEntryButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateParticleTimeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;

import java.util.ArrayList;
import java.util.List;

public class ParticleOverviewScreen extends BaseBook {
    int slot;
    InteractionHand stackHand;
    TimelineMap.MutableTimelineMap timeline;

    public IParticleTimelineType<?> selectedTimeline = null;


    List<AbstractWidget> rightPageWidgets = new ArrayList<>();
    List<AbstractWidget> leftPageWidgets = new ArrayList<>();

    ParticleConfigWidgetProvider propertyWidgetProvider;
    DocEntryButton timelineButton;
    AbstractCaster<?> caster;

    int rowOffset = 0;
    boolean hasMoreElements = false;
    boolean hasPreviousElements = false;

    public ParticleOverviewScreen(AbstractCaster<?> caster,  int slot, InteractionHand stackHand) {
        this.slot = slot;
        this.stackHand = stackHand;
        this.caster = caster;
        this.timeline = caster.getParticles().mutable();

        for(AbstractSpellPart spellPart : caster.getSpell(slot).recipe()){
            var allTimelines = ParticleTimelineRegistry.PARTICLE_TIMELINE_REGISTRY.entrySet();
            for (var entry : allTimelines) {
                if (entry.getValue().getSpellPart() == spellPart) {
                    selectedTimeline = entry.getValue();
                    break;
                }
            }
        }
        if(selectedTimeline == null){
            selectedTimeline = ParticleTimelineRegistry.PROJECTILE_TIMELINE.get();
        }
    }

    @Override
    public void init() {
        super.init();
        addSaveButton((b) -> Networking.sendToServer(new PacketUpdateParticleTimeline(slot, timeline.immutable(), this.stackHand == InteractionHand.MAIN_HAND)));
        timelineButton = addRenderableWidget(new DocEntryButton(bookLeft + LEFT_PAGE_OFFSET, bookTop + 36, selectedTimeline.getSpellPart().glyphItem.getDefaultInstance(), Component.translatable(selectedTimeline.getSpellPart().getLocaleName()), (button) -> {
            addTimelinePage();
        }));
        addTimelinePage();
        initLeftSideButtons();
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        SoundManager manager = Minecraft.getInstance().getSoundManager();
        if (pScrollY < 0 && hasMoreElements) {
            rowOffset = rowOffset + 1;
            initLeftSideButtons();
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        } else if (pScrollY > 0 && hasPreviousElements) {
            rowOffset = rowOffset - 1;
            initLeftSideButtons();
            manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }

        return true;
    }


    public void addParticleMotionOptions(TimelineOption timelineOption) {
        clearRightPage();
        int entryCount = 0;
        addRightPageWidget(new HeaderWidget(bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, 20, timelineOption.name()));
        for (IParticleMotionType<?> type : timelineOption.options()) {
            var widget = new GuiImageButton(bookLeft + RIGHT_PAGE_OFFSET + 10 + entryCount * 20, bookTop + 40, 14, 14, type.getIconLocation(), (button) -> {
                timelineOption.entry().setMotion(type.create());
                initLeftSideButtons();

            }).withTooltip(type.getName());
            addRightPageWidget(widget);
            entryCount++;
        }
    }

    public void initLeftSideButtons() {
        clearList(leftPageWidgets);
        List<TimelineOption> configurableParticles = timeline.getOrCreate(selectedTimeline).getTimelineOptions();
        int propertyOffset = 0;
        List<AbstractWidget> widgets = new ArrayList<>();
        for (int i = 0; i < configurableParticles.size(); i++) {
            TimelineOption timelineOption = configurableParticles.get(i);
            TimelineEntryData entryData = timelineOption.entry();
            ParticleMotion configuration = entryData.motion();
            IParticleMotionType<?> motionType = configuration.getType();
            Component name = Component.literal(timelineOption.name().getString() + ": " + motionType.getName().getString());
            DropdownParticleButton dropdownParticleButton = new DropdownParticleButton(bookLeft + LEFT_PAGE_OFFSET + 13, bookTop + 51 + 15 * (propertyOffset), name, DocAssets.NESTED_ENTRY_BUTTON, motionType.getIconLocation(), (button) -> {
                addParticleMotionOptions(timelineOption);
            });
            widgets.add(dropdownParticleButton);
            propertyOffset++;
            List<BaseProperty> allProps = new ArrayList<>();
            for (Property property : timelineOption.properties()) {
                property.setChangedListener(this::initLeftSideButtons);
                allProps.add(property);
                List<SubProperty> subProperties = property.subProperties();
                allProps.addAll(subProperties);
            }
            for (Property property : configuration.getProperties()) {
                property.setChangedListener(this::initLeftSideButtons);
                allProps.add(property);
                List<SubProperty> subProperties = property.subProperties();
                allProps.addAll(subProperties);
            }
            for (BaseProperty property : allProps) {
                PropertyButton propertyButton = buildPropertyButton(property, propertyOffset);
                widgets.add(propertyButton);
                propertyOffset++;
            }
        }

        if(rowOffset >= widgets.size()){
            rowOffset = 0;
        }
        List<AbstractWidget> slicedWidgets = widgets.subList(rowOffset, widgets.size());
        int LEFT_PAGE_SLICE = 7;
        for (int i = 0; i < Math.min(slicedWidgets.size(), LEFT_PAGE_SLICE); i++) {
            AbstractWidget widget = slicedWidgets.get(i);
            widget.y = bookTop + 51 + 15 * i;
            addLeftPageWidget(widget);
        }
        hasMoreElements = rowOffset + LEFT_PAGE_SLICE < widgets.size();
        hasPreviousElements = rowOffset > 0;
        if(hasPreviousElements){
            addLeftPageWidget(new GuiImageButton(bookLeft + LEFT_PAGE_OFFSET + 80, bookBottom - 30, DocAssets.BUTTON_UP, (button) -> {
                rowOffset = Math.max(rowOffset - 1, 0);
                initLeftSideButtons();
            }).withHoverImage(DocAssets.BUTTON_UP_HOVER));
        }

        if(hasMoreElements){
            addLeftPageWidget(new GuiImageButton(bookLeft + LEFT_PAGE_OFFSET + 100, bookBottom - 30, DocAssets.BUTTON_DOWN, (button) -> {
                rowOffset = rowOffset + 1;
                initLeftSideButtons();
            }).withHoverImage(DocAssets.BUTTON_DOWN_HOVER));
        }
    }

    public PropertyButton buildPropertyButton(BaseProperty property, int yOffset) {
        boolean isSubProperty = property instanceof SubProperty;
        var widgetProvider = property.buildWidgets(bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, ONE_PAGE_HEIGHT);
        return new PropertyButton(bookLeft + LEFT_PAGE_OFFSET + 26 + (isSubProperty ? 13 : 0), bookTop + 51 + 15 * (yOffset), isSubProperty ? DocAssets.TRIPLE_NESTED_ENTRY_BUTTON : DocAssets.DOUBLE_NESTED_ENTRY_BUTTON, widgetProvider, (button) -> {
            clearRightPage();
            propertyWidgetProvider = widgetProvider;
            List<AbstractWidget> propertyWidgets = new ArrayList<>();
            propertyWidgetProvider.addWidgets(propertyWidgets);

            for (AbstractWidget widget : propertyWidgets) {
                addRightPageWidget(widget);
            }
        });
    }

    public void addTimelinePage() {
        clearRightPage();
        rightPageWidgets.add(addRenderableWidget(new HeaderWidget(bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, 20, Component.translatable("ars_nouveau.particle_timelines"))));
        var timelineList = new ArrayList<>(ParticleTimelineRegistry.PARTICLE_TIMELINE_REGISTRY.entrySet());
        for (int i = 0; i < timelineList.size(); i++) {
            var entry = timelineList.get(i);
            var widget = new GlyphButton(bookLeft + RIGHT_PAGE_OFFSET + 2 + 20 * (i % 7), bookTop + 40 + 20*(i/7), entry.getValue().getSpellPart(), (button) -> {
                selectedTimeline = entry.getValue();
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

    private void clearList(List<AbstractWidget> list) {
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

    public void addLeftPageWidget(AbstractWidget widget) {
        leftPageWidgets.add(widget);
        addRenderableWidget(widget);
    }

    public void addRightPageWidget(AbstractWidget widget) {
        rightPageWidgets.add(widget);
        addRenderableWidget(widget);
    }
}
