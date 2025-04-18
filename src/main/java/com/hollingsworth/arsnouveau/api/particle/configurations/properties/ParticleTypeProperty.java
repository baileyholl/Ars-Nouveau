package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ParticleTypeProperty extends Property{
    public static final Map<ParticleType<? extends ParticleOptions>, ParticleData> PARTICLE_TYPES = new ConcurrentHashMap<>();

    public static void addType(ParticleType<? extends ParticleOptions> type, ParticleData data) {
        PARTICLE_TYPES.put(type, data);
    }

    public static void addType(ParticleType<? extends ParticleOptions> type) {
        PARTICLE_TYPES.put(type, new ParticleTypeProperty.ParticleData(type, false));
    }


    protected ParticleData selectedData;

    public ParticleTypeProperty(PropertyHolder propertyHolder) {
        super(propertyHolder);
        this.selectedData = PARTICLE_TYPES.get(propertyHolder.defaultType);
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

                    ResourceLocation location = BuiltInRegistries.PARTICLE_TYPE.getKey(particleType.getKey());

                    widgets.add(new GuiImageButton(x + 24 * count, y + 20, 16, 16, ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "textures/particle_config/" + location.getPath()), (b) ->{
                        System.out.println(location);
                        selectedData = particleType.getValue();
                        propertyHolder.onTextureChanged.accept(particleType.getKey());
                    }).withTooltip(Component.translatable(location.getNamespace() + ".particle." + location.getPath())));
                    count++;
                }
            }
        };
    }

    @Override
    public List<Property> subProperties() {
        if(selectedData == null || !selectedData.acceptsColor) {
            return Collections.emptyList();
        }

        return List.of(new ColorProperty(propertyHolder));
    }

    public record ParticleData(ParticleType<?> type, Supplier<ParticleOptions> defaultOptions, boolean acceptsColor){
        public ParticleData(ParticleType<?> type, boolean acceptsColor){
            this(type, () -> new PropertyParticleOptions(type), acceptsColor);
        }
    }
}
