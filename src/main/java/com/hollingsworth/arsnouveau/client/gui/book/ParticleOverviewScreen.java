package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.IConfigurableParticleType;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.IParticleProperty;
import com.hollingsworth.arsnouveau.api.particle.timelines.IParticleTimelineType;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineOption;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.buttons.DropdownParticleButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.ParticleTimelineButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateParticleTimeline;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ParticleOverviewScreen extends BaseBook {
    int slot;
    InteractionHand stackHand;
    TimelineMap timeline;

    public Map.Entry<AbstractSpellPart, Supplier<IParticleTimelineType<?>>> selectedTimeline = null;

    List<AbstractWidget> rightPageWidgets = new ArrayList<>();
    List<AbstractWidget> leftPageWidgets = new ArrayList<>();

    ParticleConfigWidgetProvider propertyWidgetProvider;

    public ParticleOverviewScreen(TimelineMap particleTimeline, int slot, InteractionHand stackHand) {
        this.slot = slot;
        this.stackHand = stackHand;
        this.timeline = particleTimeline;
        selectedTimeline = ParticleTimelineRegistry.PARTICLE_TIMELINE_MAP.map.entrySet().iterator().next();
    }

    @Override
    public void init() {
        super.init();
        AbstractSpellPart selectedPart = selectedTimeline.getKey();
        addRenderableWidget(new GuiImageButton(bookLeft + 25, bookBottom - 30, 0, 0, 37, 12, 37, 12, "textures/gui/save_icon.png", this::onCreate));

        addRenderableWidget(new ParticleTimelineButton(bookLeft + LEFT_PAGE_OFFSET, bookTop + 36, (button) -> {
            addTimelinePage();
        }, Component.translatable(selectedPart.getLocaleName()), selectedPart.glyphItem.getDefaultInstance()));
        addTimelinePage();
        addSelectedTimelineOptions();
    }

    public void addConfiguredParticleOptions(TimelineOption timelineOption){
        clearRightPage();
        int entryCount = 0;
        for(IConfigurableParticleType<?> type : timelineOption.options()){
            var widget = new GuiImageButton(bookLeft + RIGHT_PAGE_OFFSET + 10 + entryCount * 20, bookTop + 20, 16, 16, type.getIconLocation(), (button) -> {
                System.out.println(type);
            }).withTooltip(type.getName());
            rightPageWidgets.add(widget);
            addRenderableWidget(widget);
            entryCount++;
        }
    }

    public void addPropertyWidgets(IParticleProperty property){
        clearRightPage();
        propertyWidgetProvider = property.buildWidgets(bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, ONE_PAGE_HEIGHT);
        List<AbstractWidget> propertyWidgets = new ArrayList<>();
        propertyWidgetProvider.addWidgets(propertyWidgets);

        for(AbstractWidget widget : propertyWidgets){
            rightPageWidgets.add(widget);
            addRenderableWidget(widget);
        }
    }

    public void addSelectedTimelineOptions(){
        clearList(leftPageWidgets);
        clearRightPage();
        var configurableParticles = timeline.get(selectedTimeline.getValue().get()).getTimelineOptions();
        int propertyOffset = 0;
        for(TimelineOption timelineOption : configurableParticles){
            var type = timelineOption.getSelected().get().getType();
            Component name = Component.literal(timelineOption.name().getString() + ": " + timelineOption.getSelected().get().getType().getName().getString());
            leftPageWidgets.add(addRenderableWidget(new DropdownParticleButton(bookLeft + LEFT_PAGE_OFFSET + 13, bookTop + 52 + 16 * (propertyOffset), name, DocAssets.NESTED_ENTRY_BUTTON, type.getIconLocation(), (button) -> {
                addConfiguredParticleOptions(timelineOption);
            })));

            propertyOffset++;

            for(IParticleProperty property : timelineOption.getSelected().get().getProperties()){
                leftPageWidgets.add(addRenderableWidget(new DropdownParticleButton(bookLeft + LEFT_PAGE_OFFSET + 26, bookTop + 52 + 16 * (propertyOffset), property.getName(), DocAssets.DOUBLE_NESTED_ENTRY_BUTTON, property.getIconLocation(), (button) -> {
                    addPropertyWidgets(property);
                })));
                propertyOffset++;
            }
        }
    }

    public void addTimelinePage(){
        clearRightPage();
        int entryCount = 0;
        for(var entry : ParticleTimelineRegistry.PARTICLE_TIMELINE_MAP.map.entrySet()){
            var widget = new GlyphButton(bookLeft + RIGHT_PAGE_OFFSET + 10 + entryCount * 20, bookTop + 20, entry.getKey(), (button) -> {
                selectedTimeline = entry;
            });
            rightPageWidgets.add(widget);
            addRenderableWidget(widget);
            entryCount++;
        }
    }

    private void clearRightPage(){
        clearList(rightPageWidgets);
        propertyWidgetProvider = null;
    }

    private void clearList(List<AbstractWidget> list){
        for(AbstractWidget widget : list){
            this.removeWidget(widget);
        }
        list.clear();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        DocClientUtils.drawHeader(Component.translatable("ars_nouveau.spell_styles"), graphics, bookLeft + LEFT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, mouseX, mouseY, partialTicks);
        if(propertyWidgetProvider != null){
            propertyWidgetProvider.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    public void onCreate(Button button){
        Networking.sendToServer(new PacketUpdateParticleTimeline(slot, timeline, this.stackHand == InteractionHand.MAIN_HAND));
    }
}
