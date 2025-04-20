package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ParticleTypeProperty extends Property {
    public static final Map<ParticleType<? extends PropertyParticleOptions>, ParticleData> PARTICLE_TYPES = new ConcurrentHashMap<>();

    public static void addType(ParticleType<? extends PropertyParticleOptions> type, ParticleData data) {
        PARTICLE_TYPES.put(type, data);
    }

    public static void addType(ParticleType<? extends PropertyParticleOptions> type) {
        PARTICLE_TYPES.put(type, new ParticleTypeProperty.ParticleData(type, false));
    }


    protected ParticleData selectedData;

    public ParticleTypeProperty(PropertyHolder propertyHolder) {
        super(propertyHolder);
        this.selectedData = PARTICLE_TYPES.get(propertyHolder.defaultType);
        if (selectedData == null) {
            System.out.println("UNREGISTERED PARTICLE TYPE FOR " + propertyHolder.defaultType);
            selectedData = new ParticleData(propertyHolder.defaultType, false);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ArsNouveau.prefix("particle_type");
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
                var particleEntries = PARTICLE_TYPES.entrySet();
                int count = 0;
                for (var particleType : particleEntries) {
                    widgets.add(new GuiImageButton(x + 24 * count, y + 20, 16, 16, getImagePath(particleType.getKey()), (b) -> {
                        var didHaveColor = selectedData.acceptsColor;
                        selectedData = particleType.getValue();
                        var nowHasColor = selectedData.acceptsColor;
                        propertyHolder.onTextureChanged.accept(particleType.getKey());
                        if(didHaveColor != nowHasColor && onDependenciesChanged != null){
                            onDependenciesChanged.run();
                        }
                    }).withTooltip(getTypeName(particleType.getKey())));
                    count++;
                }
            }

            private ResourceLocation getImagePath(ParticleType<?> type) {
                ResourceLocation location = getKey(type);
                return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "textures/particle_config/" + location.getPath() + ".png");
            }

            private ResourceLocation getKey(ParticleType<?> type) {
                return BuiltInRegistries.PARTICLE_TYPE.getKey(type);
            }

            private Component getTypeName(ParticleType<?> type) {
                ResourceLocation location = getKey(type);
                return Component.translatable(location.getNamespace() + ".particle." + location.getPath());
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                graphics.blit(getImagePath(selectedData.type()), x, y, 0, 0, 14, 14, 14, 14);
            }

            @Override
            public Component getButtonTitle() {
                return Component.literal(getName().getString() + ": " + getTypeName(selectedData.type()).getString());
            }
        };
    }

    @Override
    public List<SubProperty> subProperties() {
        if (selectedData == null || !selectedData.acceptsColor) {
            return Collections.emptyList();
        }

        return List.of(new ColorProperty(propertyHolder));
    }

    public record ParticleData(ParticleType<?> type, Supplier<PropertyParticleOptions> defaultOptions, boolean acceptsColor) {
        public ParticleData(ParticleType<?> type, boolean acceptsColor) {
            this(type, () -> new PropertyParticleOptions(type), acceptsColor);
        }
    }
}
