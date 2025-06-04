package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ListParticleWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.documentation.DocEntryButton;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.nuggets.client.rendering.RenderHelpers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class RuneTextureProperty extends BaseProperty<RuneTextureProperty>{
    public static final List<RuneTexture> TEXTURES = new CopyOnWriteArrayList<>();

    public RuneTexture runeTexture;

    public static MapCodec<RuneTextureProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("texture").forGetter(i -> i.runeTexture.pattern)
    ).apply(instance, RuneTextureProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, RuneTextureProperty> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            (i) -> i.runeTexture.pattern,
            RuneTextureProperty::new
    );
    static {
        TEXTURES.add(new RuneTexture(() -> ItemsRegistry.RUNIC_CHALK.asItem().getDefaultInstance(), "rune"));
        TEXTURES.add(new RuneTexture(() -> ItemsRegistry.ABJURATION_ESSENCE.asItem().getDefaultInstance(), "rune_abjuration"));
        TEXTURES.add(new RuneTexture(() -> ItemsRegistry.CONJURATION_ESSENCE.asItem().getDefaultInstance(), "rune_conjuration"));
        TEXTURES.add(new RuneTexture(() -> ItemsRegistry.AIR_ESSENCE.asItem().getDefaultInstance(), "rune_air"));
        TEXTURES.add(new RuneTexture(() -> ItemsRegistry.EARTH_ESSENCE.asItem().getDefaultInstance(), "rune_earth"));
        TEXTURES.add(new RuneTexture(() -> ItemsRegistry.FIRE_ESSENCE.asItem().getDefaultInstance(), "rune_fire"));
        TEXTURES.add(new RuneTexture(() -> ItemsRegistry.WATER_ESSENCE.asItem().getDefaultInstance(), "rune_water"));
        TEXTURES.add(new RuneTexture(() -> ItemsRegistry.MANIPULATION_ESSENCE.asItem().getDefaultInstance(), "rune_manipulation"));
    }

    public RuneTextureProperty(String string){
        super(new PropMap());
        this.runeTexture = TEXTURES.stream()
                .filter(r -> r.pattern.equals(string))
                .findFirst()
                .orElse(TEXTURES.get(0));
    }

    public RuneTextureProperty(){
        super(new PropMap());
        this.runeTexture = TEXTURES.get(0);
    }

    public RuneTextureProperty(PropMap propMap){
        super(propMap);
        this.runeTexture = propMap.getOrDefault(ParticlePropertyRegistry.RUNE_PROPERTY.get(), new RuneTextureProperty()).runeTexture;
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < TEXTURES.size(); i++) {
            RuneTexture texture = TEXTURES.get(i);
            boolean isDefault = texture.renderStack.get().is(ItemsRegistry.RUNIC_CHALK.asItem());
            DocEntryButton button = new DocEntryButton(x, y + 20 + 15 * i, texture.renderStack.get(), isDefault ? Component.translatable("ars_nouveau.default") : texture.renderStack.get().getHoverName(), (b) -> {
                runeTexture = texture;
                writeChanges();
                onDependenciesChanged.run();
            });
            buttons.add(button);
        }


        return new ListParticleWidgetProvider(x, y, width, height, buttons) {
            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                RenderHelpers.drawItemAsIcon(runeTexture.renderStack.get(), graphics, x - 1, y - 1, 10, false);
            }

            @Override
            public Component getButtonTitle() {
                if(runeTexture == null || runeTexture.renderStack.get().is(ItemsRegistry.RUNIC_CHALK.asItem())) {
                    return Component.literal(getName().getString() + ": " +  Component.translatable("ars_nouveau.default").getString());
                }
                return Component.literal(getName().getString() + ": " + runeTexture.renderStack.get().getHoverName().getString());
            }
        };
    }

    @Override
    public IPropertyType<RuneTextureProperty> getType() {
        return ParticlePropertyRegistry.RUNE_PROPERTY.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RuneTextureProperty that = (RuneTextureProperty) o;
        return Objects.equals(runeTexture, that.runeTexture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), runeTexture);
    }

    public record RuneTexture(Supplier<ItemStack> renderStack, String pattern) {

        public RuneTexture(ItemStack renderStack, String pattern) {
            this(() -> renderStack, pattern);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            RuneTexture that = (RuneTexture) o;
            return Objects.equals(pattern, that.pattern);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(pattern);
        }
    }
}
