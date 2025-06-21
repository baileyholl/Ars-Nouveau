package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectedParticleButton;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class ModelProperty extends BaseProperty<ModelProperty> {

    public static MapCodec<ModelProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("resource").forGetter(i -> i.selectedResource.resourceLocation),
            PropMap.CODEC.fieldOf("subPropMap").forGetter(i -> i.subPropMap)
    ).apply(instance, ModelProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, ModelProperty> STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, i -> i.selectedResource.resourceLocation,
            PropMap.STREAM_CODEC, i -> i.subPropMap, ModelProperty::new);

    public Model selectedResource;
    public PropMap subPropMap;

    public static final Model NONE = new Model(ArsNouveau.prefix("empty"), DocAssets.STYLE_ICON_NONE, false);
    public static final Model CUBE_BODY = new Model(ArsNouveau.prefix("cube"), DocAssets.STYLE_ICON_BLOCK, true, (spell) -> {
        return ArsNouveau.prefix("textures/particle/" + "projectile_" + (spell.age / 5) % 4 + ".png");
    });

    public static final List<Model> resources = new CopyOnWriteArrayList<>();

    static {
        resources.add(NONE);
        resources.add(CUBE_BODY);
    }

    public ModelProperty(PropMap propMap) {
        super(propMap);
        if (propMap.has(ParticlePropertyRegistry.MODEL_PROPERTY.get())) {
            ModelProperty modelProperty = propMap.get(ParticlePropertyRegistry.MODEL_PROPERTY.get());
            selectedResource = modelProperty.selectedResource;
            subPropMap = propMap.get(ParticlePropertyRegistry.MODEL_PROPERTY.get()).subPropMap;
        } else {
            selectedResource = NONE;
            subPropMap = new PropMap();
        }

    }

    public ModelProperty() {
        selectedResource = NONE;
        subPropMap = new PropMap();
    }

    public ModelProperty(ResourceLocation resourceLocation, PropMap subPropMap) {
        this(new PropMap());
        this.selectedResource = resources.stream().filter(res -> res.resourceLocation.equals(resourceLocation)).findFirst().orElse(NONE);
        this.subPropMap = subPropMap;
    }


    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        ModelProperty self = this;
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            SelectedParticleButton selectedButton;

            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                for (int i = 0; i < resources.size(); i++) {
                    var resource = resources.get(i);
                    SelectedParticleButton selectedParticleButton = new SelectedParticleButton(x + 6 + 16 * (i % 7), y + 20 + 20 * (i / 7), 14, 14, getImagePath(resource), (b) -> {
                        var didHaveColor = selectedResource.supportsColor;
                        selectedResource = resource;
                        var nowHasColor = selectedResource.supportsColor;

                        propertyHolder.set(getType(), self);
                        if (didHaveColor != nowHasColor && onDependenciesChanged != null) {
                            onDependenciesChanged.run();
                        }
                        selectedButton.selected = false;
                        if (b instanceof SelectedParticleButton selectedParticleButton1) {
                            selectedParticleButton1.selected = true;
                            selectedButton = selectedParticleButton1;
                        }
                    });
                    selectedParticleButton.withTooltip(getTypeName(resource.resourceLocation));
                    widgets.add(selectedParticleButton);
                    if (selectedResource == resource) {
                        selectedButton = selectedParticleButton;
                        selectedButton.selected = true;
                    }
                }
            }

            private ResourceLocation getImagePath(Model location) {
                return location.blitInfo.location();
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
                return Component.literal(getName().getString() + ": " + getTypeName(selectedResource.resourceLocation).getString());
            }

            @Override
            public void getButtonTooltips(List<Component> tooltip) {
                tooltip.add(Component.translatable("ars_nouveau.model_tooltip"));
            }
        };
    }

    @Override
    public List<BaseProperty<?>> subProperties() {
        if (selectedResource.supportsColor) {
            return List.of(subPropMap.createIfMissing(new ColorProperty()));
        } else {
            return List.of();
        }
    }

    @Override
    public IPropertyType<ModelProperty> getType() {
        return ParticlePropertyRegistry.MODEL_PROPERTY.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ModelProperty that = (ModelProperty) o;
        return Objects.equals(selectedResource, that.selectedResource) && Objects.equals(subPropMap, that.subPropMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedResource, subPropMap);
    }

    public record Model(ResourceLocation resourceLocation, DocAssets.BlitInfo blitInfo, boolean supportsColor,
                        Function<EntityProjectileSpell, ResourceLocation> getTexture) {
        public Model(ResourceLocation resourceLocation, DocAssets.BlitInfo blitInfo, boolean supportsColor) {
            this(resourceLocation, blitInfo, supportsColor, spell -> ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), "textures/entity/" + resourceLocation.getPath() + ".png"));
        }


        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Model model = (Model) o;
            return Objects.equals(resourceLocation, model.resourceLocation);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(resourceLocation);
        }
    }
}
