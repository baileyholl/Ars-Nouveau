package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModelProperty extends Property<ModelProperty>{

    public static MapCodec<ModelProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("resource").forGetter(i -> i.selectedResource)
    ).apply(instance, ModelProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, ModelProperty> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, i -> i.selectedResource, ModelProperty::new);

    public ResourceLocation selectedResource;

    public static final ResourceLocation NONE = ArsNouveau.prefix("empty");

    public static final List<ResourceLocation> resources = new CopyOnWriteArrayList<>();

    public ModelProperty(PropMap propMap){
        super(propMap);
        if(propMap.has(ParticlePropertyRegistry.MODEL_PROPERTY.get())){
            selectedResource = propMap.get(ParticlePropertyRegistry.MODEL_PROPERTY.get()).selectedResource;
        } else {
            selectedResource = NONE;
        }
    }

    public ModelProperty(ResourceLocation resourceLocation) {
        this(new PropMap());
        this.selectedResource = resourceLocation;
        if(!resources.contains(resourceLocation)){
            selectedResource = NONE;
        }
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
                for (int i = 0; i < resources.size(); i++) {
                    var particleType = resources.get(i);
                    widgets.add(new GuiImageButton(x + 6 + 16 * (i % 7), y + 20 + 20* (i / 7), 14, 14, getImagePath(selectedResource), (b) -> {
//                        var didHaveColor = selectedData.acceptsColor;
//                        selectedData = particleType.getValue();
//                        type = particleType.getKey();
//                        var nowHasColor = selectedData.acceptsColor;
//
//                        propertyHolder.set(getType(), self);
//                        if (didHaveColor != nowHasColor && onDependenciesChanged != null) {
//                            onDependenciesChanged.run();
//                        }
                    }).withTooltip(getTypeName(selectedResource)));
                }
            }

            private ResourceLocation getImagePath(ResourceLocation location) {
                return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "textures/entity/" + location.getPath() + ".png");
            }

            private Component getTypeName(ResourceLocation location) {
                return Component.translatable(location.getNamespace() + ".model." + location.getPath());
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                graphics.blit(getImagePath(selectedResource), x, y, 0, 0, 14, 14, 14, 14);
            }

            @Override
            public Component getButtonTitle() {
                return Component.literal(getName().getString() + ": " + getTypeName(selectedResource).getString());
            }
        };
    }

    @Override
    public IPropertyType<ModelProperty> getType() {
        return ParticlePropertyRegistry.MODEL_PROPERTY.get();
    }
}
