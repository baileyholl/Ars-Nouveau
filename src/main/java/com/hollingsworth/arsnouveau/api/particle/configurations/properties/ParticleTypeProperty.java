package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.SelectedParticleButton;
import com.hollingsworth.arsnouveau.client.gui.documentation.DocEntryButton;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ParticleTypeProperty extends Property<ParticleTypeProperty> {
    public static final Map<ParticleType<? extends PropertyParticleOptions>, ParticleData> PARTICLE_TYPES = new ConcurrentHashMap<>();

    public static void addType(ParticleData data) {
        PARTICLE_TYPES.put(data.type, data);
    }


    public static MapCodec<ParticleTypeProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.PARTICLE_TYPE.byNameCodec().fieldOf("particleType").forGetter(i -> i.type),
            PropMap.CODEC.fieldOf("subProperties").forGetter(i -> i.subProperties)
    ).apply(instance, ParticleTypeProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, ParticleTypeProperty> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(BuiltInRegistries.PARTICLE_TYPE.key()),
            ParticleTypeProperty::type,
            PropMap.STREAM_CODEC,
            (i) -> i.subProperties,
            ParticleTypeProperty::new
    );

    protected ParticleData selectedData;
    protected PropMap subProperties;
    protected ParticleType<? extends PropertyParticleOptions> type;

    public ParticleTypeProperty(ParticleType<?> type, PropMap subProperties) {
        super();
        this.subProperties = subProperties;;
        this.type = (ParticleType<? extends PropertyParticleOptions>) type;
        selectedData = PARTICLE_TYPES.get(type);
        if (selectedData == null) {
            System.out.println("UNREGISTERED PARTICLE TYPE FOR " + type);
            selectedData = new ParticleData(ModParticles.NEW_GLOW_TYPE.get(), false);
        }
    }

    public ParticleTypeProperty(PropMap propMap) {
        super(propMap);
        ParticleTypeProperty property = propMap.getOrDefault(getType(), new ParticleTypeProperty(ModParticles.NEW_GLOW_TYPE.get(), new PropMap()));
        this.type = property.type;
        this.subProperties = property.subProperties;
        selectedData = PARTICLE_TYPES.get(type);
        if (selectedData == null) {
            System.out.println("UNREGISTERED PARTICLE TYPE FOR " + type);
            selectedData = new ParticleData(ModParticles.NEW_GLOW_TYPE.get(), false);
        }
    }

    public ParticleType<? extends PropertyParticleOptions> type() {
        return type;
    }

    public ColorProperty getColor(){
        return subProperties.getOrDefault(ParticlePropertyRegistry.COLOR_PROPERTY.get(), new ColorProperty(new PropMap()));
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        ParticleTypeProperty self = this;
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            SelectedParticleButton selectedParticleButton;
            int pageOffset;
            int maxEntries;
            List<DocEntryButton> buttons = new ArrayList<>();
            ArrayList<Map.Entry<ParticleType<? extends PropertyParticleOptions>, ParticleTypeProperty.ParticleData>> particleEntries;
            GuiImageButton upButton;
            GuiImageButton downButton;

            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                maxEntries = PARTICLE_TYPES.size();
                particleEntries = new ArrayList<>(PARTICLE_TYPES.entrySet());
                particleEntries.sort((o1, o2) ->{
                    if(o1.getKey() == ModParticles.NEW_GLOW_TYPE.get()){
                        return -1;
                    }
                    return getTypeName(o1.getKey()).getString().compareTo(getTypeName(o2.getKey()).getString());
                });
                for (int i = 0; i < particleEntries.size(); i++) {
                    var particleType = particleEntries.get(i);
                    DocEntryButton button = new DocEntryButton(x + 6,  y + 20 + 15*i, ItemStack.EMPTY, getTypeName(particleType.getKey()), (b) -> {
                        var didHaveColor = selectedData.acceptsColor;
                        var wasLegacyRGB = selectedData.useLegacyRGB();
                        selectedData = particleType.getValue();
                        type = particleType.getKey();
                        var nowHasColor = selectedData.acceptsColor;
                        var useLegacyRGB = selectedData.useLegacyRGB();
                        propertyHolder.set(getType(), self);
                        if ((useLegacyRGB != wasLegacyRGB || didHaveColor != nowHasColor) && onDependenciesChanged != null) {
                            onDependenciesChanged.run();
                        }

//                        selectedParticleButton.selected = false;
//                        if(b instanceof SelectedParticleButton selectedParticleButton1){
//                            selectedParticleButton1.selected = true;
//                            selectedParticleButton = selectedParticleButton1;
//                        }
                    });
                    buttons.add(button);
                    widgets.add(button);
                }

                upButton = new GuiImageButton(x + 80, y + height - 5, DocAssets.BUTTON_UP, (button) -> {
                    onScroll(-1);
                }).withHoverImage(DocAssets.BUTTON_UP_HOVER);

                downButton = new GuiImageButton(x + 100, y + height - 5, DocAssets.BUTTON_DOWN, (button) -> {
                    onScroll(1);
                }).withHoverImage(DocAssets.BUTTON_DOWN_HOVER);

                widgets.add(upButton);
                widgets.add(downButton);
                onScroll(pageOffset);
            }

            @Override
            public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
                if(GuiHelpers.isMouseInRelativeRange((int) pMouseX, (int) pMouseY, x, y, width, height)) {
                    SoundManager manager = Minecraft.getInstance().getSoundManager();
                    if (pScrollY < 0) {
                        onScroll(1);
                        manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
                    } else if (pScrollY > 0) {
                        onScroll(-1);
                        manager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
                    }

                    return true;
                }
                return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
            }

            public void onScroll(int offset) {
                pageOffset += offset;
                pageOffset = Math.max(0, Math.min(pageOffset, maxEntries - 8));
                if (selectedParticleButton != null) {
                    selectedParticleButton.selected = false;
                }

                for(var button : buttons){
                    button.active = false;
                    button.visible = false;
                }
                var sublist = buttons.subList(pageOffset, Math.min(particleEntries.size(), pageOffset + 8));
                for (int i = 0; i < sublist.size(); i++) {
                    int x = this.x + 6;
                    int y = this.y + 20 + 15 * (i % 8);
                    DocEntryButton button = sublist.get(i);
                    button.visible = true;
                    button.active = true;
                    button.setPosition(x, y);
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
    public IPropertyType<ParticleTypeProperty> getType() {
        return ParticlePropertyRegistry.TYPE_PROPERTY.get();
    }

    @Override
    public List<SubProperty<?>> subProperties() {
        if (selectedData == null || !selectedData.acceptsColor) {
            return Collections.emptyList();
        }

        return List.of(new ColorProperty(subProperties, selectedData.useLegacyRGB()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParticleTypeProperty property = (ParticleTypeProperty) o;
        return Objects.equals(type, property.type) && Objects.equals(subProperties, property.subProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subProperties);
    }

    public record ParticleData(ParticleType<? extends PropertyParticleOptions> type, Supplier<PropertyParticleOptions> defaultOptions, boolean acceptsColor, boolean useLegacyRGB) {
        public ParticleData(ParticleType<? extends PropertyParticleOptions> type, boolean acceptsColor) {
            this(type, () -> new PropertyParticleOptions(type), acceptsColor, false);
        }

        public ParticleData(ParticleType<? extends PropertyParticleOptions> type, boolean acceptsColor, boolean useLegacyRGB) {
            this(type, () -> new PropertyParticleOptions(type), acceptsColor, useLegacyRGB);
        }
    }
}
