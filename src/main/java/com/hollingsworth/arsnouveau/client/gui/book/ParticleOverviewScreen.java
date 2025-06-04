package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.Property;
import com.hollingsworth.arsnouveau.api.particle.timelines.IParticleTimeline;
import com.hollingsworth.arsnouveau.api.particle.timelines.IParticleTimelineType;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineMap;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineOption;
import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.HeaderWidget;
import com.hollingsworth.arsnouveau.client.gui.buttons.*;
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

public class ParticleOverviewScreen extends BaseBook {
    int slot;
    InteractionHand stackHand;
    TimelineMap.MutableTimelineMap timelineMap;

    public IParticleTimelineType<?> selectedTimeline = null;


    List<AbstractWidget> rightPageWidgets = new ArrayList<>();
    List<AbstractWidget> leftPageWidgets = new ArrayList<>();

    ParticleConfigWidgetProvider propertyWidgetProvider;
    DocEntryButton timelineButton;
    AbstractCaster<?> caster;

    int rowOffset = 0;
    boolean hasMoreElements = false;
    boolean hasPreviousElements = false;
    public static IParticleTimelineType<?> LAST_SELECTED_PART = null;
    public static int lastOpenedHash;
    public static ParticleOverviewScreen lastScreen;
    BaseProperty selectedProperty;
    SelectedParticleButton selectedParticleButton;
    SelectableButton currentlySelectedButton;

    public ParticleOverviewScreen(AbstractCaster<?> caster,  int slot, InteractionHand stackHand) {
        this.slot = slot;
        this.stackHand = stackHand;
        this.caster = caster;
        this.timelineMap = caster.getParticles(slot).mutable();
        if(LAST_SELECTED_PART == null) {
            for (AbstractSpellPart spellPart : caster.getSpell(slot).recipe()) {
                var allTimelines = ParticleTimelineRegistry.PARTICLE_TIMELINE_REGISTRY.entrySet();
                for (var entry : allTimelines) {
                    if (entry.getValue().getSpellPart() == spellPart) {
                        selectedTimeline = entry.getValue();
                        break;
                    }
                }
            }
            if (selectedTimeline == null) {
                selectedTimeline = ParticleTimelineRegistry.PROJECTILE_TIMELINE.get();
            }
        }else{
            selectedTimeline = LAST_SELECTED_PART;
        }
    }

    public static void openScreen(ItemStack stack, int slot, InteractionHand stackHand) {
        AbstractCaster<?> caster = SpellCasterRegistry.from(stack);
        int hash = caster.getSpell(slot).particleTimeline().hashCode();
        if(ParticleOverviewScreen.lastOpenedHash != hash || ParticleOverviewScreen.lastScreen == null){
            LAST_SELECTED_PART = null;
            ParticleOverviewScreen.lastOpenedHash = hash;
            Minecraft.getInstance().setScreen(new ParticleOverviewScreen(caster, slot, stackHand));
        }else{
            ParticleOverviewScreen.lastScreen.slot = slot;
            Minecraft.getInstance().setScreen(ParticleOverviewScreen.lastScreen);
        }
    }


    @Override
    public void onClose() {
        super.onClose();
        int hash = timelineMap.immutable().hashCode();
        ParticleOverviewScreen.lastOpenedHash = hash;
        ParticleOverviewScreen.lastScreen = this;
    }

    @Override
    public void removed() {
        super.removed();
        int hash = timelineMap.immutable().hashCode();
        ParticleOverviewScreen.lastOpenedHash = hash;
        ParticleOverviewScreen.lastScreen = this;
    }

    @Override
    public void init() {
        super.init();

        addSaveButton((b) -> Networking.sendToServer(new PacketUpdateParticleTimeline(slot, timelineMap.immutable(), this.stackHand == InteractionHand.MAIN_HAND)));
        timelineButton = addRenderableWidget(new DocEntryButton(bookLeft + LEFT_PAGE_OFFSET, bookTop + 36, selectedTimeline.getSpellPart().glyphItem.getDefaultInstance(), Component.translatable(selectedTimeline.getSpellPart().getLocaleName()), (button) -> {
            addTimelineSelectionWidgets();
            setSelectedButton(timelineButton);
        }));
        if(currentlySelectedButton == null){
            setSelectedButton(timelineButton);
        }
        if(selectedProperty == null) {
            addTimelineSelectionWidgets();
        }else{
            onPropertySelected(selectedProperty);
        }
        initLeftSideButtons();
//        virtualLevel = new VirtualLevel(Minecraft.getInstance().level.registryAccess(), true, level -> {
//            level.refreshBlockEntityModels();
//            var bakedLevel = LevelBakery.bakeVertices(level, bounds, new Vector3f());
//            updateScene(bakedLevel);
//        });
//        virtualLevel.setBounds(new AABB(BlockPos.ZERO).inflate(60));
//        for(BlockPos pos : BlockPos.withinManhattan(BlockPos.ZERO, 5, 5, 5)) {
//            virtualLevel.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
//        }

//        Vec3 pos = Minecraft.getInstance().player.position;
//        var bakedLevel = LevelBakery.bakeVertices(Minecraft.getInstance().level, new AABB(BlockPos.containing(pos)).inflate(5), new Vector3f());
//        this.updateScene(bakedLevel);
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

        if(propertyWidgetProvider != null && GuiHelpers.isMouseInRelativeRange((int) pMouseX, (int) pMouseY, propertyWidgetProvider.x,
                propertyWidgetProvider.y, propertyWidgetProvider.width, propertyWidgetProvider.height)){
            if(propertyWidgetProvider.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY)){
                return true;
            }
        }
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
            SelectedParticleButton widget = new SelectedParticleButton(bookLeft + RIGHT_PAGE_OFFSET + 10 + entryCount * 20, bookTop + 40, 14, 14, type.getIconLocation(), (button) -> {
                timelineOption.entry().setMotion(type.create());
                initLeftSideButtons();
                if(selectedParticleButton != null){
                    selectedParticleButton.selected = false;
                }
                selectedParticleButton = (SelectedParticleButton) button;
                selectedParticleButton.selected = true;
            });
            widget.withTooltip(type.getName());
            if(timelineOption.entry().motion().getType() == type){
                widget.selected = true;
                selectedParticleButton = widget;
            }
            addRightPageWidget(widget);
            entryCount++;
        }
    }

    public void initLeftSideButtons() {
        clearList(leftPageWidgets);
        IParticleTimeline<?> timeline = timelineMap.getOrCreate(selectedTimeline);
        List<AbstractWidget> widgets = new ArrayList<>();
        widgets.addAll(getPropButtons(timeline.getProperties(), new ArrayList<>(), 0));

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
            addLeftPageWidget(new GuiImageButton(bookLeft + LEFT_PAGE_OFFSET + 87, bookBottom - 30, DocAssets.BUTTON_UP, (button) -> {
                rowOffset = Math.max(rowOffset - 1, 0);
                initLeftSideButtons();
            }).withHoverImage(DocAssets.BUTTON_UP_HOVER));
        }

        if(hasMoreElements){
            addLeftPageWidget(new GuiImageButton(bookLeft + LEFT_PAGE_OFFSET + 103, bookBottom - 30, DocAssets.BUTTON_DOWN, (button) -> {
                rowOffset = rowOffset + 1;
                initLeftSideButtons();
            }).withHoverImage(DocAssets.BUTTON_DOWN_HOVER));
        }
    }

    public List<PropertyButton> getPropButtons(List<BaseProperty<?>> props, List<PropertyButton> buttons, int depth){
        if(depth > 3) {
            return buttons;
        }
        for (BaseProperty property : props) {
            property.setChangedListener(this::initLeftSideButtons);
            PropertyButton propertyButton = buildPropertyButton(property, buttons.size(), depth);
            buttons.add(propertyButton);
            if(property instanceof Property parentProp) {
                getPropButtons(parentProp.subProperties(), buttons, depth + 1);
            }
        }
        return buttons;
    }

    public PropertyButton buildPropertyButton(BaseProperty property, int yOffset, int nestLevel) {
        DocAssets.BlitInfo texture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON;
        DocAssets.BlitInfo selectedTexture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON_SELECTED;
        int xOffset = 26;
        switch (nestLevel){
            case 0 ->{
                texture = DocAssets.NESTED_ENTRY_BUTTON;
                selectedTexture = DocAssets.NESTED_ENTRY_BUTTON_SELECTED;
                xOffset = 13;
            }
            case 1 -> {
                texture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON;
                selectedTexture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON_SELECTED;
                xOffset = 26;
            }
            case 2 -> {
                texture = DocAssets.TRIPLE_NESTED_ENTRY_BUTTON;
                selectedTexture = DocAssets.TRIPLE_NESTED_ENTRY_BUTTON_SELECTED;
                xOffset = 39;
            }
            default -> {
            }
        }
        var widgetProvider = property.buildWidgets(bookLeft + RIGHT_PAGE_OFFSET, bookTop + PAGE_TOP_OFFSET, ONE_PAGE_WIDTH, ONE_PAGE_HEIGHT);
        return new PropertyButton(bookLeft + LEFT_PAGE_OFFSET + xOffset, bookTop + 51 + 15 * (yOffset), texture, selectedTexture, widgetProvider, (button) -> {
            onPropertySelected(property);
            if(button instanceof PropertyButton propertyButton){
                propertyButton.widgetProvider = propertyWidgetProvider;
                setSelectedButton(propertyButton);
            }
            selectedProperty = property;
        });
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
            var widget = new GlyphButton(bookLeft + RIGHT_PAGE_OFFSET + 2 + 20 * (i % 6), bookTop + 40 + 20*(i/6), entry.getValue().getSpellPart(), (button) -> {
                selectedTimeline = entry.getValue();
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
