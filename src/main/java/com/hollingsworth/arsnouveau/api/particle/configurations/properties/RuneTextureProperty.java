package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ListParticleWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.client.gui.documentation.DocEntryButton;
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

public class RuneTextureProperty extends BaseProperty<RuneTextureProperty>{
    public static final List<String> TEXTURES = new CopyOnWriteArrayList<>();

    public String runeTexture;

    public static MapCodec<RuneTextureProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("texture").forGetter(i -> i.runeTexture)
    ).apply(instance, RuneTextureProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, RuneTextureProperty> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            (i) -> i.runeTexture,
            RuneTextureProperty::new
    );
    static {
        TEXTURES.add("rune");
        TEXTURES.add("rune_abjuration");
        TEXTURES.add("rune_conjuration");
        TEXTURES.add("rune_air");
        TEXTURES.add("rune_earth");
        TEXTURES.add("rune_fire");
        TEXTURES.add( "rune_water");
        TEXTURES.add("rune_manipulation");
    }

    public RuneTextureProperty(String string){
        super(new PropMap());
        this.runeTexture = TEXTURES.stream()
                .filter(r -> r.equals(string))
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
            String texture = TEXTURES.get(i);
            DocEntryButton button = new DocEntryButton(x, y + 20 + 15 * i, ItemStack.EMPTY, getPatternName(texture), (b) -> {
                runeTexture = texture;
                writeChanges();
                onDependenciesChanged.run();
            }).withStaticIcon(new DocAssets.BlitInfo(ArsNouveau.prefix("textures/block/runes/" + texture + ".png"), 16, 16));
            buttons.add(button);
        }


        return new ListParticleWidgetProvider(x, y, width, height, buttons) {
            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                graphics.blit(ArsNouveau.prefix("textures/block/runes/" + runeTexture + ".png"), x + 1, y + 1, 0, 0, 12, 12, 12, 12);
            }

            @Override
            public Component getButtonTitle() {
                return Component.literal(getName().getString() + ": " + getPatternName(runeTexture).getString());
            }
        };
    }

    private Component getPatternName(String pattern){
        return Component.translatable("ars_nouveau.rune." + pattern);
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
}
