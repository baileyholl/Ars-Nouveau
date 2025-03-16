package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.DocPlayerData;
import com.hollingsworth.arsnouveau.api.particle.configurations.ListParticleWidgetProvider;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellSoundRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.client.gui.HorizontalSlider;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.documentation.DocEntryButton;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Consumer;

public class SoundProperty extends BaseProperty<SoundProperty> {

    public ConfiguredSpellSound sound;

    public static MapCodec<SoundProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ConfiguredSpellSound.CODEC.fieldOf("sound").forGetter(i -> i.sound)
    ).apply(instance, SoundProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, SoundProperty> STREAM_CODEC = StreamCodec.composite(
            ConfiguredSpellSound.STREAM,
            (i) -> i.sound,
            SoundProperty::new
    );

    public SoundProperty(ConfiguredSpellSound sound) {
        super();
        this.sound = sound;
    }

    public SoundProperty() {
        this(ConfiguredSpellSound.DEFAULT);
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        List<DocEntryButton> buttons = new ArrayList<>();
        List<SpellSound> spellSounds = SpellSoundRegistry.SPELL_SOUNDS.stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        spellSounds.sort(Comparator.<SpellSound>comparingInt(o -> DocPlayerData.favoriteSounds.contains(o) ? -1 : 1).thenComparingInt(SpellSound::sortNum).thenComparing(o -> o.getSoundName().getString().toLowerCase(Locale.ROOT)));
        for (SpellSound spellSound : spellSounds) {
            DocEntryButton button = new DocEntryButton(0, 0, ItemStack.EMPTY, spellSound.getSoundName(), (b) -> {
                this.sound = new ConfiguredSpellSound(spellSound, sound.getVolume(), sound.getPitch());
            }).setFavoritable(() -> DocPlayerData.favoriteSounds.contains(spellSound), (b) -> {
                if (DocPlayerData.favoriteSounds.contains(spellSound)) {
                    DocPlayerData.favoriteSounds.remove(spellSound);
                } else {
                    DocPlayerData.favoriteSounds.add(spellSound);
                }
            });
            button.onClickFunction = ((xPos, yPos, buttonNum) -> {
                if (button.active && button.visible && buttonNum == 1) {
                    playTestSound(new ConfiguredSpellSound(spellSound, sound.getVolume(), sound.getPitch()));
                    return true;
                }
                return false;
            });
            button.withTooltip(Component.translatable("ars_nouveau.right_click_sound"));
            buttons.add(button);
        }

        return new ListParticleWidgetProvider(x, y, width, height, buttons, 4, () -> providerData) {
            HorizontalSlider volumeSlider;
            HorizontalSlider pitchSlider;

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {
                super.addWidgets(widgets);

                int yOffset = 103;
                int xSliderOffset = x + 4;
                volumeSlider = buildSlider(xSliderOffset, y + yOffset, 5, 200, 5, 1, Component.translatable("ars_nouveau.xzmaxspeed_slider"), Component.empty(), 0.0, (value) -> {
                    sound = new ConfiguredSpellSound(sound.getSound(), (float) (volumeSlider.getValue() / 100f), sound.getPitch());
                });
                yOffset += 25;
                pitchSlider = buildSlider(xSliderOffset, y + yOffset, 5, 200, 5, 1, Component.translatable("ars_nouveau.xzmaxspeed_slider"), Component.empty(), 0.0, (value) -> {
                    sound = new ConfiguredSpellSound(sound.getSound(), sound.getVolume(), (float) pitchSlider.getValue() / 100f);
                });
                yOffset += 10;
                GuiImageButton testButton = new GuiImageButton(xSliderOffset, y + yOffset, 37, 12, ArsNouveau.prefix("textures/gui/sound_test_icon.png"), (b) -> {
                    playTestSound(sound);
                });
                testButton.soundDisabled = true;
                volumeSlider.setValue(sound.getVolume() * 100f);
                pitchSlider.setValue(sound.getPitch() * 100f);
                widgets.add(volumeSlider);
                widgets.add(pitchSlider);
                widgets.add(testButton);
            }

            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
                int yOffset = 93;
                int sliderSpacing = 25;
                DocClientUtils.drawHeaderNoUnderline(Component.translatable("ars_nouveau.sounds.volume", volumeSlider.getValueString()), graphics, x, y + yOffset, width, mouseX, mouseY, partialTicks);

                yOffset += sliderSpacing;
                DocClientUtils.drawHeaderNoUnderline(Component.translatable("ars_nouveau.sounds.pitch", pitchSlider.getValueString()), graphics, x, y + yOffset, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void renderIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.blit(graphics, DocAssets.SOUND_ICON, x, y);
            }

            @Override
            public Component getButtonTitle() {
                return Component.literal(getName().getString() + ": " + sound.getSound().getSoundName().getString());
            }

            public HorizontalSlider buildSlider(int x, int y, double min, double max, double stepSize, int precision, Component prefix, Component suffix, double currentVal, Consumer<Double> onValueChange) {
                return new HorizontalSlider(x, y, DocAssets.SLIDER_BAR_FILLED, DocAssets.SLIDER, prefix, suffix, min, max, currentVal, stepSize, precision, false, onValueChange);
            }
        };
    }

    private void playTestSound(ConfiguredSpellSound sound) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        Vec3 pos = localPlayer.position().add(0, 2, 0);
        localPlayer.level.playLocalSound(pos.x(), pos.y(), pos.z(), sound.getSound().getSoundEvent().value(), SoundSource.PLAYERS, (float) sound.getVolume(), sound.getPitch(), false);
    }

    @Override
    public IPropertyType<SoundProperty> getType() {
        return ParticlePropertyRegistry.SOUND_PROPERTY.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SoundProperty that = (SoundProperty) o;
        return Objects.equals(sound, that.sound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sound);
    }
}
