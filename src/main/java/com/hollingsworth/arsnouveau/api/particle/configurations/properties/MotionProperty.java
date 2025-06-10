package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineEntryData;
import com.hollingsworth.arsnouveau.api.particle.timelines.TimelineOption;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectedParticleButton;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;

public class MotionProperty extends BaseProperty<MotionProperty>{
    private static MotionProperty instance = new MotionProperty();
    public static MapCodec<MotionProperty> CODEC = MapCodec.unit(instance);

    public static StreamCodec<RegistryFriendlyByteBuf, MotionProperty> STREAM_CODEC = StreamCodec.unit(instance);

    TimelineOption timelineOption;

    private MotionProperty(){
        timelineOption = new TimelineOption(ResourceLocation.fromNamespaceAndPath("ars_nouveau", "default"), new TimelineEntryData(), ImmutableList.of());
    }

    public MotionProperty(TimelineOption timelineOption){
        super(timelineOption.entry().particleOptions().map);
        this.timelineOption = timelineOption;
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {

                var options = timelineOption.options();
                for (int i = 0; i < options.size(); i++) {
                    IParticleMotionType<?> type = options.get(i);
                    SelectedParticleButton widget = new SelectedParticleButton(x + 5 + (i % 7) * 16, y + 20 + 18 * (i / 7), 14, 14, type.getIconLocation(), (button) -> {
                        timelineOption.entry().setMotion(type.create(timelineOption.entry().motion().propertyMap));
                        if (onDependenciesChanged != null) {
                            onDependenciesChanged.run();
                        }
                    });
                    widget.withTooltip(type.getName());
                    widgets.add(widget);
                }
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                graphics.blit(timelineOption.entry().motion().getType().getIconLocation(), x, y, 0, 0, 14 , 14, 14, 14, 14);
            }
            public Component timelineName(){
                ResourceLocation id = timelineOption.id();
                return Component.translatable(id.getNamespace() + ".timeline." + id.getPath());
            }
            @Override
            public Component getButtonTitle() {
                return Component.literal(timelineName().getString() + ": " + timelineOption.entry().motion().getType().getName().getString());
            }
        };
    }

    @Override
    public IPropertyType<MotionProperty> getType() {
        return ParticlePropertyRegistry.MOTION_PROPERTY.get();
    }

    @Override
    public List<BaseProperty<?>> subProperties() {
        return timelineOption.entry().motion().getProperties(propertyHolder);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MotionProperty that &&
                this.timelineOption.equals(that.timelineOption) &&
                this.propertyHolder.equals(that.propertyHolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timelineOption, propertyHolder);
    }
}
