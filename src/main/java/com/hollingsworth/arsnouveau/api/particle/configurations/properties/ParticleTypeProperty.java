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
        this.subProperties = subProperties;
        this.type = (ParticleType<? extends PropertyParticleOptions>) type;
        selectedData = PARTICLE_TYPES.get(type);
        if (selectedData == null) {
            System.out.println("UNREGISTERED PARTICLE TYPE FOR " + type);
            System.out.println(BuiltInRegistries.PARTICLE_TYPE.getKey(type));
            selectedData = PARTICLE_TYPES.get(ModParticles.NEW_GLOW_TYPE.get());
        }
    }

    public ParticleTypeProperty(PropMap propMap) {
        super(propMap);
        ParticleTypeProperty property = propMap.getOrDefault(getType(), new ParticleTypeProperty(ModParticles.NEW_GLOW_TYPE.get(), new PropMap()));
        this.type = property.type;
        this.subProperties = property.subProperties;
        selectedData = PARTICLE_TYPES.get(type);
        if (selectedData == null) {
            selectedData = PARTICLE_TYPES.get(ModParticles.NEW_GLOW_TYPE.get());
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
                for (Map.Entry<ParticleType<? extends PropertyParticleOptions>, ParticleData> particleType : particleEntries) {
                    DocEntryButton button = new DocEntryButton(0, 0, ItemStack.EMPTY, getTypeName(particleType.getKey()), (b) -> {
                        selectedData = particleType.getValue();
                        type = particleType.getKey();
                        propertyHolder.set(getType(), self);
                        onDependenciesChanged.run();
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

            public void onScroll(int offset) {
                pageOffset += offset;
                pageOffset = Math.max(0, Math.min(pageOffset, maxEntries - 8));
                if (selectedParticleButton != null) {
                    selectedParticleButton.selected = false;
                }

                for(var button : buttons){
                    button.active = false;
                    button.visible = false;
                    button.setPosition(-100, -100);
                }
                var sublist = buttons.subList(pageOffset, Math.min(particleEntries.size(), pageOffset + 8));
                for (int i = 0; i < sublist.size(); i++) {
                    int x = this.x;
                    int y = this.y + 20 + 15 * (i % 8);
                    DocEntryButton button = sublist.get(i);
                    button.visible = true;
                    button.active = true;
                    button.setPosition(x, y);
                }

                boolean hasMore = pageOffset + 8 < maxEntries;
                boolean hasFewer = pageOffset > 0;
                upButton.visible = hasFewer;
                upButton.active = hasFewer;
                downButton.active = hasMore;
                downButton.visible = hasMore;
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
                DocClientUtils.blit(graphics, DocAssets.PARTICLES_ICON, x, y);
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
