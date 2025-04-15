package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.ParticleEmitter;
import com.hollingsworth.arsnouveau.api.registry.ParticleConfigRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.List;

public interface IParticleConfig {
    Codec<IParticleConfig> CODEC = ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY.byNameCodec().dispatch(IParticleConfig::getType, IParticleConfigType::codec);

    StreamCodec<RegistryFriendlyByteBuf, IParticleConfig> STREAM_CODEC = ByteBufCodecs.registry(ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY_KEY).dispatch(IParticleConfig::getType, IParticleConfigType::streamCodec);

    IParticleConfigType<?> getType();

    void init(ParticleEmitter emitter);

    void tick(Level level, double x, double y, double z, double prevX, double prevY, double prevZ);

    default Component getName(){
        ResourceLocation key = ParticleConfigRegistry.PARTICLE_CONFIG_REGISTRY.getKey(this.getType());
        return Component.translatable(key.getNamespace() + ".particle_config." + key.getPath() + ".name");
    }

    default ParticleConfigWidgetProvider getWidget(){
        return new ParticleConfigWidgetProvider(this) {
            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

            }

            @Override
            public void addWidgets(List<AbstractWidget> list, int x, int y, int width, int height) {

            }
        };
    }
}
